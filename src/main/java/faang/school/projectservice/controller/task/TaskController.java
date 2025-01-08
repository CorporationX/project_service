package faang.school.projectservice.controller.task;

import faang.school.projectservice.dto.TaskDto;
import faang.school.projectservice.service.task.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("users/{userId}")
    public TaskDto createTask(@PathVariable Long userId, @Valid @RequestBody TaskDto taskDto) {
        return taskService.createTask(userId, taskDto);
    }

    @PutMapping("users/{userId}")
    public TaskDto updateTask(@PathVariable Long userId, @Valid @RequestBody TaskDto taskDto) {
        return taskService.updateTask(userId, taskDto);
    }

    @GetMapping("{taskId}/users/{userId}")
    public TaskDto getTaskById(@PathVariable Long userId, @PathVariable Long taskId){
        return taskService.getTaskById(userId, taskId);
    }

    @GetMapping("users/{userId}/projects/{projectId}")
    public List<TaskDto> getTasksByProject(@PathVariable Long userId, @PathVariable Long projectId){
        return taskService.getTasksByProject(userId, projectId);
    }
}
