package faang.school.projectservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskCompletedEvent {
    private Long userId;
    private Long projectId;
    private Long taskId;
}
