package faang.school.projectservice.dto.client.task;

import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TaskCreateDto(
        @NotNull
        String name,
        @NotNull
        String description,
        @NotNull
        TaskStatus status,
        @NotNull
        Long parentTaskId,
        @NotNull
        List<Long> linkedTasksId,
        @NotNull
        Long projectId,
        @NotNull
        Long stageId
) {
}
