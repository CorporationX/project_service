package faang.school.projectservice.filter.task;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskKeyWordFilter implements TaskFilterStrategy {
    @Override
    public boolean filter(Task task, TaskFilterDto taskFilterDto) {
        return task.getDescription().toLowerCase().contains(taskFilterDto.getKeyword().toLowerCase());
    }
    @Override
    public boolean isAppicable (TaskFilterDto taskFilterDto) {
        return taskFilterDto.getKeyword() != null;
    }
}
