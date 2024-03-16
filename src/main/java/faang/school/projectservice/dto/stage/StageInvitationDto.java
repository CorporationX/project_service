package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;

public class StageInvitationDto {
    private Long id;
    private Long StageId;
    private StageInvitationStatus status;
    private Long authorId;
    private Long invitedId;
}
