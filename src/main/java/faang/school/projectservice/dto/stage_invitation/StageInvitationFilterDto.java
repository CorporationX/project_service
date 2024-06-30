package faang.school.projectservice.dto.stage_invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StageInvitationFilterDto {

    @Positive(message = "StageId should be positive")
    private Long stageId;

    @Positive(message = "AuthorId should be positive")
    private Long authorId;

    private StageInvitationStatus status;
}
