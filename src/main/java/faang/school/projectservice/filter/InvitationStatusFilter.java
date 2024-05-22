package faang.school.projectservice.filter;

import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class InvitationStatusFilter implements InvitationFilter{
    @Override
    public boolean isApplicable(InvitationFilterDto filterDto) {
        return Objects.nonNull(filterDto.getStatus());
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitations, InvitationFilterDto filterDto) {
        return stageInvitations.filter(invitation -> invitation.getStatus().equals(filterDto.getStatus()));
    }
}
