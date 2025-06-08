package faang.school.projectservice.service.task;

import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskFilterDto;

import java.util.List;

public interface TaskService {
    void createTask(TaskDto taskDto);

    void updateTask(Long taskId, TaskDto taskDto);

    List<TaskDto> getAllTasks(Long projectId, TaskFilterDto taskFilterDto);

    TaskDto getTask(Long taskId);
}
