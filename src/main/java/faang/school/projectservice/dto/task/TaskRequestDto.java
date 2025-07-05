package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.List;

public record TaskRequestDto(
        @NotBlank(message = "Name cannot be blank")
        @Size(max = 255, message = "Name must be at most 255 characters")
        String name,

        @Size(max = 4096, message = "Description must be at most 4096 characters")
        String description,

        @NotNull(message = "Status cannot be null")
        TaskStatus status,

        @NotNull(message = "PerformerUserId cannot be null")
        @Positive(message = "PerformerUserId must be positive")
        Long performerUserId,

        @NotNull(message = "ReporterUserId cannot be null")
        @Positive(message = "ReporterUserId must be positive")
        Long reporterUserId,

        @PositiveOrZero(message = "MinutesTracked must be positive or zero")
        Integer minutesTracked,

        Long parentTaskId,

        List<Long> linkedTaskIds,

        @NotNull(message = "ProjectId cannot be null")
        @Positive(message = "ProjectId must be positive")
        Long projectId,

        @NotNull(message = "StageId cannot be null")
        @Positive(message = "StageId must be positive")
        Long stageId
) {
}