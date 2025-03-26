package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record TaskResponse(
        String name,
        String description,
        TaskStatus status,
        Long performerUserId,
        Long reporterUserId,
        LocalDateTime createdAt,
        LocalDateTime deadline,
        Long parentTaskId,
        List<Long> linkedTasksIds
) {
}
