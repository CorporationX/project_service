package faang.school.projectservice.stage_invitation_filter;

import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.stream.Stream;

public interface StageInvitationFilter {
    boolean isApplicable(StageInvitationFilterDto filters);

    Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitations, StageInvitationFilterDto filters);
}
