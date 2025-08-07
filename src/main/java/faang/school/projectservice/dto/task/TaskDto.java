package faang.school.projectservice.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Data transfer object representing a single task")
public record TaskDto(

        @Schema(description = "Task name")
        String name,

        @Schema(description = "Task description")
        String description,

        @Schema(description = "Task creation date and time")
        LocalDateTime createdAt
) {
}
