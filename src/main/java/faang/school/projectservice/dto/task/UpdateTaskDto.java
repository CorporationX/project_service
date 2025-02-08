package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.util.Collections;
import java.util.List;

@Builder
public record UpdateTaskDto(
        @NotEmpty String name,
        String description,
        TaskStatus status,
        @Positive Long performerUserId,
        @PositiveOrZero Integer minutesTracked,
        @Positive Long parentTaskId,
        List<Long> linkedTaskIds,
        @Positive Long stageId
) {
    public UpdateTaskDto {
        linkedTaskIds = linkedTaskIds != null ? linkedTaskIds : Collections.emptyList();
    }
}