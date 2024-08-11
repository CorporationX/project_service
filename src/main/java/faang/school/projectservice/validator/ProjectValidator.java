package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProjectValidator {

    private final ProjectService projectService;

    public void checkUserPermission(long projectId, long userId) {

        boolean campaignCreateByProjectOwner = projectService.checkOwnerPermission(userId, projectId);
        boolean campaignCreateByManager = projectService.checkManagerPermission(userId, projectId);

        if (!campaignCreateByProjectOwner && !campaignCreateByManager) {
            log.warn("You don't have permission to manage campaign for project with id: {}. " +
                    "Only project owner or manager can manage campaign", projectId);
            throw new DataValidationException("You don't have permission to manage the campaign");
        }
    }


}
