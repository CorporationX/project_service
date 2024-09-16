package faang.school.projectservice.dto.stage_invitation;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeclineInvitationDto {
    @NotBlank(message = "Reason cannot be blank")
    private String reason;
}
