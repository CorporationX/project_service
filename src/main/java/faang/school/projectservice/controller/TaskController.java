package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.TaskDto;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.service.task.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final UserContext userContext;

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@RequestBody TaskDto taskDto) {
        Long userId = userContext.getUserId();
        log.info("Получен запрос на создание задачи от пользователя с ID: {}", userId);
        try {
            TaskDto createdTask = taskService.createTask(taskDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
        } catch (Exception e) {
            log.error("Ошибка при создании задачи: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long taskId, @RequestBody TaskDto taskDto) {
        Long userId = userContext.getUserId();
        log.info("Получен запрос на обновление задачи с ID: {} от пользователя с ID: {}", taskId, userId);
        try {
            TaskDto updatedTask = taskService.updateTask(taskId, taskDto, userId);
            log.info("Задача с ID: {} успешно обновлена пользователем с ID: {}", taskId, userId);
            return ResponseEntity.ok(updatedTask);
        } catch (SecurityException e) {
            log.warn("Пользователь с ID {} не имеет прав на обновление задачи с ID {}", userId, taskId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException e) {
            log.error("Задача с ID {} не найдена", taskId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Неожиданная ошибка при обновлении задачи с ID {}: {}", taskId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/project/{projectId}/filtered")
    public ResponseEntity<List<TaskDto>> getFilteredTasks(
            @PathVariable Long projectId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Long performerId) {
        Long userId = userContext.getUserId();
        log.info("Получен запрос на получение отфильтрованных задач проекта {} от пользователя с ID: {}", projectId, userId);
        try {
            List<TaskDto> tasks = taskService.getFilteredTasks(projectId, status, performerId);
            return ResponseEntity.ok(tasks);
        } catch (SecurityException e) {
            log.warn("Пользователь с ID {} не имеет доступа к задачам проекта {}", userId, projectId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("Ошибка при получении отфильтрованных задач проекта {}: {}", projectId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskDto>> getAllTasks(@PathVariable Long projectId) {
        Long userId = userContext.getUserId();
        log.info("Получен запрос на получение всех задач проекта {} от пользователя с ID: {}", projectId, userId);
        try {
            List<TaskDto> tasks = taskService.getAllTasksByProjectId(projectId);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            log.error("Ошибка при получении задач проекта {}: {}", projectId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long taskId) {
        Long userId = userContext.getUserId();
        log.info("Получен запрос на получение задачи с ID: {} от пользователя с ID: {}", taskId, userId);
        try {
            TaskDto task = taskService.getTaskById(taskId);
            log.info("Задача успешно получена: {}", task);
            return ResponseEntity.ok(task);
        } catch (IllegalArgumentException e) {
            log.warn("Задача с ID {} не найдена", taskId);
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            log.warn("Пользователь с ID {} не имеет доступа к задаче с ID {}", userId, taskId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            log.error("Произошла ошибка при получении задачи с ID {}: {}", taskId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
