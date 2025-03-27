package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.TaskDto;
import faang.school.projectservice.service.task.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    //Получить задачу по id.
    @GetMapping("/task/{taskId}")
    public TaskDto getTaskById(long taskId) {
        //тут будет логика обращения к taskService get
        return taskService.getTaskById(taskId);
    }
}
