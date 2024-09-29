package faang.school.projectservice.dto.stageInvitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
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
public class StageInvitationDtoResponse {

    @NotNull
    private Long id;

    @NotNull
    private Long invitedId;

    @Length(max = 128)
    private String description;

    @NotNull
    private StageInvitationStatus status;

}
