package faang.school.projectservice.validator;

import faang.school.projectservice.exception.ApiException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static faang.school.projectservice.validator.util.ProjectValidatorErrorMessages.NOT_OWNER_OF_PROJECT;
import static faang.school.projectservice.validator.util.ProjectValidatorErrorMessages.PROJECT_HAS_NO_COVER;
import static faang.school.projectservice.validator.util.ProjectValidatorErrorMessages.PROJECT_RESOURCE_FILLED;

@Slf4j
@Service
public class ProjectServiceValidator {

    public void projectOwner(Project project, long ownerId) {
        if (project.getOwnerId() != ownerId) {
            log.error(String.format(NOT_OWNER_OF_PROJECT, ownerId, project.getOwnerId()));
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

    public long validResourceSize(Project project, MultipartFile file, Resource resource) {
        long deletedResourceSize = resource == null ? 0 : resource.getSize().longValue();
        long newFileSize = file.getSize();
        long currentSize = project.getStorageSize() == null ? 0 : project.getStorageSize().longValue();
        long maxStorageSize = project.getMaxStorageSize().longValue();
        long freeSpace = maxStorageSize - (currentSize - deletedResourceSize);

        if (newFileSize > freeSpace) {
            log.error(String.format(PROJECT_RESOURCE_FILLED, project.getId(), freeSpace));
            throw new ApiException(PROJECT_RESOURCE_FILLED, HttpStatus.BAD_REQUEST, project.getId(), freeSpace);
        }
        return (currentSize - deletedResourceSize) + newFileSize;
    }

    public String getCoverImageId(Project project) {
        String imageKey = project.getCoverImageId();

        if (imageKey == null || imageKey.isBlank()) {
            log.error(String.format(PROJECT_HAS_NO_COVER, project.getId()));
            throw new ApiException(PROJECT_HAS_NO_COVER, HttpStatus.NOT_FOUND, project.getId());
        }
        return imageKey;
    }
}
