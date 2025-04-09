package faang.school.projectservice.service.task;

import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.dto.task.TaskResponseDto;
import jakarta.validation.Valid;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface TaskService {

    TaskResponseDto createTask(@Valid TaskDto taskDto);

    TaskResponseDto updateTask(Long id, @Valid TaskDto taskDto) throws AccessDeniedException;

    List<TaskResponseDto> getFilteredTasks(TaskFilterDto filterDto);

    List<TaskResponseDto> getAllTasks();

    List<TaskResponseDto> getAllTasksByProjectId(Long projectId);

    TaskResponseDto getTaskById(long id);

    void deleteTask(Long id) throws AccessDeniedException;
}
