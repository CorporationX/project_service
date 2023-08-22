package faang.school.projectservice.dto.redis;

import lombok.Data;

@Data
public class TaskCompletedEvent {
    private Long userId;
    private Long projectId;
    private Long taskId;
}
