package faang.school.projectservice.validation.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.exception.AccessDeniedProjectException;
import faang.school.projectservice.exception.ErrorMessage;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceValidator {

    private final ResourceRepository resourceRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserContext userContext;

    public Resource validateResourceAccess(Long resourceId, Long projectId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException(
                        ErrorMessage.RESOURCE_NOT_FOUND.getMessage(resourceId)));

        if (!resource.getProject().getId().equals(projectId)) {
            throw new AccessDeniedProjectException(
                    ErrorMessage.RESOURCE_DOES_NOT_BELONG_TO_PROJECT.getMessage(resourceId, projectId)
            );
        }

        Long userId = userContext.getUserId();
        boolean isAuthor = resource.getCreatedBy().getUserId().equals(userId);

        TeamMember member = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);
        if (!isAuthor && (member == null || !member.getRoles().contains(TeamRole.MANAGER))) {
            throw new AccessDeniedProjectException(
                    ErrorMessage.RESOURCE_DELETE_FORBIDDEN.getMessage(resourceId, userId)
            );
        }
        return resource;
    }
}

