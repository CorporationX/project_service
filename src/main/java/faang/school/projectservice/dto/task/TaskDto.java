package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import lombok.Builder;

import java.util.List;

@Builder
public record TaskDto(
        Long id,
        String name,
        String description,
        TaskStatus status,
        Long performerUserId,
        Long reporterUserId,
        Integer minutesTracked,
        Long parentTaskId,
        List<Long> linkedTaskIds,
        Long projectId,
        Long stageId
) {
}