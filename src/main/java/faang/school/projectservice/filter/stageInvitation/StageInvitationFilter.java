package faang.school.projectservice.filter.stageInvitation;

import faang.school.projectservice.dto.invitation.DtoStageInvitationFilter;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.stream.Stream;

public interface StageInvitationFilter {
    boolean isApplication(DtoStageInvitationFilter filters);

    Stream<StageInvitation> apply(Stream<StageInvitation> invitations, DtoStageInvitationFilter filters);
}
