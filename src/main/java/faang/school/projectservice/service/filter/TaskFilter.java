package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;

public interface TaskFilter {
    boolean isApplicable(TaskFilterDto filters);

    boolean apply(Task internship, TaskFilterDto filters);
}
