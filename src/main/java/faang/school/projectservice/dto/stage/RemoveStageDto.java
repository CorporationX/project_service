package faang.school.projectservice.dto.stage;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RemoveStageDto {
    @NotNull
    private Long id;
    @NotNull
    private Actions actions;
    private Long forwardId;
}
