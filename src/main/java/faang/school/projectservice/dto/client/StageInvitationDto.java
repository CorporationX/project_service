package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.Data;


@Data
public class StageInvitationDto {
    private long id;
    private String description;
    private StageInvitationStatus status;
    private String rejectionReason;
    private long stageId;
    private long authorId;
    private long invitedId;
}