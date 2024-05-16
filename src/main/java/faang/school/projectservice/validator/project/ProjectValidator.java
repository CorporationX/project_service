package faang.school.projectservice.validator.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.project.ProjectAlreadyExistsException;
import faang.school.projectservice.exception.project.ProjectStatusImmutableException;
import faang.school.projectservice.exception.project.ProjectStorageSizeInvalidException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

import static faang.school.projectservice.exception.project.ProjectRequestExceptions.*;

@Component
@RequiredArgsConstructor
public class ProjectValidator {
    private final ProjectRepository projectRepository;

    public void verifyCanBeCreated(@Valid ProjectDto projectDto) {
        verifyStorageSize(projectDto);
        verifyProjectDoesntExists(projectDto);
    }

    /*
       Сейчас метод похож на валидацию при создании
       Будет дополняться по необходимости
     */
    public void verifyCanBeUpdated(@Valid ProjectDto projectDto) {
        verifyProjectDoesntExists(projectDto);
        verifyImmutableStatus(projectDto);
    }

    private void verifyImmutableStatus(ProjectDto projectDto) {
        Project project = projectRepository.getProjectById(projectDto.getId());
        //Если пытаемся сменить статус с законченного или отмененного проекта на другой статус
        if (checkStatusFinished(project.getStatus()) && !checkStatusFinished(projectDto.getStatus())) {
            throw new ProjectStatusImmutableException(STATUS_IMMUTABLE.getMessage());
        }
    }

    private void verifyProjectDoesntExists(ProjectDto projectDto) {
        Long ownerId = projectDto.getOwnerId();
        String projectName = projectDto.getName();
        if (projectRepository.existsByOwnerUserIdAndName(ownerId, projectName)) {
            throw new ProjectAlreadyExistsException(ALREADY_EXISTS.getMessage());
        }
    }

    private static void verifyStorageSize(ProjectDto projectDto) {
        BigInteger storageSize = projectDto.getStorageSize();
        BigInteger maxStorageSize = projectDto.getMaxStorageSize();
        if (storageSize.compareTo(maxStorageSize) > 0) {
            throw new ProjectStorageSizeInvalidException(STORAGE_SIZE_INVALID.getMessage());
        }
    }

    private boolean checkStatusFinished(ProjectStatus status) {
        return status == ProjectStatus.CANCELLED || status == ProjectStatus.COMPLETED;
    }
}
