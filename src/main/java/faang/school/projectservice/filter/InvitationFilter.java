package faang.school.projectservice.filter;

import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.stream.Stream;

public interface InvitationFilter {
    boolean isApplicable(InvitationFilterDto filterDto);
    Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitations, InvitationFilterDto filterDto);
}
