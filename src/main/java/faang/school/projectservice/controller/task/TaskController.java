package faang.school.projectservice.controller.task;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.filter.TaskFilterDto;
import faang.school.projectservice.service.task.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final UserContext userContext;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto createTask(@RequestBody @Valid TaskDto taskDto) {
        return taskService.createTask(taskDto);
    }

    @PutMapping("/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDto updateTask(@PathVariable Long taskId, @RequestBody TaskDto taskDto) {
        return taskService.updateTask(taskId, taskDto);
    }

    @GetMapping("/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDto getTaskById(@PathVariable Long taskId) {
        return taskService.getTaskById(taskId, userContext.getUserId());
    }

    @GetMapping("/project/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public List<TaskDto> getTasksByProjectId(@PathVariable Long projectId) {
        return taskService.getTasksByProjectId(projectId, userContext.getUserId());
    }

    @GetMapping("/project/{projectId}/filter")
    @ResponseStatus(HttpStatus.OK)
    public List<TaskDto> getAllVacanciesByFilter(@PathVariable Long projectId, @RequestBody TaskFilterDto filter) {
        return taskService.getTasksByProjectIdAndFilter(projectId, userContext.getUserId(), filter);
    }
}
