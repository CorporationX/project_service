package faang.school.projectservice.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

@Builder
public record TaskCreateRequest(

        @NotBlank(message = "Name cannot be blank")
        @Size(min = 3, max = 50, message = "Name size must be between {min} and {max} symbols")
        String name,

        @Size(max = 5000, message = "Task description size more max description size ({max})")
        String description,

        @Positive(message = "Performer id must be positive")
        @NotNull(message = "Id performer is missing")
        Long performerUserId,

        @Positive(message = "Reporter id must be positive")
        @NotNull(message = "Id reporter is missing")
        Long reporterUserId,

        @PositiveOrZero(message = "Tracked time cannot be negative")
        Integer minutesTracked,

        @Positive(message = "Parent task id must be positive")
        Long parentTaskId,

        List<Long> linkedTasksIds,

        @Positive(message = "Project id must be positive")
        @NotNull(message = "Id project is missing")
        Long projectId
) {
}
