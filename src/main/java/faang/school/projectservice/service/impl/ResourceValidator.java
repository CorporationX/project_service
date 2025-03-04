package faang.school.projectservice.service.impl;

import faang.school.projectservice.config.filestorage.GalleryProperties;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceValidator {

    private final ProjectService projectService;
    private final ProjectValidator projectValidator;
    private final GalleryProperties galleryProperties;

    void validateUserCanDownloadFromProject(Long userId, Long projectId) {
        boolean isProjectNotPublic = !projectValidator.isProjectPublic(projectId);
        boolean isUserNotInProject = !projectValidator.isUserParticipatedInProject(userId, projectId);
        if (isUserNotInProject || isProjectNotPublic) {
            throw new IllegalArgumentException("User with id "
                    + userId + " has not access to resources of project "
                    + projectId + " at this moment");
        }
    }

    void validateResourcesOversize(Long projectId) {
        int maxFilesPerProjectQuantity = galleryProperties.getMaxFiles();

        if (projectService.getProjectResourceIds(projectId).size() > maxFilesPerProjectQuantity) {
            throw new RuntimeException("Limit resources of project is reached [" + maxFilesPerProjectQuantity + "]");
        }
    }
}
