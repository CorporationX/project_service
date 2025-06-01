package faang.school.projectservice.filter.task;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskPerformerFilter implements TaskFilterStrategy {
    @Override
    public boolean filter(Task task, TaskFilterDto taskFilterDto) {
        return  task.getPerformerUserId().equals(taskFilterDto.getPerformerUserId());
    }
    @Override
    public boolean isAppicable (TaskFilterDto taskFilterDto) {
        return taskFilterDto.getPerformerUserId() != null;
    }
}
