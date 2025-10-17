package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record StageRequestAllStageDto(
        @NotNull Long projectId,
        List<@NotNull TeamRole> teamRoleList,
        TaskStatus taskStatus
        ) {
}