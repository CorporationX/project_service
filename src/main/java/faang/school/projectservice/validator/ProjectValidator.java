package faang.school.projectservice.validator;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.dto.ProjectUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static faang.school.projectservice.model.ProjectVisibility.PRIVATE;

@Component
@RequiredArgsConstructor
public class ProjectValidator {
    private final ProjectRepository projectRepository;

    public void validateCreateProject(ProjectDto projectDto) {
        if (projectDto.getName().isBlank() || projectDto.getDescription().isBlank()) {
            throw new DataValidationException("Project name and description cannot be null");
        }
    }

    public void validateUpdateProject(ProjectUpdateDto projectUpDateDto) {
        if (projectUpDateDto.getDescription().isBlank() && projectUpDateDto.getStatus() == null) {
            throw new DataValidationException("Check for id or description or status");
        }
    }

    public void validateFilter(ProjectFilterDto projectFilterDto) {
        if (projectFilterDto.getStatus() == null && projectFilterDto.getName().isBlank()) {
            throw new DataValidationException("Enter at least one filter");
        }
    }

    public void validateProjectId(Long id) {
        if (id <= 0) {
            throw new DataValidationException("Check for id");
        }
    }

    public Project checkIfUserIsMember(Long userId, Project project) {
        if (project.getVisibility()==PRIVATE) {
            boolean isMember = project.getTeams().stream()
                    .flatMap(team -> team.getTeamMembers().stream())
                    .anyMatch(teamMember -> teamMember.getId().equals(userId));
            if (!isMember) {
                return null;
            }
        }
        return project;
    }

    public void checkForValidOwner(Long userId, Project project) {
        if (!userId.equals(project.getOwnerId())) {
            throw new DataValidationException("You are not owner of project");
        }
    }
}
