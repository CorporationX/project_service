package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.TaskDto;
import faang.school.projectservice.service.task.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final UserContext userContext;

    //TODO: "Получить задачу по id"
    @GetMapping("/task/{taskId}")
    public TaskDto getTaskById(@PathVariable Long taskId) {
        //todo: тут будет валидация + логика обращения к taskService с исключением
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
