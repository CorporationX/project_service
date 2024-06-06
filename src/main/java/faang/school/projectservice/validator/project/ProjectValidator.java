package faang.school.projectservice.validator.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.project.ProjectAlreadyExistsException;
import faang.school.projectservice.exception.project.ProjectStatusImmutableException;
import faang.school.projectservice.exception.project.ProjectStorageSizeInvalidException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static faang.school.projectservice.exception.project.ProjectRequestExceptions.*;

@Component
@RequiredArgsConstructor
public class ProjectValidator {
    private final ProjectRepository projectRepository;

    public void verifyCanBeCreated(ProjectDto projectDto) {
        verifyStorageSizeLimitsCorrect(projectDto);
        
        if (isProjectExists(projectDto)) {
            throw new ProjectAlreadyExistsException(ALREADY_EXISTS.getMessage());
        }
    }
    
    public void verifyCanBeUpdated(ProjectDto projectDto) {
        verifyImmutableStatus(projectDto);
        
        if (!isProjectExists(projectDto)) {
            throw new EntityNotFoundException(NOT_FOUND.getMessage());
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
            throw new ProjectStatusImmutableException(STATUS_IMMUTABLE.getMessage());
        }
    }

    private static void verifyStorageSizeLimitsCorrect(ProjectDto projectDto) {
        if (projectDto.isStorageSizeGreaterThanMaxStorageSize()) {
            throw new ProjectStorageSizeInvalidException(STORAGE_SIZE_INVALID.getMessage());
        }
    }

}
