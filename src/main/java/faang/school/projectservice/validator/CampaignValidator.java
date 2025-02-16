package faang.school.projectservice.validator;

import faang.school.projectservice.adapter.CampaignRepositoryAdapter;
import faang.school.projectservice.adapter.ProjectRepositoryAdapter;
import faang.school.projectservice.adapter.TeamMemberRepositoryAdapter;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.exception.DataValidateException;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CampaignValidator {
    private final ProjectRepositoryAdapter projectAdapter;
    private final CampaignRepositoryAdapter campaignAdapter;
    private final TeamMemberRepositoryAdapter teamMemberAdapter;
    private final UserContext userContext;

    public void creatorStatusValidation(Long projectId) {
        TeamMember teamMember = teamMemberAdapter.getByUserIdAndProjectId(userContext.getUserId(), projectId);
        if (!teamMember.getRoles().contains(TeamRole.MANAGER) || !teamMember.getRoles().contains(TeamRole.OWNER)) {
            throw new DataValidateException("You are not the creator or manager of the project");
        }
    }

    public void statusByCreateValidation(CampaignStatus status) {
        if (!status.equals(CampaignStatus.ACTIVE)) {
            throw new DataValidateException("When created, the status can only be ACTIVE");
        }
    }
}
