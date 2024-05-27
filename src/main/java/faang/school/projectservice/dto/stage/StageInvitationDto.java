package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageInvitationDto {
@NotNull
    private Long id;

    @NotNull(message = "Description cannot be null")
    private String description;

    @NotNull(message = "Status cannot be null")
    private StageInvitationStatus status;

    @NotNull(message = "Stage ID cannot be null")
    private Long stageId;

    @NotNull(message = "Author ID cannot be null")
    private Long authorId;

    @NotNull(message = "Invited ID cannot be null")
    private Long invitedId;
}
