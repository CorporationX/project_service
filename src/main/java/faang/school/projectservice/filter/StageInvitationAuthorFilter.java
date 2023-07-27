package faang.school.projectservice.filter;

import faang.school.projectservice.dto.invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class StageInvitationAuthorFilter implements StageInvitationFilter{
    @Override
    public boolean isApplicable(StageInvitationFilterDto filter) {
        return filter.getAuthorId() != null;
    }

    @Override
    public void apply(List<StageInvitation> invitations, StageInvitationFilterDto filter) {
        invitations.removeIf(invitation -> !Objects.equals(invitation.getAuthor().getId(), filter.getAuthorId()));
    }
}
