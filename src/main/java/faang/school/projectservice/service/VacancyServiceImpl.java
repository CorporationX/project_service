package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.candidate.CandidateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.filter.VacancyFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class VacancyServiceImpl implements VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final ProjectRepository projectRepository;
    private final UserContext userContext;
    private final TeamMemberRepository memberRepository;
    private final List<VacancyFilter> filterList;

    @Override
    public VacancyDto createVacancy(long projectId, VacancyDto vacancyDto) {
        checkOwnerOrManager(projectId);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new DataValidationException(
                        String.format("Project with id %d not found", projectId)));

        List<CandidateDto> candidates = vacancyDto.getCandidates();
        if (candidates != null && !candidates.isEmpty()) {
            checkCandidatesNotProjectMembers(candidates, projectId);
        }

        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        vacancy.setProject(project);
        vacancy.setStatus(VacancyStatus.OPEN);
        Vacancy savedVacancy = vacancyRepository.save(vacancy);
        return vacancyMapper.toDto(savedVacancy);
    }

    @Override
    public VacancyDto updateVacancy(long projectId, long vacancyId, VacancyDto vacancyDto) {
        checkOwnerOrManager(projectId);
        Vacancy existingVacancy = loadVacancy(vacancyId);
        validateVacancyOwnership(existingVacancy, projectId);

        List<CandidateDto> newCandidates = vacancyDto.getCandidates();
        if (newCandidates != null && !newCandidates.isEmpty()) {
            checkCandidatesNotProjectMembers(newCandidates, projectId);
        }
        existingVacancy.setName(vacancyDto.getName());
        existingVacancy.setDescription(vacancyDto.getDescription());
        existingVacancy.setPosition(vacancyDto.getPosition());
        existingVacancy.setCount(vacancyDto.getCount());
        existingVacancy.setStatus(vacancyDto.getStatus());
        Vacancy updated = vacancyRepository.save(existingVacancy);
        return vacancyMapper.toDto(updated);
    }

    @Override
    public VacancyDto closeVacancy(long projectId, long vacancyId) {
        checkOwnerOrManager(projectId);
        Vacancy vacancy = loadVacancy(vacancyId);
        validateVacancyOwnership(vacancy, projectId);

        long acceptedCandidateCount = vacancy.getCandidates().stream()
                .filter(candidate -> candidate.getCandidateStatus() == CandidateStatus.ACCEPTED)
                .count();
        if (acceptedCandidateCount < vacancy.getCount()) {
            throw new DataValidationException(
                    String.format("Not enough accepted candidates to close vacancy %d. Required: %d, Actual: %d",
                            vacancyId, vacancy.getCount(), acceptedCandidateCount));
        }
        vacancy.setStatus(VacancyStatus.CLOSED);
        Vacancy saved = vacancyRepository.save(vacancy);
        return vacancyMapper.toDto(saved);
    }

    @Override
    public VacancyDto getVacancyById(long projectId, long vacancyId) {
        Vacancy vacancy = loadVacancy(vacancyId);
        validateVacancyOwnership(vacancy, projectId);
        return vacancyMapper.toDto(vacancy);
    }

    @Override
    public List<VacancyDto> getVacanciesByProjectId(long projectId, String positionFilter, String nameFilter) {
        Stream<Vacancy> vacancyStream = vacancyRepository.findAll().stream()
                .filter(vacancy -> vacancy.getProject().getId().equals(projectId));

        if (filterList != null) {
            for (VacancyFilter filter : filterList) {
                if (filter.isApplicable(positionFilter, nameFilter)) {
                    vacancyStream = filter.apply(vacancyStream, positionFilter, nameFilter);
                }
            }
        }

        return vacancyStream
                .map(vacancyMapper::toDto)
                .toList();
    }

    private void checkOwnerOrManager(long projectId) {
        long userId = userContext.getUserId();
        TeamMember teamMember = memberRepository.findByUserIdAndProjectId(userId, projectId);
        boolean hasRightRole =
                teamMember != null &&
                        teamMember.getRoles().stream()
                                .anyMatch(role -> role == TeamRole.OWNER || role == TeamRole.MANAGER);

        if (!hasRightRole) {
            throw new DataValidationException(
                    String.format("Not allowed: User %d does not have required role (OWNER or MANAGER) in project %d",
                            userId, projectId)
            );
        }
    }

    private Vacancy loadVacancy(long vacancyId) {
        return vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new DataValidationException(
                        String.format("Vacancy id %d not found", vacancyId)));
    }

    private void validateVacancyOwnership(Vacancy vacancy, long projectId) {
        if (!vacancy.getProject().getId().equals(projectId)) {
            throw new DataValidationException(
                    String.format("Vacancy project id %d does not belong to this project", projectId));
        }
    }

    private void checkCandidatesNotProjectMembers(List<CandidateDto> candidates, long projectId) {
        for (CandidateDto candidate : candidates) {
            TeamMember member = memberRepository.findByUserIdAndProjectId(candidate.getUserId(), projectId);
            if (member != null) {
                throw new DataValidationException(
                        String.format("User %d already in project %d", candidate.getUserId(), projectId)
                );
            }
        }
    }
}