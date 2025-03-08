package faang.school.projectservice.filter.task;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;

import java.util.Objects;

public class TaskPerformerUserFilter implements TaskFilter {
    @Override
    public boolean isApplicable(TaskFilterDto filters) {
        return filters.getPerformerUserId() != null;
    }

    @Override
    public boolean filterEntity(Task task, TaskFilterDto filters) {
        return Objects.equals(task.getPerformerUserId(), filters.getPerformerUserId());
    }
}
