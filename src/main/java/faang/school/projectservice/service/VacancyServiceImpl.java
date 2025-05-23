package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.candidate.CandidateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exeption.DataValidationException;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.filter.VacancyFilter;
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
    private final TeamMemberRepository memberRepo;
    private final List<VacancyFilter> filterList;

    public void checkOwnerOrManager(long projectId) {
        long userId = userContext.getUserId();
        TeamMember teamMember = memberRepo.findByUserIdAndProjectId(userId, projectId);
        boolean hasRightRole =
                teamMember != null &&
                        teamMember.getRoles().stream()
                                .anyMatch(role -> role == TeamRole.OWNER || role == TeamRole.MANAGER);

        if (!hasRightRole) {
            throw new DataValidationException(
                    "Not allowed to perform this operation, only for owner and project manager"
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

    private void validateCandidatesNotProjectMembers(List<CandidateDto> candidates, long projectId) {
        for (CandidateDto candidate : candidates) {
            TeamMember member = memberRepo.findByUserIdAndProjectId(candidate.getUserId(), projectId);
            if (member != null) {
                throw new DataValidationException(
                        String.format("User %d already in project %d", candidate.getUserId(), projectId)
                );
            }
        }
    }

    @Override
    public VacancyDto createVacancy(long projectId, VacancyDto vacancyDto) {
        checkOwnerOrManager(projectId);
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new DataValidationException("Project not found"));
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
            validateCandidatesNotProjectMembers(newCandidates, projectId);
        }

        vacancyMapper.updateVacancy(existingVacancy, vacancyDto);
        Vacancy updated = vacancyRepository.save(existingVacancy);
        return vacancyMapper.toDto(updated);
    }

    @Override
    public VacancyDto closeVacancy(long projectId, long vacancyId) {
        checkOwnerOrManager(projectId);
        Vacancy vacancy = loadVacancy(vacancyId);
        long acceptedCandidateCount = vacancy.getCandidates().stream()
                .filter(candidate -> candidate.getCandidateStatus() == CandidateStatus.ACCEPTED)
                .count();
        if (acceptedCandidateCount < vacancy.getCount()) {
            throw new DataValidationException("Not enough accepted candidates to close");
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
}

