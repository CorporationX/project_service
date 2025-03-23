package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaskFilterDto {
    private TaskStatus status;
    private Long performerUserId;
    private String namePattern;
}
