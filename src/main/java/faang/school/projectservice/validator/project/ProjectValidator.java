package faang.school.projectservice.validator.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProjectValidator {
    private final ProjectRepository projectRepository;
    private final UserContext userContext;

    public void validateSubProjectForCreate(CreateSubProjectDto subProjectDto) {
        if (subProjectDto.getOwnerId() == null) {
            subProjectDto.setOwnerId(userContext.getUserId());
        }
        if (projectRepository.existsByOwnerUserIdAndName(
                subProjectDto.getOwnerId(), subProjectDto.getName())) {
            throw new DataValidationException("User with ID " + subProjectDto.getOwnerId() +
                    " already has project with name " + subProjectDto.getName());
        }
    }

    public void validateParentProjectForCreateSubProject(Project parentProject, CreateSubProjectDto subProjectDto) {
        if (subProjectDto.getVisibility() == null) {
            subProjectDto.setVisibility(parentProject.getVisibility());
        } else if (parentProject.getVisibility() != subProjectDto.getVisibility()) {
            throw new DataValidationException("Visibility of parent project and subproject should be equals");
        }
    }

    public void validateSubProjectForUpdate(Project subProject, UpdateSubProjectDto updateDto) {
        if (updateDto.getStatus() == subProject.getStatus()) {
            throw new DataValidationException("Project " + subProject.getId() + " already has status " + updateDto.getStatus());
        } else if (updateDto.getVisibility() == ProjectVisibility.PRIVATE) {
            subProject.getChildren().forEach(project -> project.setVisibility(ProjectVisibility.PRIVATE));
        }
    }

    public boolean readyToNewMoment(Project subProject, ProjectStatus newStatus) {
        if (newStatus == ProjectStatus.COMPLETED || newStatus == ProjectStatus.CANCELLED) {
            if (!areAllSubprojectsClosed(subProject)) {
                throw new DataValidationException("Children projects status should be COMPLETED or CANCELLED");
            }
            return true;
        }
        return false;
    }

    private boolean areAllSubprojectsClosed(Project subProject) {
        return subProject.getChildren().stream()
                .allMatch(children ->
                        children.getStatus() == ProjectStatus.COMPLETED
                        || children.getStatus() == ProjectStatus.CANCELLED);
    }

    public Project getProjectAfterValidateId(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("project with this ID: " + projectId + " doesn't exist");
        }
        return projectRepository.getProjectById(projectId);
    }

    public void validateOwnerId(Project subProject) {
        if (!subProject.getOwnerId().equals(userContext.getUserId())) {
            throw new DataValidationException("UserID " + userContext.getUserId()
                    + " isn't owner of Project " + subProject.getId());
        }
    }

    public boolean userHasAccessToProject(Long userId, Project project) {
        if (project.getVisibility() == ProjectVisibility.PRIVATE) {
            return Optional.ofNullable(project.getTeams()).stream()
                    .flatMap(Collection::stream)
                    .flatMap(team -> Optional.ofNullable(team.getTeamMembers()).stream()
                            .flatMap(Collection::stream))
                            .anyMatch(member -> member.getUserId().equals(userId));
        }
        return true;
    }
}
