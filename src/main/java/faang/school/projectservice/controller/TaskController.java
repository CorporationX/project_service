package faang.school.projectservice.controller;

import faang.school.projectservice.dto.task.CreateTaskDto;
import faang.school.projectservice.dto.task.ResponseTaskDto;
import faang.school.projectservice.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseTaskDto createTask(@RequestBody CreateTaskDto createTaskDto) {
        return taskService.createTask(createTaskDto);
    }
}
