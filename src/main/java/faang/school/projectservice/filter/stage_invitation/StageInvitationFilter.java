package faang.school.projectservice.filter.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.stream.Stream;

public interface StageInvitationFilter {
    boolean isApplicable(StageInvitationFilterDto filterDto);

    Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitationStream, StageInvitationFilterDto filterDto);
}
