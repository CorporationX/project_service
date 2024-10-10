package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import org.springframework.stereotype.Component;

@Component
public class PerformerUserFilter implements TaskFilter {
    @Override
    public boolean isApplicable(TaskFilterDto filters) {
        return filters.getPerformerUserId() != null;
    }

    @Override
    public boolean apply(Task task, TaskFilterDto filters) {
        return task.getPerformerUserId().equals(filters.getPerformerUserId());
    }
}
