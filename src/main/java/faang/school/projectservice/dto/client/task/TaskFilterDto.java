package faang.school.projectservice.dto.client.task;

import faang.school.projectservice.model.TaskStatus;

public record TaskFilterDto(
        TaskStatus status,
        Long performerUserId,
        String name
) {
}
