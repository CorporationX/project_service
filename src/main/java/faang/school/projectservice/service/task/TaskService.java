package faang.school.projectservice.service.task;

import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskFilterDto;

import java.util.List;

public interface TaskService {
    public void createTask(TaskDto taskDto);
    public void updateTask(Long taskId, TaskDto taskDto);
    public List<TaskDto> getAllTasks(Long projectId, TaskFilterDto taskFilterDto);
    public TaskDto getTask(Long taskId);
}
