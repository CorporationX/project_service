package faang.school.projectservice.dto.stage_invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StageInvitationDto {
    private Long id;
    @NotNull
    private Long stageId;
    @NotNull
    private Long authorId;
    @NotNull
    private Long invitedId;
    private String description;
    private StageInvitationStatus status;
}
