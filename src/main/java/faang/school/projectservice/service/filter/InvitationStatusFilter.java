package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.stage.StageInvitationFilterDTO;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class InvitationStatusFilter implements InvitationFilter {
    @Override
    public boolean isApplicable(StageInvitationFilterDTO stageInvitationFilterDTO) {
        return stageInvitationFilterDTO.getStatus() != null;
    }

    @Override
    public Stream<StageInvitation> filter(Stream<StageInvitation> invitationStream, StageInvitationFilterDTO stageInvitationFilterDTO) {
        return invitationStream.filter(invitation -> invitation.getStatus().equals(stageInvitationFilterDTO.getStatus()));
    }
}
