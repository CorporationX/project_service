package faang.school.projectservice.controller;

import faang.school.projectservice.dto.task.TaskCreateDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.dto.task.TaskReadDto;
import faang.school.projectservice.dto.task.TaskUpdateDto;
import faang.school.projectservice.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public TaskReadDto create(@RequestBody @Validated TaskCreateDto createDto) {
        return taskService.create(createDto);
    }

    @PutMapping
    public TaskReadDto update(@RequestBody TaskUpdateDto updateDto) {
        return taskService.update(updateDto);
    }

    @PutMapping("/project/{projectId}/filter")
    public List<TaskReadDto> getAllFilteredTasksByProjectId(@PathVariable long projectId, @RequestBody TaskFilterDto filterDto) {
        return taskService.getAllFilteredTasksByProjectId(projectId, filterDto);
    }

    @GetMapping("/project/{projectId}")
    public List<TaskReadDto> getAllTasksByProjectId(@PathVariable long projectId) {
        return taskService.getAllTasksByProjectId(projectId);
    }

    @DeleteMapping("/{taskId}")
    public void delete(long taskId) {
        taskService.delete(taskId);
    }
}
