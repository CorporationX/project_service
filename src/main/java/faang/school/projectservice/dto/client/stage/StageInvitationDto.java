package faang.school.projectservice.dto.client.stage;

import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StageInvitationDto {

    @NotNull
    private Long id;

    @NotNull
    private Long invitedId;

    @NotNull
    private Long stageId;

    @Length(max = 128)
    private String description;

    @NotNull
    private StageInvitationStatus status;
}
