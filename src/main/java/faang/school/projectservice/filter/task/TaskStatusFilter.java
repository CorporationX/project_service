package faang.school.projectservice.filter.task;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;


public class TaskStatusFilter implements TaskFilter {
    @Override
    public boolean isApplicable(TaskFilterDto filters) {
        return filters.getStatus() != null;
    }

    @Override
    public boolean filterEntity(Task task, TaskFilterDto filters) {
        return task.getStatus() == filters.getStatus();
    }
}
