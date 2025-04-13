package faang.school.projectservice.validation.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.exception.AccessDeniedProjectException;
import faang.school.projectservice.exception.ErrorMessage;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectValidator {
    private final TeamMemberRepository teamMemberRepository;
    private final UserContext userContext;

    public TeamMember validateProjectMembership(Long projectId) {
        Long userId = userContext.getUserId();
        TeamMember member = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);

        if (member == null) {
            throw new AccessDeniedProjectException(
                    ErrorMessage.ACCESS_DENIED_FOR_PROJECT.getMessage(projectId, userId)
            );
        }
        return member;
    }
}

