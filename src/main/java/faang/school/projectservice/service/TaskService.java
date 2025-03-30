package faang.school.projectservice.service;

import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import jakarta.validation.Valid;

import java.util.List;

public interface TaskService {

    TaskDto createTask(@Valid TaskDto taskDto);

    TaskDto updateTask(Long id, @Valid TaskDto taskDto);

    List<TaskDto> getFilteredTasks(TaskFilterDto filterDto);

    List<TaskDto> getAllTasks();

    List<TaskDto> getAllTasksByProjectId(Long projectId);

    TaskDto getTaskById(long id);
}
