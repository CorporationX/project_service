package faang.school.projectservice.controller;

import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.service.TaskService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("api/v1/tasks")
@RestController
@RequiredArgsConstructor
public class TaskController {
    private final TaskMapper taskMapper;
    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto createTask(@RequestBody @Validated TaskDto taskDto) {
        Task tempTask = taskMapper.toEntity(taskDto);
        Task createdTask = taskService.createTask(tempTask);

        return taskMapper.toDto(createdTask);
    }

    @PutMapping("/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDto updateTask(@PathVariable @NotNull Long taskId,
                              @RequestBody @Validated TaskDto taskDto) {
        taskDto.setTaskId(taskId);
        Task tempTask = taskMapper.toEntity(taskDto);
        Task updatedlTask = taskService.updateTask(tempTask);
        return taskMapper.toDto(updatedlTask);
    }

    @PostMapping("/{projectId}/filters")
    public List<TaskDto> getFilteredTasks(@RequestParam @NotNull Long userId,
                                          @PathVariable @NotNull Long projectId,
                                          @RequestBody @Validated TaskFilterDto filters) {
        List<Task> filteredTasks = taskService.getTasks(userId, projectId, filters);
        return taskMapper.toDto(filteredTasks);
    }

    @GetMapping("/{projectId}/all")
    public List<TaskDto> getAllTasksByProject(@RequestParam @NotNull Long userId,
                                              @PathVariable @NotNull Long projectId) {
        List<Task> projectTasks = taskService.getTasks(userId, projectId, null);
        return taskMapper.toDto(projectTasks);
    }

    @GetMapping("/{taskId}")
    public TaskDto getTaskById(@PathVariable @NotNull Long taskId,
                               @RequestParam @NotNull Long userId) {
        Task foundTask = taskService.getTaskById(taskId, userId);
        return taskMapper.toDto(foundTask);
    }
}
