package faang.school.projectservice.dto.stageInvitation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class RejectStageInvitationDto {
    @NotNull(message = "The invitation id can't be empty")
    private Long id;

    @NotBlank(message = "The exception can't be empty")
    private String explanation;
}
