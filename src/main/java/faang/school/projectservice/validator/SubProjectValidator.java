package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.SubProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubProjectValidator {

    private final ProjectRepository projectRepository;

    public void validateSubProject(SubProjectDto subProjectDto) {
        if (subProjectDto.getName() == null || subProjectDto.getName().isBlank()) {
            throw new DataValidationException("Project can't be created with empty name");
        }
        if (subProjectDto.getParentProjectId() == null) {
            throw new DataValidationException("SubProject must have parentProjectId");
        }
        if (subProjectDto.getVisibility() == null) {
            throw new DataValidationException(String.format("Visibility of subProject '%s' must be specified as 'private' or 'public'.", subProjectDto.getName()));
        }
    }

    public void validateProjectNotExist(SubProjectDto projectDto) {
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataValidationException(String.format("Project %s is already exist", projectDto.getName()));
        }
    }

    public void validateParentProjectExist(SubProjectDto projectDto) {
        Project parentProject = projectRepository.getProjectById(projectDto.getParentProjectId());
        if (parentProject == null) {
            throw new EntityNotFoundException(String.format("Parent project not found by id: %s", projectDto.getParentProjectId()));
        }
    }

    public void checkSubProjectNotPrivateOnPublicProject(SubProjectDto projectDto) {
        Project parentProject = projectRepository.getProjectById(projectDto.getParentProjectId());
        if (parentProject.getVisibility().equals(ProjectVisibility.PUBLIC) && projectDto.getVisibility().equals(ProjectVisibility.PRIVATE)) {
            throw new DataValidationException(String.format("Private SubProject; %s, cant be with a public Parent Project: %s", projectDto.getName(), parentProject.getName()));
        }
    }

    public void checkSubProjectStatusCompleteOrCancelled(Project subProject) {
        if (subProject.getStatus() != ProjectStatus.COMPLETED && subProject.getStatus() != ProjectStatus.CANCELLED) {
            throw new DataValidationException("Can't close project if subProject status are not complete or cancelled");
        }
    }

    public void validateProjectsList(List<SubProjectDto> projectDtos) {
        if (projectDtos.isEmpty()) {
            throw new DataValidationException("List of project is empty");
        }
    }
}