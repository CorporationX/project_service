package faang.school.projectservice.dto.stage;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class StageDto {
    private Long stageId;
    @NotEmpty
    @NotNull
    private String stageName;
    @NotNull
    private Long projectId;
    @NotEmpty
    private List<StageRolesDto> stageRolesDto;
}
