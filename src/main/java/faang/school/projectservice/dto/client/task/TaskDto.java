package faang.school.projectservice.dto.client.task;

import faang.school.projectservice.model.TaskStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskDto {
    private Long id;
    private String name;
    private String description;
    private TaskStatus status;
}
