package faang.school.projectservice.dto.stage_invitation;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AcceptInviteDto {
    @NotNull
    private Long id;
    @NotNull
    private Long teamMemberId;
}
