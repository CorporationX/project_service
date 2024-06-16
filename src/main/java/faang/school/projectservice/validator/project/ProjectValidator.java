package faang.school.projectservice.validator.project;

import org.springframework.stereotype.Component;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.project.ProjectAlreadyExistsException;
import faang.school.projectservice.exception.project.ProjectStatusException;
import faang.school.projectservice.exception.project.ProjectStorageSizeInvalidException;
import faang.school.projectservice.exception.project.ProjectVisibilityException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;

import static faang.school.projectservice.exception.project.ProjectExceptionMessage.ALREADY_EXISTS;
import static faang.school.projectservice.exception.project.ProjectExceptionMessage.NOT_FOUND;
import static faang.school.projectservice.exception.project.ProjectExceptionMessage.NO_COVER;
import static faang.school.projectservice.exception.project.ProjectExceptionMessage.STATUS_IMMUTABLE;
import static faang.school.projectservice.exception.project.ProjectExceptionMessage.STORAGE_SIZE_INVALID;
import static faang.school.projectservice.exception.project.ProjectExceptionMessage.STORAGE_SIZE_MAX_EXCEED;
import static faang.school.projectservice.exception.project.ProjectRequestExceptions.ALREADY_EXISTS;
import static faang.school.projectservice.exception.project.ProjectRequestExceptions.NOT_FOUND_BY_NAME_AND_OWNER_ID;
import static faang.school.projectservice.exception.project.ProjectRequestExceptions.STATUS_IMMUTABLE;
import static faang.school.projectservice.exception.project.ProjectRequestExceptions.STORAGE_SIZE_INVALID;
import static faang.school.projectservice.exception.project.ProjectRequestExceptions.SUBPROJECT_NOT_FINISHED_EXCEPTION;
import static faang.school.projectservice.exception.project.ProjectRequestExceptions.SUBPROJECT_VISIBILITY_INVALID;

@Component
@RequiredArgsConstructor
public class ProjectValidator {
    private final ProjectRepository projectRepository;

    public void verifyCanBeCreated(ProjectDto projectDto) {
        verifyStorageSizeLimitsCorrect(projectDto);
        verifySubprojectVisibility(projectDto);

        if (isProjectExists(projectDto)) {
            throw new ProjectAlreadyExistsException(ALREADY_EXISTS.getMessage());
        }
    }

    public void verifyCanBeUpdated(ProjectDto projectDto) {
        verifyImmutableStatus(projectDto);
        verifySubprojectVisibility(projectDto);

        if (!isProjectExists(projectDto)) {
            throw new EntityNotFoundException(NOT_FOUND_BY_NAME_AND_OWNER_ID.getMessage());
        }
    }

    public void verifySubprojectStatus(Project projectToBeUpdated) {
        if (projectToBeUpdated.getChildren().isEmpty()) {
            return;
        }

        projectToBeUpdated.getChildren().forEach(subproject -> {
            if (!subproject.isStatusFinished()) {
                throw new ProjectStatusException(SUBPROJECT_NOT_FINISHED_EXCEPTION.getMessage());
            }

            verifySubprojectStatus(subproject);
        });
    }

    public void verifySubprojectVisibility(ProjectDto projectDto) {
        if (projectDto.getParentProjectId() == null) {
            return;
        }

        Project parentProject = projectRepository.getProjectById(projectDto.getParentProjectId());

        if (parentProject.getVisibility().equals(ProjectVisibility.PUBLIC)
                && projectDto.getVisibility().equals(ProjectVisibility.PRIVATE)) {
            throw new ProjectVisibilityException(SUBPROJECT_VISIBILITY_INVALID.getMessage());
        }
    }
    
    public void verifyStorageSizeNotExceeding(Project project, Long fileSize) {
        if (project.isMaximumStorageSizeExceed(fileSize)) {
            throw new DataValidationException(STORAGE_SIZE_MAX_EXCEED.getMessage());
        }
    }
    
    public void verifyNoCover(Project project) {
        if (!project.hasCover()) {
            throw new DataValidationException(NO_COVER.getMessage());
        }
    }
    
    private boolean isProjectExists(ProjectDto projectDto) {
        Long ownerId = projectDto.getOwnerId();
        String projectName = projectDto.getName();

        return projectRepository.existsByOwnerUserIdAndName(ownerId, projectName);
    }

    private void verifyImmutableStatus(ProjectDto projectDto) {
        Project existingProject = projectRepository.getProjectById(projectDto.getId());

        //Если пытаемся сменить статус с законченного или отмененного проекта на другой статус
        if (existingProject.isStatusFinished() && !projectDto.isStatusFinished()) {
            throw new ProjectStatusException(STATUS_IMMUTABLE.getMessage());
        }
    }

    private static void verifyStorageSizeLimitsCorrect(ProjectDto projectDto) {
        if (projectDto.isStorageSizeGreaterThanMaxStorageSize()) {
            throw new ProjectStorageSizeInvalidException(STORAGE_SIZE_INVALID.getMessage());
        }
    }
}
