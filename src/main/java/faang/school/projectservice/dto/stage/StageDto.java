package faang.school.projectservice.dto.stage;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class StageDto {
    private Long stageId;
    @NotBlank
    private String stageName;
    @NotNull
    @PositiveOrZero
    private Long projectId;
    @NotEmpty
    @NotNull
    private List<StageRolesDto> stageRolesDto;
}
