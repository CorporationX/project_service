package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.TaskDto;
import faang.school.projectservice.service.task.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final UserContext userContext;

    //TODO: "1. Создание задачи. Задачи могут создавать все участники проекта.
    @PostMapping
    public ResponseEntity<TaskDto> createTask(@RequestBody TaskDto taskDto) {
        TaskDto createTask = taskService.createTask(taskDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createTask);
    }

    //TODO: "4. Получить все задачи проекта." Что-то надо докрутить в сервисе с валидацией.
    @GetMapping("/project/{projectId}")
    public List<TaskDto> getAllTasks(@PathVariable Long projectId) {
        Long userId = userContext.getUserId();
        log.info("Получен запрос от пользователя с ID: {}, на получение всех задач проекта: {}", userId, projectId);
        return taskService.getAllTasksByProjectId(projectId);
    }

    //TODO: "5. Получить задачу по id"
    @GetMapping("/task/{taskId}")
    public TaskDto getTaskById(@PathVariable Long taskId) {
        //todo: тут будет валидация + логика обращения к taskService с исключением
        userContext.getUserId();
        log.info("Получен запрос на получение задачи с ID: {}", taskId);
        try {
            TaskDto task = taskService.getTaskById(taskId);
            log.info("Задача успешно получена: {}", task);
            return task;
        } catch (Exception e) {
            log.error("Ошибка при получении задачи с ID {}: {}", taskId, e.getMessage(), e);
            throw e;
        }
    }
}
