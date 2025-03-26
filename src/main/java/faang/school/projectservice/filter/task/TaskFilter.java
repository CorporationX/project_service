package faang.school.projectservice.filter.task;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import org.springframework.data.jpa.domain.Specification;

public interface TaskFilter {

    boolean isApplicable(TaskFilterDto filter);

    Specification<Task> apply(TaskFilterDto filter);
}
