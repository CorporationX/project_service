package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.TaskStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskDto {
    private Long id;
    private String description;
    private TaskStatus status;
    private Long performerUserId;
    private LocalDateTime deadline;
}
