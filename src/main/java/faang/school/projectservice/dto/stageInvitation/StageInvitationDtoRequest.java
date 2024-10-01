package faang.school.projectservice.dto.stageInvitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StageInvitationDtoRequest {
    private long id;

    @Length(max = 128)
    @NotEmpty
    private String description;

    private StageInvitationStatus status;

}
