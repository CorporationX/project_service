package faang.school.projectservice.validator.resource;

import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamRole;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourcePermissionValidator {
    private final ResourceRepository resourceRepository;

    public Resource getResourceWithPermission(Long resourceId, Long userId) {
        Resource resource = resourceRepository.getReferenceById(resourceId);

        Project project = resource.getProject();
        project.setStorageSize(project.getStorageSize().subtract(resource.getSize()));

        if (resource.getCreatedBy().getUserId().equals(userId)) {
            return resource;
        }

        boolean isManager = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .anyMatch(member -> member.getUserId().equals(userId)
                        && member.getRoles().contains(TeamRole.MANAGER));
        if (isManager) {
            return resource;
        }
        throw new PermissionDeniedDataAccessException("User does not have permission to delete this resource", null);
    }
}
