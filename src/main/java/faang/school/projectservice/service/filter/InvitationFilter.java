package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.stage.StageInvitationFilterDTO;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.stream.Stream;

public interface InvitationFilter {
    boolean isApplicable(StageInvitationFilterDTO stageInvitationFilterDTO);

    Stream<StageInvitation> filter(Stream<StageInvitation> invitationStream, StageInvitationFilterDTO stageInvitationFilterDTO);
}
