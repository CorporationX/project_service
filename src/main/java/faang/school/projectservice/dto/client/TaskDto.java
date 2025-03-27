package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.TaskStatus;
import lombok.Data;

@Data
public class TaskDto {
    private Long id;
    private String name;
    private String description;
    private TaskStatus status;

}
