package faang.school.projectservice.controller.task;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskRequestDto;
import faang.school.projectservice.dto.task.TaskResponseDto;
import faang.school.projectservice.mapper.task.TaskMapper;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Slf4j
@Validated
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;
    private final UserContext userContext;

    @PostMapping("/project/{projectId}")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponseDto createTask(
            @PathVariable @Positive Long projectId,
            @Valid @RequestBody TaskRequestDto taskRequestDto) {
        log.info("Received request to create task for project {} by user {}", projectId, userContext.getUserId());
        TaskDto createdTask = taskService.createTask(taskRequestDto, projectId);
        log.info("Task created with id {}", createdTask.id());
        return taskMapper.toResponseDto(createdTask);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable @Positive Long taskId,
            @Valid @RequestBody TaskRequestDto taskRequestDto
    ) {
        Long userId = userContext.getUserId();
        log.info("Received request to update task with id {} by user {}", taskId, userId);
        TaskDto updatedTask = taskService.updateTask(taskId, taskRequestDto); // No userId parameter
        log.info("Task updated with id {}", updatedTask.id());
        return ResponseEntity.ok(taskMapper.toResponseDto(updatedTask));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskResponseDto>> getAllTasksByProjectIdWithFilters(
            @PathVariable @Positive Long projectId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Long performerUserId,
            @RequestParam(required = false) String keyword
    ) {
        log.info("Received request to get all tasks for project {} with filters: status={}, performer={}, keyword={}",
                projectId, status, performerUserId, keyword);
        List<TaskDto> taskDtos = taskService.getAllTasksByProjectIdWithFilters(projectId, status, performerUserId,
                keyword);
        return buildTaskListResponse(projectId, taskDtos);
    }

    @GetMapping("/project/{projectId}/all")
    public ResponseEntity<List<TaskResponseDto>> getAllTasksByProjectId(
            @PathVariable @Positive Long projectId
    ) {
        log.info("Received request to get all tasks for project {}", projectId);
        List<TaskDto> taskDtos = taskService.getAllTasksByProjectId(projectId);
        return buildTaskListResponse(projectId, taskDtos);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponseDto> getTaskById(
            @PathVariable @Positive Long taskId
    ) {
        log.info("Received request to get task with id {}", taskId);
        TaskDto taskDto = taskService.getTaskById(taskId);
        log.info("Found task with id {}", taskDto.id());
        return ResponseEntity.ok(taskMapper.toResponseDto(taskDto));
    }

    private ResponseEntity<List<TaskResponseDto>> buildTaskListResponse(Long projectId, List<TaskDto> taskDtos) {
        List<TaskResponseDto> taskResponseDtos = taskDtos.stream()
                .map(taskMapper::toResponseDto)
                .toList();
        log.info("Found {} tasks for project {}", taskResponseDtos.size(), projectId);
        return ResponseEntity.ok(taskResponseDtos);
    }
}