package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.NonNull;

import java.util.Collections;
import java.util.List;

@Builder
public record CreateTaskDto(
        @NotBlank String name,
        @Size(max = 1024) String description,
        @Enumerated(EnumType.STRING) TaskStatus status,
        @Positive Long stageId,
        @Positive Long parentTaskId,
        List<@Positive Long> linkedTaskIds,
        @PositiveOrZero Integer minutesTracked,
        @NonNull @Positive Long performerUserId,
        @NonNull @Positive Long reporterUserId,
        @NonNull @Positive Long projectId
) {
    public CreateTaskDto {
        linkedTaskIds = linkedTaskIds != null ? linkedTaskIds : Collections.emptyList();
    }
}
