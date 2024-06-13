package faang.school.projectservice.dto.stage_invitation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StageInvitationCreateDto {

    @NotNull(message = "StageId should not be null")
    @Positive(message = "StageId should be positive")
    private Long stageId;

    @NotNull(message = "AuthorId should not be null")
    @Positive(message = "AuthorId should be positive")
    private Long authorId;

    @NotNull(message = "InvitedId should not be null")
    @Positive(message = "InvitedId should be positive")
    private Long invitedId;
}
