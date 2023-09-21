package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.Vacancy.*;
import faang.school.projectservice.exception.DataValidException;
import faang.school.projectservice.filter.vacancy.VacancyFilter;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.repository.VacancyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final CandidateRepository candidateRepository;
    private final VacancyMapper vacancyMapper;
    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final UserContext userContext;
    private final List<VacancyFilter> vacancyFilters;


    @Transactional
    public ExtendedVacancyDto create(CreateVacancyDto vacancyDto) {
        validateExistProjectInSystem(vacancyDto);
        validateStatusOfVacancyCreator(vacancyDto);
        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        vacancy.setStatus(VacancyStatus.OPEN);
        vacancy.setCandidates(new ArrayList<>());
        vacancy.setCreatedBy(userContext.getUserId());
        return saveEntity(vacancy);
    }

    @Transactional
    public ExtendedVacancyDto update(UpdateVacancyDto vacancyDto) {
        if (vacancyDto.getStatus().equals(VacancyStatus.CLOSED)) {
            validateAvailableCloseStatus(vacancyDto);
        }
        return vacancyRepository.findById(vacancyDto.getId())
                .map(vacancy -> {
                    vacancyMapper.updateFromDto(vacancyDto, vacancy);
                    vacancy.setUpdatedBy(userContext.getUserId());
                    return saveEntity(vacancy);
                })
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Vacancy with id %d does not exist.", vacancyDto.getId()))
                );
    }

    @Transactional
    public void changeCandidateStatus(UpdateCandidateRequestDto updateCandidate) {
        Vacancy vacancy = vacancyRepository.findById(updateCandidate.getVacancyId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Vacancy with id %d not found", updateCandidate.getVacancyId())
                ));
        Candidate candidate = candidateRepository.findById(updateCandidate.getCandidateId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Candidate with id %d not found", updateCandidate.getCandidateId())
                ));
        Long userId = candidate.getUserId();
        validateExistingCandidateInVacancy(updateCandidate, vacancy);
        candidate.setCandidateStatus(updateCandidate.getCandidateStatus());
        candidateRepository.save(candidate);
        if (updateCandidate.getCandidateStatus().equals(CandidateStatus.ACCEPTED)) {
            convertCandidateInTeamMember(updateCandidate, userId, vacancy);
        }
    }

    private ExtendedVacancyDto saveEntity(Vacancy vacancy) {
        vacancy = vacancyRepository.save(vacancy);
        return vacancyMapper.toDto(vacancy);
    }

    private void validateExistProjectInSystem(CreateVacancyDto vacancyDto) {
        if (!projectRepository.existsById(vacancyDto.getProjectId())) {
            throw new EntityNotFoundException(String.format(
                    "Project with id %d does not exist.", vacancyDto.getProjectId())
            );
        }
    }

    private void validateStatusOfVacancyCreator(CreateVacancyDto vacancyDto) {
        Long projectOwnerId = projectRepository.getProjectById(vacancyDto.getProjectId()).getOwnerId();
        if (userContext.getUserId() != projectOwnerId) {
            throw new DataValidException(String.format(
                    "Status in project %s should be \"OWNER\" for create vacancy.", vacancyDto.getProjectId())
            );
        }
    }

    private void validateAvailableCloseStatus(UpdateVacancyDto vacancyDto) {
        if (vacancyDto.getCount() > getCountAcceptedCandidate(vacancyDto.getId(), vacancyDto.getCandidateIds())) {
            throw new DataValidException("Vacancy can't be closed. Count of accepted candidate less than required");
        }
    }

    private int getCountAcceptedCandidate(Long vacancyId, List<Long> candidateIds) {
        return candidateRepository.countAllByVacancyIdAndStatusAndId(
                vacancyId,
                CandidateStatus.ACCEPTED,
                candidateIds
        );
    }

    private void validateExistingCandidateInVacancy(UpdateCandidateRequestDto updateCandidate, Vacancy vacancy) {
        if (!vacancyMapper.toCandidateIds(vacancy.getCandidates()).contains(updateCandidate.getCandidateId()))
            throw new DataValidException(String.format(
                    "Candidate is not found in vacancy with id %s.", updateCandidate.getVacancyId())
            );
    }

    private void convertCandidateInTeamMember(UpdateCandidateRequestDto updateCandidate, Long userId, Vacancy vacancy) {
        Team team = teamRepository.findById(updateCandidate.getTeamId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Team with id %s not found", updateCandidate.getTeamId())
                ));
        validateConsistRequiredTeamInProject(vacancy, team);
        TeamMember newTeamMember = TeamMember
                .builder()
                .team(team)
                .roles(new ArrayList<>(List.of(updateCandidate.getRole())))
                .userId(userId)
                .build();
        teamMemberJpaRepository.save(newTeamMember);
        if (vacancy.getCount() <= getCountAcceptedCandidate(vacancy.getId(),
                vacancy.getCandidates().stream().map(Candidate::getId).toList())) {
            vacancy.setStatus(VacancyStatus.CLOSED);
            saveEntity(vacancy);
        }
    }

    private void validateConsistRequiredTeamInProject(Vacancy vacancy, Team team) {
        List<Team> teams = vacancy.getProject().getTeams();
        if (!teams.contains(team)) {
            throw new DataValidException(String.format(
                    "Team with id %s is not found in project.", team.getId())
            );
        }
    }

    public List<ExtendedVacancyDto> findByFilter(VacancyFilterDto filters) {
        Stream<Vacancy> vacancies = vacancyRepository.findAll().stream();

        List<VacancyFilter> vacancyFilterList = vacancyFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .toList();

        for (VacancyFilter filter : vacancyFilterList) {
            vacancies = filter.apply(vacancies, filters);
        }
        return vacancies.map(vacancyMapper::toDto).toList();
    }

    public List<ExtendedVacancyDto> findAll() {
        return vacancyMapper.entityListToDtoList(vacancyRepository.findAll());
    }

    public ExtendedVacancyDto findById(Long id) {
        return vacancyMapper.toDto(
                vacancyRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("The vacancy with id " + id + " is not found")));
    }

    public void delete(Long id) {
        vacancyRepository.deleteById(id);
    }
}
