package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskUpdateDto {
    private long id;
    private String description;
    private TaskStatus status;
    private Long performerUserId;
    private Long parentTaskId;
    private List<Long> linkedTasksId;
}
