package faang.school.projectservice.dto.client.task;

import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record TaskFilterDto(
        @NotNull
        Long projectId,
        TaskStatus status,
        Long performerUserId,
        String name
) {
}
