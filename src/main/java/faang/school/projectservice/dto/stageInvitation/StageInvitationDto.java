package faang.school.projectservice.dto.stageInvitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StageInvitationDto {
    private Long id;
    private String description;
    @NotNull
    private StageInvitationStatus status;
    @NotNull
    private Long author;
    @NotNull
    private Long invited;
}
