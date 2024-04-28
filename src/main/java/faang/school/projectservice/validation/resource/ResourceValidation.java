package faang.school.projectservice.validation.resource;

import faang.school.projectservice.handler.exceptions.DataValidationException;
import faang.school.projectservice.handler.exceptions.EntityNotFoundException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceValidation {
    private final ResourceRepository resourceRepository;

    public void checkingUserForUpdatingFile(TeamMember user, Resource resource) {
        Project project = resource.getProject();
        if (!resource.getCreatedBy().getId().equals(user.getId()) && !project.getOwnerId().equals(user.getId())) {
            throw new DataValidationException(ResourceConstraints.RESOURCE_CAN_NOT_CHANGE.getMessage());
        }
    }

    public void checkingUserForDeletingFile(TeamMember user, Resource resource) {
        Project project = resource.getProject();
        if (!resource.getCreatedBy().getId().equals(user.getId()) && !project.getOwnerId().equals(user.getId())) {
            throw new DataValidationException(ResourceConstraints.RESOURCE_CAN_NOT_DELETE.getMessage());
        }
    }
}
