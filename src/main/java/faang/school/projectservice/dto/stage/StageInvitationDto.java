package faang.school.projectservice.dto.stage;

import lombok.Data;

@Data
public class StageInvitationDto {
    private Long id;
    private Long stageId;
    private Long authorId;
    private Long invitedId;
    private String description;

}
