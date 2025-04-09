package faang.school.projectservice.controller.task;

import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.dto.task.TaskResponseDto;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.service.task.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
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
    public ResponseEntity<TaskResponseDto> createTask(@Valid @RequestBody TaskDto taskDto) {
        TaskResponseDto createdTask = taskService.createTask(taskDto);
        return ResponseEntity.ok(createdTask);
    }

    @Operation(
            summary = "Update an existing task",
            description = "Updates an existing task with the provided task information"
    )
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @Parameter(description = "ID of the task to update") @PathVariable Long id,
            @Valid @RequestBody TaskDto taskDto
    ) throws AccessDeniedException {
        TaskResponseDto updatedTask = taskService.updateTask(id, taskDto);
        return ResponseEntity.ok(updatedTask);
    }

    @Operation(summary = "Get filtered tasks")
    @GetMapping("/search")
    public ResponseEntity<List<TaskResponseDto>> getTasksFiltered(
            @Parameter(description = "Task status filter")
            @RequestParam(required = false) TaskStatus status,

            @Parameter(description = "Performer ID filter")
            @RequestParam(required = false) Long performerId,

            @Parameter(description = "Search keyword")
            @RequestParam(required = false) String keyword) {

        TaskFilterDto filterDto = TaskFilterDto.builder()
                .status(status != null ? TaskStatus.valueOf(status.getValue()) : null)
                .performerId(performerId)
                .keyword(keyword)
                .build();

        List<TaskResponseDto> tasks = taskService.getFilteredTasks(filterDto);
        return ResponseEntity.ok(tasks);
    }

    @Operation(
            summary = "Get all tasks",
            description = "Fetches a list of all tasks"
    )
    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAllTasks() {
        List<TaskResponseDto> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @Operation(
            summary = "Get all tasks by project ID",
            description = "Fetches a list of all tasks by project ID"
    )
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskResponseDto>> getAllTasksByProjectId(
            @Parameter(description = "ID проекта") @PathVariable Long projectId
    ) {
        List<TaskResponseDto> tasks = taskService.getAllTasksByProjectId(projectId);
        return ResponseEntity.ok(tasks);
    }

    @Operation(
            summary = "Get a task by ID",
            description = "Fetches a task by its ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(
            @Parameter(description = "ID of the task to retrieve") @PathVariable Long id
    ) {
        TaskResponseDto task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @Operation(
            summary = "Delete task by ID",
            description = "Deleting task by its ID"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "ID of the task to delete") @PathVariable Long id
    ) throws AccessDeniedException {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

}
