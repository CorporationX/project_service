package faang.school.projectservice.controller.task;

import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.service.task.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public void createTask(@RequestBody @Valid TaskDto taskDto) {
        taskService.createTask(taskDto);
    }

    @PutMapping("/{taskId}")
    public void updateTask(@PathVariable Long taskId, @RequestBody @Valid TaskDto taskDto) {
        taskService.updateTask(taskId, taskDto);
    }

    @PostMapping("/tasks/{projectId}")
    public void findAll(@PathVariable Long projectId, @RequestBody TaskFilterDto taskFilterDto) {
        taskService.getAllTasks(projectId, taskFilterDto);
    }

    @GetMapping("/{taskId}")
    public TaskDto getTask(@PathVariable Long taskId) {
        return taskService.getTask(taskId);
    }
}
