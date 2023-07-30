package faang.school.projectservice.filter;

import faang.school.projectservice.dto.invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;
import java.util.stream.Stream;

@Component
public class StageInvitationInvitedFilter implements StageInvitationFilter {
    @Override
    public boolean isApplicable(StageInvitationFilterDto stageInvitationFilterDto) {
        return stageInvitationFilterDto.getInvitedPattern() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> invitationStream, StageInvitationFilterDto stageInvitationFilterDto) {
        return invitationStream.filter(stageInvitation ->
                stageInvitation.getInvited().getId().equals(stageInvitationFilterDto.getInvitedPattern()));
    }
}