package faang.school.projectservice.validator;

import faang.school.projectservice.exception.ApiException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static faang.school.projectservice.validator.util.ProjectValidatorErrorMessages.NOT_OWNER_OF_PROJECT;
import static faang.school.projectservice.validator.util.ProjectValidatorErrorMessages.PROJECT_HAS_NO_COVER;
import static faang.school.projectservice.validator.util.ProjectValidatorErrorMessages.PROJECT_RESOURCE_FILLED;

@Slf4j
@Service
public class ProjectServiceValidator {

    public void projectOwner(Project project, long ownerId) {
        if (project.getOwnerId() != ownerId) {
            String logMessage = String.format(NOT_OWNER_OF_PROJECT, ownerId, project.getOwnerId());
            log.error(logMessage);
            throw new ApiException(NOT_OWNER_OF_PROJECT, HttpStatus.BAD_REQUEST, ownerId, project.getOwnerId());
        }
    }

    public Optional<Resource> getResourceByKey(Project project, String resourceKey) {
        if (resourceKey == null || project.getResources() == null) {
            return Optional.empty();
        }
        return project.getResources()
                .stream()
                .filter(resource -> resource.getKey().equals(resourceKey))
                .findFirst();
    }

    public long validResourceSize(Project project, long newFileSize) {
        long currentSize = project.getStorageSize() == null ? 0 : project.getStorageSize().longValue();
        long maxStorageSize = project.getMaxStorageSize().longValue();
        long freeSpace = maxStorageSize - currentSize;

        if (newFileSize > freeSpace) {
            String logMessage = String.format(PROJECT_RESOURCE_FILLED, project.getId(), freeSpace);
            log.error(logMessage);
            throw new ApiException(PROJECT_RESOURCE_FILLED, HttpStatus.BAD_REQUEST, project.getId(), freeSpace);
        }
        return currentSize + newFileSize;
    }

    public String coverImageId(Project project) {
        String imageKey = project.getCoverImageId();

        if (imageKey == null || imageKey.isBlank()) {
            String logMessage = String.format(PROJECT_HAS_NO_COVER, project.getId());
            log.error(logMessage);
            throw new ApiException(PROJECT_HAS_NO_COVER, HttpStatus.NOT_FOUND, project.getId());
        }
        return imageKey;
    }
}
