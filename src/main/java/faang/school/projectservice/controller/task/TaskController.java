package faang.school.projectservice.controller.task;

import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.service.task.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/task")
    public void createTask (@RequestBody TaskDto taskDto) {
        taskService.createTask(taskDto);
    }

    @PutMapping("/task/{id}")
    public void updateTask (@PathVariable Long id, @RequestBody TaskDto taskDto) {
        taskService.updateTask(id,taskDto);
    }

    @PostMapping("/tasks/{projectId}")
    public void findAll (@PathVariable Long projectId, @RequestBody TaskFilterDto taskFilterDto) {
        taskService.getAllTasks(projectId,taskFilterDto);
    }

    @GetMapping("/task/{taskId}")
    public TaskDto getTask (@PathVariable Long taskId) {
        return taskService.getTask(taskId);
    }
}
