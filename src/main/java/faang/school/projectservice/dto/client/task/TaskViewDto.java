package faang.school.projectservice.dto.client.task;

import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;

import java.util.List;

public record TaskViewDto(
        String name,
        String description,
        TaskStatus status,
        Long performerUserId,
        Long reporterUserId,
        Integer minutesTracked,
        Long parentTaskId,
        List<Long> linkedTasksId,
        Long projectId,
        Long stageId
) {
}
