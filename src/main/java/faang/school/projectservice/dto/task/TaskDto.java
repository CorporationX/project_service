package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TaskDto {
    private TaskStatus taskStatus;
}