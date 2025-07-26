package faang.school.projectservice.controller.task;

import faang.school.projectservice.dto.client.task.TaskCreateDto;
import faang.school.projectservice.dto.client.task.TaskFilterDto;
import faang.school.projectservice.dto.client.task.TaskUpdateDto;
import faang.school.projectservice.dto.client.task.TaskViewDto;
import faang.school.projectservice.service.task.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService service;

    @PostMapping
    public ResponseEntity<TaskViewDto> createTask(@RequestBody @Valid TaskCreateDto createDto) {
        return ResponseEntity.ok(service.createTask(createDto));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskViewDto> updateTask(@PathVariable long taskId,
                                                  @RequestBody @Valid TaskUpdateDto updateDto) {
        return ResponseEntity.ok(service.updateTask(taskId, updateDto));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<List<TaskViewDto>> getTaskByFilter(@PathVariable Long projectId,
                                                             @RequestBody TaskFilterDto filterDto) {
        return ResponseEntity.ok(service.getByFilter(filterDto, projectId));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskViewDto> getTaskById(@PathVariable long taskId) {
        return ResponseEntity.ok(service.getById(taskId));
    }
}
