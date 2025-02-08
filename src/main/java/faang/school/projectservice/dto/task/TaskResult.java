package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record TaskResult(
        Long id,
        String name,
        String description,
        TaskStatus status,
        Long performerUserId,
        Long reporterUserId,
        Integer minutesTracked,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long parentTaskId,
        List<Long> linkedTaskIds,
        Long projectId,
        Long stageId
) {}
