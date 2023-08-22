package faang.school.projectservice.dto.stage;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StageDto {

    @NotNull(message = "Stage id must not be null")
    @Min(value = 0, message = "Stage id must be greater than or equal to 0")
    @Max(value = Long.MAX_VALUE, message = "Stage id must be less than or equal to Long.MAX_VALUE")
    private Long stageId;

    @NotNull(message = "Stage name must not be null")
    @NotBlank(message = "Stage name must not be blank")
    private String stageName;

    @NotNull(message = "Project must not be null")
    @Min(value = 0, message = "Project id must be greater than or equal to 0")
    @Max(value = Long.MAX_VALUE, message = "Project id must be less than or equal to Long.MAX_VALUE")
    private Long projectId;

    @NotNull(message = "Stage roles must not be null")
    @NotBlank(message = "Stage roles must not be blank")
    private List<Long> stageRoleIds;

    @NotNull(message = "Team members must not be null")
    @NotBlank(message = "Team members must not be blank")
    private List<Long> teamMemberIds;
}
