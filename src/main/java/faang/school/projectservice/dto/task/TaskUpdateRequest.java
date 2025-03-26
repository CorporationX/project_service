package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record TaskUpdateRequest(

        @NotNull(message = "Task id is missing")
        Long id,

        String description,
        TaskStatus status,
        Integer minutesTracked,
        Long performerUserId,
        Long parentTaskId,
        List<Long> linkedTasksIds
) {
}
