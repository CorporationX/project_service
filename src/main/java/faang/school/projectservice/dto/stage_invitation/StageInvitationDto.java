package faang.school.projectservice.dto.stage_invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    private String description;
    private StageInvitationStatus status;
    @NotNull(message = "stageId can`t be null!")
    @Positive
    private Long stageId;
    @NotNull(message = "authorId can't be null")
    @Positive
    private Long authorId;
    @Positive
    @NotNull(message = "invitedId can't be null")
    private Long invitedId;
}
