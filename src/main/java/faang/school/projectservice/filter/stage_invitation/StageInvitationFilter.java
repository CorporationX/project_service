package faang.school.projectservice.filter.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.List;

public interface StageInvitationFilter {

    boolean IsApplicable(StageInvitationFilterDto filter);

    void apply(List<StageInvitation> invitations, StageInvitationFilterDto filter);
}
