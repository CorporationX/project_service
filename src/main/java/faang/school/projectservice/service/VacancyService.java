package faang.school.projectservice.service;

import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.dto.VacancyFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.filter.VacancyFilter;
import faang.school.projectservice.validator.VacancyValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyService {

    private final VacancyRepository vacancyRepository;
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final CandidateRepository candidateRepository;
    private final VacancyValidator vacancyValidator;
    private final VacancyMapper vacancyMapper;
    private final List<VacancyFilter> vacancyFilters;

    @Transactional
    public VacancyDto create(VacancyDto vacancyDto) {
        vacancyValidator.validateSkills(vacancyDto);
        vacancyValidator.validateCreateVacancyStatus(vacancyDto);

        TeamMember curator = getCurator(vacancyDto);

        vacancyValidator.validateCuratorRole(curator);
        vacancyValidator.validateProject(vacancyDto, curator);

        List<Candidate> candidates = getCandidatesById(vacancyDto);

        createTeamMembers(candidates, curator.getTeam());

        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        vacancy.setCandidates(candidates);
        Vacancy savedVacancy = vacancyRepository.save(vacancy);

        setVacancyForCandidates(candidates, savedVacancy);

        return vacancyMapper.toDto(savedVacancy);
    }

    @Transactional
    public VacancyDto update(VacancyDto vacancyDto, Long id) {

        log.info("Check vacancy exists");
        Vacancy vacancy = vacancyRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Vacancy doesn't exist by id: %s", id)));

        log.info("Check curator exists");
        TeamMember curator = getCurator(vacancyDto);

        vacancyValidator.validateProject(vacancyDto, curator);

        List<Candidate> candidates = getCandidatesById(vacancyDto);

        log.info("Check candidates for team member in project");
        List<Candidate> newCandidates = candidates.stream()
                .filter(candidate -> teamMemberJpaRepository.findByUserIdAndProjectId(candidate.getUserId(), vacancyDto.getProjectId()) == null)
                .toList();

        if (!newCandidates.isEmpty()) {
            createTeamMembers(newCandidates, curator.getTeam());
            newCandidates.forEach(candidate -> vacancy.getCandidates().add(candidate));
            setVacancyForCandidates(newCandidates, vacancy);
        }

        vacancyMapper.update(vacancyDto, vacancy);
        vacancy.setId(id);

        Vacancy updatedVacancy = vacancyRepository.save(vacancy);

        vacancyValidator.validateUpdateVacancyStatus(vacancyDto.getStatus(), updatedVacancy);

        return vacancyMapper.toDto(updatedVacancy);
    }

    private TeamMember getCurator(VacancyDto vacancyDto) {
        return teamMemberJpaRepository.findById(vacancyDto.getCreatedBy()).orElseThrow(() ->
                new EntityNotFoundException(String.format("Team member doesn't exist by id: %s", vacancyDto.getCreatedBy())));
    }

    private void setVacancyForCandidates(List<Candidate> candidates, Vacancy savedVacancy) {
        candidates.forEach(candidate -> {
            if (candidate.getVacancy() != null && !Objects.equals(candidate.getVacancy().getId(), savedVacancy.getId())) {
                throw new DataValidationException(String.format("Candidate with ID: %s is already in another vacancy", candidate.getId()));
            }
            candidate.setVacancy(savedVacancy);
            candidateRepository.save(candidate);
        });
    }

    private List<Candidate> getCandidatesById(VacancyDto vacancyDto) {
        List<Candidate> candidates = new ArrayList<>();
        for (Long candidateId : vacancyDto.getCandidateIds()) {
            Candidate candidate = candidateRepository.findById(candidateId).orElseThrow(() ->
                    new EntityNotFoundException(String.format("Candidate doesn't exist by id: %s", candidateId)));
            candidates.add(candidate);
        }
        return candidates;
    }

    private void createTeamMembers(List<Candidate> candidates, Team team) {
        candidates.stream()
                .filter(candidate -> vacancyValidator.checkTeamMemberNotExists(candidate.getUserId(), team.getProject().getId()))
                .forEach(candidate -> {
                    TeamMember teamMember = TeamMember.builder()
                            .userId(candidate.getUserId())
                            .team(team)
                            .build();
                    teamMemberJpaRepository.save(teamMember);
                });
    }

    @Transactional
    public void delete(long id) {
        Vacancy vacancy = vacancyRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Vacancy with id " + id + " not found!"));

        for (Candidate candidate : vacancy.getCandidates()) {
            candidate.setVacancy(null);
            TeamMember teamMember = teamMemberJpaRepository.findByUserIdAndProjectId(candidate.getUserId(), vacancy.getProject().getId());
            if (teamMember.getRoles().isEmpty()) {
                teamMemberJpaRepository.deleteById(teamMember.getId());
            }
        }
        vacancyRepository.deleteById(vacancy.getId());
    }

    @Transactional(readOnly = true)
    public VacancyDto findById(long id) {
        Vacancy vacancy = vacancyRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Vacancy with id " + id + " not found!"));
        return vacancyMapper.toDto(vacancy);
    }

    @Transactional(readOnly = true)
    public List<VacancyDto> getAllByFilter(VacancyFilterDto filters) {
        Supplier<Stream<Vacancy>> vacancies = () -> vacancyRepository.findAll().stream();
        List<Vacancy> filteredVacancies = vacancyFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(vacancies, filters))
                .distinct()
                .toList();
        return vacancyMapper.toDtoList(filteredVacancies);
    }
}
