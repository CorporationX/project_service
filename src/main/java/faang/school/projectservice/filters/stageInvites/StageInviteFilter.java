package faang.school.projectservice.filters.stageInvites;

import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.stream.Stream;

public interface StageInviteFilter {
    boolean isApplicable(FilterStageInviteDto filterStageInviteDto);

    Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitationStream,
                                  FilterStageInviteDto filterStageInviteDto);
}
