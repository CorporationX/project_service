package faang.school.projectservice.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class TaskCompletedEvent {
    private long userId;
    private long projectId;
    private long taskId;
}
