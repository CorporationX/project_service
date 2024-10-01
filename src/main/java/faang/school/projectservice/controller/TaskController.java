package faang.school.projectservice.controller;

import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.service.TaskService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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


    @PostMapping("/filters")
    public List<TaskDto> getFilteredTasks(@RequestParam @NotNull Long requestingUserId, @RequestBody TaskFilterDto filters) {
        List<Task> tasks = taskService.getFilteredTasks(requestingUserId, filters);
        return taskMapper.toDto(tasks);
    }
}
