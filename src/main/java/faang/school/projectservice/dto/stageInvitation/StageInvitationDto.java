package faang.school.projectservice.dto.stageInvitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StageInvitationDto {
    private Long id;
    private String description;
    @NotNull
    private StageInvitationStatus status;
    @NotNull
    private long authorId;
    @NotNull
    private long invitedId;
}