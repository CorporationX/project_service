package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.filter.VacancyFilterDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.filter.VacancyFilter;
import faang.school.projectservice.validator.VacancyValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final CandidateRepository candidateRepository;
    private final VacancyValidator vacancyValidator;
    private final VacancyMapper vacancyMapper;
    private final List<VacancyFilter> vacancyFilters;
    private final ProjectRepository projectRepository;

    @Transactional
    public VacancyDto create(VacancyDto vacancyDto) {
        Project project = projectRepository.getProjectById(vacancyDto.getProjectId());
        vacancyValidator.validate(vacancyDto, project);
        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        vacancy.setStatus(VacancyStatus.OPEN);
        vacancy.setProject(project);
        Vacancy savedVacancy = vacancyRepository.save(vacancy);
        return vacancyMapper.toDto(savedVacancy);
    }

    @Transactional
    public VacancyDto update(VacancyDto vacancyDto) {
        Vacancy vacancy = findVacancyById(vacancyDto.getId());
        vacancyValidator.validate(vacancyDto, vacancy.getProject());
        List<Candidate> candidates = getCandidatesById(vacancyDto);
        vacancyValidator.validateCandidatesCount(candidates, vacancyDto);
        for (Candidate candidate : candidates) {
            TeamMember teamMember = teamMemberRepository.findByUserIdAndProjectId(candidate.getUserId(), vacancy.getProject().getId());
            teamMember.setRoles(List.of(TeamRole.DEVELOPER));
            teamMemberRepository.save(teamMember);
        }
        vacancy.setStatus(VacancyStatus.CLOSED);
        vacancy.setUpdatedAt(LocalDateTime.now());
        Vacancy updatedVacancy = vacancyRepository.save(vacancy);
        return vacancyMapper.toDto(updatedVacancy);
    }

    @Transactional
    public VacancyDto delete(Long id) {
        Vacancy vacancy = findVacancyById(id);
        List<Candidate> candidates = vacancy.getCandidates();
        for (Candidate candidate : candidates) {
            TeamMember teamMember = teamMemberRepository.findByUserIdAndProjectId(candidate.getUserId(), vacancy.getProject().getId());
            if (teamMember.getRoles().isEmpty()) {
                teamMemberRepository.deleteById(teamMember.getId());
            }
        }
        vacancyRepository.deleteById(id);
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

    @Transactional(readOnly = true)
    public VacancyDto get(Long id) {
        Vacancy vacancy = findVacancyById(id);
        return vacancyMapper.toDto(vacancy);
    }

    private List<Candidate> getCandidatesById(VacancyDto vacancyDto) {
        return vacancyDto.getCandidateIds().stream()
                .map(candidateId -> candidateRepository.findById(candidateId)
                        .orElseThrow(() -> new EntityNotFoundException(String.format("Candidate doesn't exist by id: %s", candidateId))))
                .toList();
    }

    private Vacancy findVacancyById(Long id) {
        return vacancyRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Vacancy not found!"));
    }
}
