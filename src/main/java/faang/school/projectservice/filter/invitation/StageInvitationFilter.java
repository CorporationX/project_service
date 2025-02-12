package faang.school.projectservice.filter.invitation;

import faang.school.projectservice.dto.filterDto.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.stream.Stream;

public interface StageInvitationFilter {
    boolean isApplicable(StageInvitationFilterDto filter);

    Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitations, StageInvitationFilterDto filter);
}
