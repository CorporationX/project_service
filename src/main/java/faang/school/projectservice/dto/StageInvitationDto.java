package faang.school.projectservice.dto;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StageInvitationDto {
    private Long id;
    @NotBlank
    private String reason;
    private StageInvitationStatus status;
    @NotNull
    private Long stageId;
    @NotNull
    private Long authorId;
    @NotNull
    private Long invitedId;
}