package faang.school.projectservice.filter;

import faang.school.projectservice.dto.stage.invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.stream.Stream;

public interface StageInvitationFilter {

    boolean isApplicable(StageInvitationFilterDto stageInvitationFilterDto);

    Stream<StageInvitation> apply(Stream<StageInvitation> allStageInvitations,
            StageInvitationFilterDto stageInvitationFilterDto);
}
