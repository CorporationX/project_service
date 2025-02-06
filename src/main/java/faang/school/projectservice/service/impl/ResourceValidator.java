package faang.school.projectservice.service.impl;

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

    //@Value("${gallery.max-files}")
    private Integer MAX_FILES_PER_PROJECT = 50;

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
        if (projectService.getProjectResourceIds(projectId).size() > MAX_FILES_PER_PROJECT) {
            throw new RuntimeException("Limit resources of project is reached [" + MAX_FILES_PER_PROJECT + "]");
        }
    }



}
