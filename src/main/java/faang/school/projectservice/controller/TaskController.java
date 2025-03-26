package faang.school.projectservice.controller;

import faang.school.projectservice.dto.task.TaskCreateRequest;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.dto.task.TaskResponse;
import faang.school.projectservice.dto.task.TaskUpdateRequest;
import faang.school.projectservice.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public TaskResponse createTask(@RequestBody TaskCreateRequest taskDto) {
        return taskService.createTask(taskDto);
    }

    @PutMapping
    public TaskResponse updateTask(@RequestBody TaskUpdateRequest taskDto) {
        return taskService.updateTask(taskDto);
    }

    @PostMapping("/{projectId}")
    public List<TaskResponse> getAllTasksByFilters(@PathVariable Long projectId, @RequestBody TaskFilterDto filter) {
        return taskService.getAllTasksByFilters(projectId, filter);
    }

    @GetMapping("/{projectId}")
    public List<TaskResponse> getAllTasks(@PathVariable Long projectId) {
        return taskService.getAllTasks(projectId);
    }

    @GetMapping("/task-{taskId}")
    public TaskResponse getTaskById(@PathVariable Long taskId) {
        return taskService.getTaskById(taskId);
    }
}
