package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;

import java.util.List;

public record TaskResponseDto(
        Long id,
        String name,
        String description,
        TaskStatus status,
        Long performerUserId,
        Integer minutesTracked,
        Long parentTaskId,
        List<Long> linkedTaskIds,
        Long stageId
) {
}