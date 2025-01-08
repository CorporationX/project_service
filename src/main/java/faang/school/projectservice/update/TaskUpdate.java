package faang.school.projectservice.update;

import faang.school.projectservice.dto.TaskDto;
import faang.school.projectservice.model.Task;

public interface TaskUpdate {

    boolean isApplicable(TaskDto taskDto);

    void apply(Task task, TaskDto taskDto);
}
