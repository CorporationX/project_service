package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskFilterDto {
    private String keyword;
    private TaskStatus status;
    private Long performerUserId;
}
