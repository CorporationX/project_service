package faang.school.projectservice.service.stage_invitation.filter;

import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StageInvitationStatusFilter implements StageInvitationFilter {

    @Override
    public boolean isAcceptable(StageInvitationFilterDto stageInvitationFilterDto) {
        return stageInvitationFilterDto.getStatus() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitations, StageInvitationFilterDto stageInvitationFilterDto) {
        return stageInvitations.filter(stageInvitation -> stageInvitation.getStatus().equals(stageInvitationFilterDto.getStatus()));
    }
}
