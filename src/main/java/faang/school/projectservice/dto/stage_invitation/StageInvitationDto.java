package faang.school.projectservice.dto.stage_invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotNull;
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
    private String description;
    private StageInvitationStatus status;
    @NotNull(message = "stageId can't be null")
    private Long stageId;
    @NotNull(message = "authorId can't be null")
    private Long authorId;
    @NotNull(message = "invitedId can't be null")
    private Long invitedId;
}
