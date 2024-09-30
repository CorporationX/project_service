package faang.school.projectservice.controller;

import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("api/v1/tasks")
@RestController
@RequiredArgsConstructor
public class TaskController {
    private final TaskMapper taskMapper;
    private final TaskService taskService;

    @PostMapping("/create")
    TaskDto createTask(@RequestBody @Validated TaskDto taskDto) {
        Task tempTask = taskMapper.toEntity(taskDto);
        Task originalTask = taskService.createTask(tempTask);

        return taskMapper.toDto(originalTask);
    }
}
