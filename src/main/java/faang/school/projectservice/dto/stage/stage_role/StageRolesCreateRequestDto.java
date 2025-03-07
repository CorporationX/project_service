package faang.school.projectservice.dto.stage.stage_role;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class StageRolesCreateRequestDto {
    @NotBlank
    private TeamRole teamRole;
    @PositiveOrZero
    private Integer count;
}
