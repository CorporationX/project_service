package faang.school.projectservice.dto.stage_invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StageInvitationDto {
    private Long id;

    @Positive(message = "StageId must be positive")
    @NotNull(message = "StageId must not be null")
    private Long stageId;

    @Positive(message = "AuthorId must be positive")
    @NotNull(message = "AuthorId must not be null")
    private Long authorId;

    @Positive(message = "InvitedId must be positive")
    @NotNull(message = "InvitedId must not be null")
    private Long invitedId;

    private StageInvitationStatus status;
}
