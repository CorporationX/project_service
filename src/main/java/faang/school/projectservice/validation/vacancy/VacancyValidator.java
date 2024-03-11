package faang.school.projectservice.validation.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.VacancyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class VacancyValidator {
    private final ProjectRepository projectRepository;
    private final VacancyRepository vacancyRepository;

    public void validateVacancyFields(VacancyDto vacancyDto) {
        if (vacancyDto.getCuratorId() == null) {
            throw new DataValidationException("Vacancy must have a curator");
        }
        if (vacancyDto.getName() == null || vacancyDto.getName().isBlank()) {
            throw new DataValidationException("Vacancy must have a name");
        }
        if (vacancyDto.getProjectId() == null) {
            throw new DataValidationException("Vacancy must be assigned to a project");
        }
        if (vacancyDto.getDescription() == null || vacancyDto.getDescription().isBlank()) {
            throw new DataValidationException("Vacancy must have a description");
        }
    }

    public void validateIfVacancyCanBeClosed(VacancyDto vacancyDto) {
        Project project = projectRepository.getProjectById(vacancyDto.getProjectId());
        List<TeamMember> teamMembers = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .toList();
        teamMembers.forEach(teamMember -> {
            if (teamMember.getRoles() == null || teamMember.getRoles().isEmpty()) {
                throw new DataValidationException("Vacancy can't be closed until all team members gor their role");
            }
        });
    }

    public void validateIfCandidatesNoMoreNeeded(VacancyDto vacancyDto) {
        long vacancyId = vacancyDto.getId();
        int workersRequired = vacancyDto.getWorkersRequired();
        Vacancy vacancy = vacancyRepository.findById(vacancyDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Vacancy doesn't exist by id: " + vacancyId));
        long candidatesApprovedQuantity = vacancy.getCandidates().stream()
                .filter(candidate -> candidate.getCandidateStatus() == CandidateStatus.ACCEPTED)
                .count();
        if (candidatesApprovedQuantity <= workersRequired) {
            throw new DataValidationException(
                    String.format("Vacation can't be closed until %d candidates accepted", workersRequired));
        }
    }

    public void validateIfVacancyExistsById(long vacancyId) {
        vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new EntityNotFoundException("Vacancy doesn't exist by id: " + vacancyId));
    }

    public void validateIfProjectExistsById(long projectId) {
        projectRepository.getProjectById(projectId);
    }
}
