package faang.school.projectservice.filters.stageInvites;

import faang.school.projectservice.controller.model.Moment;
import faang.school.projectservice.controller.model.stage_invitation.StageInvitation;
import faang.school.projectservice.filters.moments.FilterMomentDto;

import java.util.stream.Stream;

public interface StageInviteFilter {
    boolean isApplicable(FilterStageInviteDto filterStageInviteDto);

    Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitationStream,
                                  FilterStageInviteDto filterStageInviteDto);
}
