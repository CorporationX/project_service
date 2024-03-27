package faang.school.projectservice.dto.stage;

import lombok.Data;

@Data
public class StageInvitationDto {
    private Long stageInvitationId;
    private String stageInvitationStatus;
    private Long stageId;
    private Long authorId;
    private Long invitedId;

}
