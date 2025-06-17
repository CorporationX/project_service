package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskRequestDto;
import faang.school.projectservice.exception.TaskEntityNotFoundException;
import faang.school.projectservice.exception.TaskValidationException;
import faang.school.projectservice.mapper.task.TaskMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ProjectService projectService;
    private final UserContext userContext;

    @Transactional
    public TaskDto createTask(TaskRequestDto taskRequestDto, Long projectId) {
        if (taskRequestDto.stageId() == null) {
            throw new TaskValidationException("StageId can't be null");
        }
        Project project = projectService.getProjectById(projectId);
        Long reporterUserId = userContext.getUserId();

        Task taskEntity = taskMapper.toEntity(taskRequestDto);
        taskEntity.setProject(project);
        taskEntity.setReporterUserId(reporterUserId);
        Task savedTask = taskRepository.save(taskEntity);
        TaskDto taskDto = taskMapper.toDto(savedTask);

        return new TaskDto(
                taskDto.id(),
                taskDto.name(),
                taskDto.description(),
                taskDto.status(),
                taskDto.performerUserId(),
                taskDto.reporterUserId(),
                taskDto.minutesTracked(),
                taskDto.parentTaskId(),
                taskDto.linkedTaskIds(),
                projectId,
                taskDto.stageId()
        );
    }

    @Transactional
    public TaskDto updateTask(Long taskId, TaskRequestDto taskRequestDto) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isEmpty()) {
            throw new TaskEntityNotFoundException("Task with id " + taskId + " not found");
        }
        Task existingTask = optionalTask.get();
        taskMapper.toEntity(taskRequestDto, existingTask);
        existingTask.setReporterUserId(userContext.getUserId());
        Task savedTask = taskRepository.save(existingTask);
        return taskMapper.toDto(savedTask);
    }

    @Transactional
    public List<TaskDto> getAllTasksByProjectIdWithFilters(Long projectId, TaskStatus status, Long performerUserId,
                                                           String keyword) {
        List<Task> tasks = taskRepository.findTasksByFilters(projectId, status, performerUserId, keyword);
        return tasks.stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<TaskDto> getAllTasksByProjectId(Long projectId) {
        List<Task> tasks = taskRepository.findAllByProjectId(projectId);
        if (tasks.isEmpty()) {
            throw new TaskEntityNotFoundException("Tasks not found for project with id: " + projectId);
        }
        return tasks.stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskDto getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskEntityNotFoundException("Task not found with id: " + taskId));
        return taskMapper.toDto(task);
    }
}