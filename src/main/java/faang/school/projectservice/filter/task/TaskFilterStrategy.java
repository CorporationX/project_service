package faang.school.projectservice.filter.task;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;

public interface TaskFilterStrategy {
    boolean filter(Task task, TaskFilterDto taskFilterDto);
    boolean isApplicable(TaskFilterDto taskFilterDto);
}
