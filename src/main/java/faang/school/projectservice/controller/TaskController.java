package faang.school.projectservice.controller;

import faang.school.projectservice.dto.TaskDto;
import faang.school.projectservice.integration.jira.queue.JiraTaskQueue;
import faang.school.projectservice.integration.jira.service.JiraIntegrationService;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TaskRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
@RequiredArgsConstructor
@Tag(name = "Project Tasks", description = "API для управления задачами проекта с Jira интеграцией")
public class TaskController {
    
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final JiraTaskQueue jiraTaskQueue;
    private final JiraIntegrationService jiraIntegrationService;
    
    // ==========================================
    // CREATE Operations
    // ==========================================
    
    @Operation(
        summary = "Создать задачу",
        description = "Создаёт задачу в проекте и синхронизирует с Jira"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "202",
                    description = "Задача принята к созданию",
                    content = @Content(schema = @Schema(implementation = TaskDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Проект не найден")
    })
    @PostMapping
    public ResponseEntity<TaskDto> createTask(
        @Parameter(description = "ID проекта", required = true)
        @PathVariable Long projectId,
        
        @Parameter(description = "ID пользователя (для Oauth)", required = false)
        @RequestHeader(value = "X-User-Id", required = false) Long userId,
        
        @Valid @RequestBody TaskDto taskDto
    ) {
        log.info("Creating task for project: {}, user: {}", projectId, userId);
        
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        
        Task task = Task.builder()
            .name(taskDto.getName())
            .description(taskDto.getDescription())
            .status(taskDto.getStatus() != null ? taskDto.getStatus() : TaskStatus.TODO)
            .performerUserId(taskDto.getPerformerUserId())
            .reporterUserId(taskDto.getReporterUserId() != null ? taskDto.getReporterUserId() : userId)
            .project(project)
            .build();
        
        task = taskRepository.save(task);
        
        jiraTaskQueue.queueTaskCreation(task, userId);
        
        return ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .body(toDto(task));
    }
    
    // ==========================================
    // READ Operations
    // ==========================================
    
    @Operation(
        summary = "Получить задачи проекта",
        description = "Возвращает список задач проекта с возможностью фильтрации"
    )
    @GetMapping
    public ResponseEntity<List<TaskDto>> getTasks(
        @PathVariable Long projectId,
        
        @Parameter(description = "Фильтр по статусу")
        @RequestParam(required = false) TaskStatus status,
        
        @Parameter(description = "Фильтр по исполнителю")
        @RequestParam(required = false) Long performerUserId
    ) {
        log.info("Getting tasks for project: {}, status: {}, performerUserId: {}", 
            projectId, status, performerUserId);
        
        List<Task> tasks = taskRepository.findByProjectIdAndFilters(projectId, status, performerUserId);
        
        return ResponseEntity.ok(
            tasks.stream().map(this::toDto).collect(Collectors.toList())
        );
    }
    
    @Operation(
        summary = "Получить задачу по ID",
        description = "Возвращает детальную информацию о задаче"
    )
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDto> getTask(
        @PathVariable Long projectId,
        @PathVariable Long taskId
    ) {
        log.info("Getting task: {}", taskId);
        
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
        
        return ResponseEntity.ok(toDto(task));
    }
    
    // ==========================================
    // UPDATE Operations
    // ==========================================
    
    @Operation(
        summary = "Обновить задачу",
        description = "Обновляет задачу и синхронизирует изменения с Jira"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Задача обновлена",
                    content = @Content(schema = @Schema(implementation = TaskDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDto> updateTask(
        @PathVariable Long projectId,
        @PathVariable Long taskId,
        @RequestHeader(value = "X-User-Id", required = false) Long userId,
        @Valid @RequestBody TaskDto taskDto
    ) {
        log.info("Updating task: {}, user: {}", taskId, userId);
        
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
        
        if (taskDto.getName() != null) {
            task.setName(taskDto.getName());
        }
        if (taskDto.getDescription() != null) {
            task.setDescription(taskDto.getDescription());
        }
        if (taskDto.getStatus() != null) {
            task.setStatus(taskDto.getStatus());
        }
        if (taskDto.getPerformerUserId() != null) {
            task.setPerformerUserId(taskDto.getPerformerUserId());
        }
        
        task = taskRepository.save(task);
        
        jiraTaskQueue.queueTaskUpdate(task, userId);
        
        return ResponseEntity.ok(toDto(task));
    }
    
    // ==========================================
    // DELETE Operations
    // ==========================================
    
    @Operation(
        summary = "Удалить задачу",
        description = "Удаляет задачу из системы и Jira"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Задача удалена"),
        @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
        @PathVariable Long projectId,
        @PathVariable Long taskId,
        @RequestHeader(value = "X-User-Id", required = false) Long userId
    ) {
        log.info("Deleting task: {}, user: {}", taskId, userId);
        
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
        
        if (task.getJiraIssueKey() != null) {
            jiraTaskQueue.queueTaskDeletion(taskId, task.getJiraIssueKey(), userId);
        }
        
        taskRepository.delete(task);
        
        return ResponseEntity.noContent().build();
    }
    
    // ==========================================
    // Helper Methods
    // ==========================================
    
    private TaskDto toDto(Task task) {
        return TaskDto.builder()
            .id(task.getId())
            .name(task.getName())
            .description(task.getDescription())
            .status(task.getStatus())
            .performerUserId(task.getPerformerUserId())
            .reporterUserId(task.getReporterUserId())
            .projectId(task.getProject() != null ? task.getProject().getId() : null)
            .parentTaskId(task.getParentTask() != null ? task.getParentTask().getId() : null)
            .jiraIssueKey(task.getJiraIssueKey())
            .jiraIssueId(task.getJiraIssueId())
            .build();
    }
}

