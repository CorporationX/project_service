package faang.school.projectservice.filter.task;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskPerformerIdFilter implements TaskFilter {

    @Override
    public boolean isApplicable(TaskFilterDto filter) {
        return true;
    }

    @Override
    public Specification<Task> apply(TaskFilterDto filter) {
        return null;
    }
}
