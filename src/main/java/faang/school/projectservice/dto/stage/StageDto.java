package faang.school.projectservice.dto.stage;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
    private List<@Valid StageRolesDto> stageRoles;
}
