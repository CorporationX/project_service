package faang.school.projectservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class StageDto {
    private Long stageId;

    @NotEmpty(message = "Stage name must not be empty")
    private String stageName;

    @NotNull(message = "Project ID must not be null")
    private Long projectId;

    private List<StageRoleDto> stageRoles;
    private List<TaskDto> tasks;
    private List<Long> executorsIds;
}
