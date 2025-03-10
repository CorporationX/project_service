package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TaskUpdateDto {
    private long id;
    private String description;
    private TaskStatus status;
    private Long performerUserId;
    private Long parentTaskId;
    private List<Long> linkedTasksId;
}
