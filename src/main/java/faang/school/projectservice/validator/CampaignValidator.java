package faang.school.projectservice.validator;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.adapter.TeamMemberRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CampaignValidator {
    private final TeamMemberRepositoryAdapter teamMemberAdapter;
    private final UserContext userContext;

    public void userStatusValidation(Long projectId) {
        TeamMember teamMember = teamMemberAdapter.getByUserIdAndProjectId(userContext.getUserId(), projectId);
        if (!(teamMember.getRoles().contains(TeamRole.MANAGER) || teamMember.getRoles().contains(TeamRole.OWNER))) {
            throw new DataValidationException("You are not the creator or manager of the project");
        }
    }

    public void statusByCreateValidation(CampaignStatus status) {
        if (!status.equals(CampaignStatus.ACTIVE)) {
            throw new DataValidationException("When created, the status can only be ACTIVE");
        }
    }
}
