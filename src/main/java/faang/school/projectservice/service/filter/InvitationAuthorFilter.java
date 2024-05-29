package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.stage.StageInvitationFilterDTO;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class InvitationAuthorFilter implements InvitationFilter {
    @Override
    public boolean isApplicable(StageInvitationFilterDTO stageInvitationFilterDTO) {
        return stageInvitationFilterDTO.getAuthorId() != null;
    }

    @Override
    public Stream<StageInvitation> filter(Stream<StageInvitation> invitationStream, StageInvitationFilterDTO stageInvitationFilterDTO) {
        return invitationStream.filter(invitation -> invitation.getAuthor().getId().equals(stageInvitationFilterDTO.getAuthorId()));
    }
}
