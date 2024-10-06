package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import org.springframework.stereotype.Component;

@Component
public class DescriptionFilter implements TaskFilter {
    @Override
    public boolean isApplicable(TaskFilterDto filters) {
        return filters.getDescription() != null;
    }

    @Override
    public boolean apply(Task task, TaskFilterDto taskFilterDto) {
        return task.getDescription().equals(taskFilterDto.getDescription());
    }
}
