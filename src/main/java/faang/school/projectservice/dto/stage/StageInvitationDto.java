package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StageInvitationDto {
    private Long id;
    @NotBlank(message = "Reason can't be empty")
    private String reason;
    @NotNull(message = "Status can't be empty")
    private StageInvitationStatus status;
    @NotNull(message = "Stage ID cannot be empty")
    private Long stageId;
    @NotNull(message = "Author ID cannot be empty")
    private Long authorId;
    @NotNull(message = "Invited ID cannot be empty")
    private Long invitedId;
}