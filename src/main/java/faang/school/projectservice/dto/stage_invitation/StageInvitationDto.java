package faang.school.projectservice.dto.stage_invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
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

    @NotBlank(message = "Description should not be blank")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;
}
