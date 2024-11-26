package faang.school.projectservice.dto.stage_invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class StageInvitationFiltersDto {

    @NotNull(message = "stageId should not be null")
    private Long stageId;

    @NotNull(message = "authorId should not be null")
    private Long authorId;

    @NotNull(message = "invitedId should not be null")
    private Long invitedId;

    @NotNull(message = "status should not be null")
    private StageInvitationStatus status;

    @Size(max = 255, message = "Pattern should not exceed 255 characters")
    private String pattern;
}
