package faang.school.projectservice.dto.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.projectservice.model.TaskStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record TaskResponse(
        String name,
        String description,
        TaskStatus status,
        Long performerUserId,
        Long reporterUserId,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime deadline,

        Long parentTaskId,
        List<Long> linkedTasksIds
) {
}
