package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class TaskReadDto {
    private long id;
    private String name;
    private String description;
    private Long projectId;
    private Long performerUserId;
    private Long reporterUserId;
    private Long parentTaskId;
    private List<Long> linkedTasksId;
    private TaskStatus status;
}
