package faang.school.projectservice.dto.stageInvitation;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateStageInvitationDto {
    private Long id;

    @NotNull(message = "The stage id can't be empty")
    private Long stageId;

    @NotNull(message = "The author id can't be empty")
    private Long authorId;

    @NotNull(message = "The invited id can't be empty")
    private Long invitedId;
}
