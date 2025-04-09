package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

@Builder
public record TaskUpdateRequest(

        @NotNull(message = "Task id is missing")
        @Positive(message = "Id must be positive")
        Long id,

        @Size(max = 5000, message = "Task description size more max description size ({max})")
        String description,

        @NotNull(message = "Invalid task status")
        TaskStatus status,

        @PositiveOrZero(message = "Tracked time cannot be negative")
        Integer minutesTracked,

        @Positive(message = "Performer id must be positive")
        Long performerUserId,

        @Positive(message = "Parent task id must be positive")
        Long parentTaskId,

        List<Long> linkedTasksIds
) {
}
