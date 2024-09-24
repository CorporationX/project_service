package faang.school.projectservice.dto.client.stageInvitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StageInvitationDto {
    private Long id;

    @NotNull
    private Long invitedId;

    @NotNull
    private Long stageId;

    @Length(max = 128)
    @NotEmpty
    private String description;

    private StageInvitationStatus status;
}
