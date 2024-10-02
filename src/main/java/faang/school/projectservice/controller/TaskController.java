package faang.school.projectservice.controller;

import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.service.TaskService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("api/v1/tasks")
@RestController
@RequiredArgsConstructor
public class TaskController {
    private final TaskMapper taskMapper;
    private final TaskService taskService;

    @PostMapping("/create")
    TaskDto createTask(@RequestBody @Validated TaskDto taskDto) {
        Task tempTask = taskMapper.toEntity(taskDto);
        Task originalTask = taskService.createTask(tempTask);

        return taskMapper.toDto(originalTask);
    }

    @PutMapping()
    TaskDto updateTask(@RequestParam @NotNull Long taskId, @RequestBody @Validated TaskDto taskDto) {
        taskDto.setTaskId(taskId);
        Task tempTask = taskMapper.toEntity(taskDto);
        Task originalTask = taskService.updateTask(tempTask);
        return taskMapper.toDto(originalTask);
    }

    @PostMapping("/filters")
    public List<TaskDto> getFilteredTasks(@RequestParam @NotNull Long requestingUserId, @RequestBody TaskFilterDto filters) {
        List<Task> tasks = taskService.getFilteredTasks(requestingUserId, filters);
        return taskMapper.toDto(tasks);
    }

    @GetMapping("/all")
    public List<TaskDto> getAllTasksByProject(@RequestParam @NotNull Long userId, @RequestParam @NotNull Long projectId) {
        List<Task> tasks = taskService.getAllTasksByProject(userId, projectId);
        return taskMapper.toDto(tasks);
    }

    @GetMapping("/id")
    public TaskDto getTaskById(@RequestParam @NotNull Long taskId, @RequestParam @NotNull Long userId) {
        Task task = taskService.getTaskById(taskId, userId);
        return taskMapper.toDto(task);
    }
}
