package faang.school.projectservice.dto.stage;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StageInvitationDto {
    private Long id;
    private Long stageId;
    private String description;
    private Long authorId;
    private Long invitedId;
}