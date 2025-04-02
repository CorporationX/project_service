package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record TaskFilterDto(

        TaskStatus status,

        @Positive(message = "Performer ID must be positive")
        Long performerId,

        @Size(max = 100, message = "Keyword too long")
        String keyword
) {
}
