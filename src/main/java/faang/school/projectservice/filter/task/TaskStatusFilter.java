package faang.school.projectservice.filter.task;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskStatusFilter implements TaskFilterStrategy{
    @Override
    public boolean filter(Task task, TaskFilterDto taskFilterDto) {
        return taskFilterDto.getStatus() == task.getStatus();
    }
    @Override
    public boolean isAppicable (TaskFilterDto taskFilterDto) {
        return taskFilterDto.getStatus() != null;
    }
}
