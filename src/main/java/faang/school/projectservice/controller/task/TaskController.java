package faang.school.projectservice.controller.task;

import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/v1/tasks")
@Tag(name = "Task Management", description = "API for task management")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(@Qualifier("taskServiceImpl") TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(
            summary = "Create a new task",
            description = "Creates a new task based on the provided task information"
    )
    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskDto taskDto) {
        TaskDto createdTask = taskService.createTask(taskDto);
        return ResponseEntity.ok(createdTask);
    }

    @Operation(
            summary = "Update an existing task",
            description = "Updates an existing task with the provided task information"
    )
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(
            @Parameter(description = "ID of the task to update") @PathVariable Long id,
            @Valid @RequestBody TaskDto taskDto
    ) {
        TaskDto updatedTask = taskService.updateTask(id, taskDto);
        return ResponseEntity.ok(updatedTask);
    }

    @Operation(
            summary = "Get tasks with filters",
            description = "Fetches tasks based on filters like status, performer, or keyword"
    )
    @GetMapping("/search")
    public ResponseEntity<List<TaskDto>> getTasksFiltered(
            @Parameter(description = "Filter tasks by status") @RequestParam(required = false) String status,
            @Parameter(description = "Filter tasks by performer ID") @RequestParam(required = false) Long performerId,
            @Parameter(description = "Filter tasks by a keyword in title or description") @RequestParam(required = false) String keyword
    ) {
        TaskFilterDto filterDto = new TaskFilterDto(status, performerId, keyword);
        List<TaskDto> tasks = taskService.getFilteredTasks(filterDto);
        return ResponseEntity.ok(tasks);
    }

    @Operation(
            summary = "Get all tasks",
            description = "Fetches a list of all tasks"
    )
    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        List<TaskDto> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @Operation(
            summary = "Get all tasks by project ID",
            description = "Fetches a list of all tasks by project ID"
    )
    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasksByProjectId(Long projectId) {
        List<TaskDto> tasks = taskService.getAllTasksByProjectId(projectId);
        return ResponseEntity.ok(tasks);
    }

    @Operation(
            summary = "Get a task by ID",
            description = "Fetches a task by its ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(
            @Parameter(description = "ID of the task to retrieve") @PathVariable Long id
    ) {
        TaskDto task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }
}
