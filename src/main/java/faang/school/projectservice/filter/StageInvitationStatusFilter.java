package faang.school.projectservice.filter;

import faang.school.projectservice.dto.invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StageInvitationStatusFilter implements StageInvitationFilter{
    @Override
    public boolean isApplicable(StageInvitationFilterDto filter) {
        return filter.getStatus() != null && !filter.getStatus().isBlank();
    }

    @Override
    public void apply(List<StageInvitation> invitations, StageInvitationFilterDto filter) {
        invitations.removeIf(invitation -> !invitation.getStatus().toString().equalsIgnoreCase(filter.getStatus()));
    }
}
