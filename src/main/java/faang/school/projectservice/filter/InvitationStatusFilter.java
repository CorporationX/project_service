package faang.school.projectservice.filter;

import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.Objects;
import java.util.stream.Stream;

public class InvitationStatusFilter implements InvitationFilter{
    @Override
    public boolean isApplicable(InvitationFilterDto filterDto) {
        return Objects.nonNull(filterDto.getStatus());
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitation, InvitationFilterDto filterDto) {
        return stageInvitation.filter(invitation -> invitation.getStatus().equals(filterDto.getStatus()));
    }
}
