package faang.school.projectservice.filter.task;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;

public class TaskNameFilter implements TaskFilter {
    @Override
    public boolean isApplicable(TaskFilterDto filters) {
        return filters.getNamePattern() != null;
    }

    @Override
    public boolean filterEntity(Task task, TaskFilterDto filters) {
        return task.getName().contains(filters.getNamePattern());
    }
}
