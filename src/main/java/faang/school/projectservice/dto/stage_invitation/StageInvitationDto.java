package faang.school.projectservice.dto.stage_invitation;

import lombok.Data;

@Data
public class StageInvitationDto {

    private Long stageInvitationId;
    private String description;
    private long authorId;
    private long invitedId;
}
