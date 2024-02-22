package faang.school.projectservice.dto;

import lombok.Data;

@Data
public class TaskCompletedEvent {
    private long userId;
    private long projectId;
    private long taskId;
}
