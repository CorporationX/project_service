package faang.school.projectservice.dto.stage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class StageDto {
    private Long stageId;
    @NotBlank
    private String stageName;
    @NotNull
    private Long projectId;
    @NotEmpty
    private List<StageRolesDto> stageRolesDto;
}
