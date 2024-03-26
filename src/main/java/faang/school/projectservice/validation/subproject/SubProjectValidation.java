package faang.school.projectservice.validation.subproject;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubProjectValidation {
    private final ProjectRepository projectRepository;

    public void toCreate(CreateSubProjectDto subProjectDto) {
        validateNameVisibility(subProjectDto.getName(), subProjectDto.getVisibility());
        validateSubProjectDescriptionWritten(subProjectDto.getDescription());
        validateSubProjectParentIdWritten(subProjectDto.getParentProjectId());
        projectExistById(subProjectDto.getParentProjectId());
    }

    public void checkProjectDto(SubProjectDto projectDto) {
        projectExistById(projectDto.getId());
        validateNameVisibility(projectDto.getName(), projectDto.getVisibility());
        validateSubProjectStatusWritten(projectDto.getStatus());
    }

    private void validateNameVisibility(String name, ProjectVisibility visibility) {
        validateSubProjectNameWritten(name);
        validateSubProjectVisibilityWritten(visibility);
    }

    private void projectExistById(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new EntityNotFoundException(SubprojectLimitations.PROJECT_DOES_NOT_EXIST_BY_ID.getMessage());
        }
    }

    private void validateSubProjectStatusWritten(ProjectStatus status) {
        if (status == null) {
            throw new NullPointerException(SubprojectLimitations.SUBPROJECT_STATUS_CANNOT_BE_NULL.getMessage());
        }
    }

    private void validateSubProjectVisibilityWritten(ProjectVisibility visibility) {
        if (visibility == null) {
            throw new NullPointerException(SubprojectLimitations.SUBPROJECT_VISIBILITY_CANNOT_BE_NULL.getMessage());
        }
    }

    private void validateSubProjectParentIdWritten(Long parentProjectId) {
        if (parentProjectId == null) {
            throw new NullPointerException(SubprojectLimitations.PARENT_PROJECT_ID_CANNOT_BE_NULL.getMessage());
        }
    }

    private void validateSubProjectNameWritten(String name) {
        if (name == null) {
            throw new NullPointerException(SubprojectLimitations.SUBPROJECT_NAME_CANNOT_BE_NULL.getMessage());
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException(SubprojectLimitations.SUBPROJECT_NAME_CANNOT_BE_BLANC.getMessage());
        }
    }

    private void validateSubProjectDescriptionWritten(String description) {
        if (description == null) {
            throw new NullPointerException(SubprojectLimitations.SUBPROJECT_DESCRIPTION_CANNOT_BE_NULL.getMessage());
        }
        if (description.isBlank()) {
            throw new IllegalArgumentException(SubprojectLimitations.SUBPROJECT_DESCRIPTION_CANNOT_BE_BLANC.getMessage());
        }
    }
}
