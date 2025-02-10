package faang.school.projectservice.dto.stage.stage_role;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class StageRolesUpdateRequestDto {
    @Positive
    private Long id;
    @PositiveOrZero
    private Integer count;
}
