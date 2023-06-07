package faang.school.projectservice.dto.stage_invitation;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RejectInviteDto {
    @NotNull
    private Long id;
    @NotNull
    private String description;
}
