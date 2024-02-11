package faang.school.projectservice.filter.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StageInvitationAuthorIdFilter implements StageInvitationFilter {

    @Override
    public boolean IsApplicable(StageInvitationFilterDto filter) {
        return filter.getAuthorId() != null;
    }

    @Override
    public void apply(List<StageInvitation> invitations, StageInvitationFilterDto filter) {
        invitations.removeIf(invitation -> !invitation.getAuthor().getId().equals(filter.getAuthorId()));
    }
}
