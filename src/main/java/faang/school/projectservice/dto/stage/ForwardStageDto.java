package faang.school.projectservice.dto.stage;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ForwardStageDto {
    @NotNull
    private Long id;
    @NotNull
    private Long forwardId;
}
