package faang.school.projectservice.filter;

import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class InvitationStageFilter implements InvitationFilter {

    @Override
    public boolean isApplicable(InvitationFilterDto filterDto) {
        return filterDto.getStageId() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitations, InvitationFilterDto filterDto) {
        return stageInvitations.filter(invitation -> invitation.getStage().getStageId().equals(filterDto.getStageId()));
    }
}
