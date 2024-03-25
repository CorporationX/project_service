package faang.school.projectservice.dto.stage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;

@Data
@AllArgsConstructor
@Builder
public class StageInvitationDto {
    private Long stageInvitationId;
    private Long stageId;
    private Long authorId;
    private Long invitedId;
}
