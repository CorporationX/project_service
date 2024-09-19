package faang.school.projectservice.dto.client.stage;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageInvitationFilterDto {

    @NotNull
    private String invitedStageName;

    @NotNull
    private Long invitedId;

    @NotNull
    private StageInvitationStatus status;

}
