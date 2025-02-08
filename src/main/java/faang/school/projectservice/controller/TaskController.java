package faang.school.projectservice.controller;

import faang.school.projectservice.dto.task.CreateTaskDto;
import faang.school.projectservice.dto.task.TaskGettingDto;
import faang.school.projectservice.dto.task.TaskResult;
import faang.school.projectservice.dto.task.UpdateTaskDto;
import faang.school.projectservice.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public TaskResult createTask(@Valid @RequestBody CreateTaskDto createTaskDto) {
        return taskService.createTask(createTaskDto);
    }

    @PutMapping("/{taskId}")
    public TaskResult updateTask(@Valid @RequestBody UpdateTaskDto updateTaskDto,
                                 @PathVariable Long taskId,
                                 @RequestParam Long userId) {
        return taskService.updateTask(updateTaskDto, taskId, userId);
    }

    @GetMapping("/project/{projectId}/filtered")
    public List<TaskResult> getProjectTasksFilter(@Valid @RequestBody TaskGettingDto taskGettingDto,
                                           @PathVariable Long projectId,
                                           @RequestParam Long userId) {
        return taskService.getTasksFilter(taskGettingDto, userId, projectId);
    }

    @GetMapping("/project/{projectId}")
    public List<TaskResult> getTasks(@RequestParam Long userId,
                                     @PathVariable Long projectId) {
        return taskService.getProjectTasks(userId, projectId);
    }

    @GetMapping("/{taskId}")
    public TaskResult getTaskById(@PathVariable Long taskId) {
        return taskService.findTaskByIdToDto(taskId);
    }
}
