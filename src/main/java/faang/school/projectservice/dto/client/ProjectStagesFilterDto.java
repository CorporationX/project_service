package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ProjectStagesFilterDto(@NotNull Long projectId,
                                     List<TeamRole> roles,
                                     List<TaskStatus> taskStatuses
) {
}