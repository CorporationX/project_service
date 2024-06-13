package faang.school.projectservice.service.stage_invitation.filter;

import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.stream.Stream;

public interface StageInvitationFilter {

    boolean isAcceptable(StageInvitationFilterDto stageInvitationFilterDto);

    Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitations, StageInvitationFilterDto stageInvitationFilterDto);
}
