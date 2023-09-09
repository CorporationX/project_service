package faang.school.projectservice.dto.stageInvitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StageInvitationDto {
    private Long id;
    @NotBlank(message = "Description cannot be blank")
    private String description;
    private StageInvitationStatus status;
    @NotNull(message = "Stage id cannot be null")
    private Long stageId;
    @NotNull(message = "Author id cannot be null")
    private Long authorId;
    @NotNull(message = "Invited id cannot be null")
    private Long invitedId;
}
