package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record StageRequestAllStageDto(
        @NotNull Long projectId,
        @NotNull @NotBlank List<TeamRole> teamRoleList,
        @NotNull TaskStatus taskStatus
        ) {
}