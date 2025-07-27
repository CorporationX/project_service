package faang.school.projectservice.dto.client.task;

import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TaskUpdateDto(
        @NotNull
        String name,
        @NotNull
        String description,
        @NotNull
        TaskStatus status,
        @NotNull
        Long performerUserId,
        @NotNull
        Long reporterUserId,
        @NotNull
        Integer minutesTracked,
        @NotNull
        List<Long> linkedTasksId,
        @NotNull
        Long projectId,
        @NotNull
        Long stageId
) {
}
