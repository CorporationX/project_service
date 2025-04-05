package faang.school.projectservice.service.task;

import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.dto.task.TaskResponseDto;
import faang.school.projectservice.exception.TaskNotFoundException;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
@Qualifier("taskServiceImpl")
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskPermissionService taskPermissionService;

    @Override
    @Transactional
    public TaskResponseDto createTask(TaskDto taskDto) {
        long currentUserId = taskPermissionService.validateTaskAccess();
        Task task = taskMapper.toEntity(taskDto);
        task.setReporterUserId(currentUserId);
        if (taskDto.getParentTaskId() != null && taskDto.getParentTaskId() != 0) {
            Task parentTask = getTaskOrThrow(taskDto.getParentTaskId());
            task.setParentTask(parentTask);
        } else {
            task.setParentTask(null);
        }
        if (taskDto.getLinkedTaskIds() != null) {
            validateLinkedTasks(taskDto.getLinkedTaskIds());
        }
        Task savedTask = taskRepository.save(task);
        return taskMapper.toResponseDto(savedTask);
    }

    @Override
    @Transactional
    public TaskResponseDto updateTask(Long id, TaskDto taskDto) throws AccessDeniedException {
        Task existingTask = getTaskOrThrow(id);
        taskPermissionService.validateTaskUpdatePermission(existingTask);
        Task updatedTask = updateTaskFields(existingTask, taskDto);
        Task savedTask = taskRepository.save(updatedTask);
        return taskMapper.toResponseDto(savedTask);
    }

    @Override
    public List<TaskResponseDto> getFilteredTasks(TaskFilterDto filterDto) {
        List<Task> tasks = taskRepository.findFilteredTasks(
                filterDto.getStatus(),
                filterDto.getPerformerId(),
                filterDto.getKeyword()
        );
        return taskMapper.toResponseDtoList(tasks);
    }

    @Override
    public List<TaskResponseDto> getAllTasks() {
        return taskMapper.toResponseDtoList(taskRepository.findAll());
    }

    @Override
    public List<TaskResponseDto> getAllTasksByProjectId(Long projectId) {
        return taskMapper.toResponseDtoList(taskRepository.findAllByProjectId(projectId));
    }

    @Override
    public TaskResponseDto getTaskById(long id) {
        return taskMapper.toResponseDto(getTaskOrThrow(id));
    }

    @Override
    public void deleteTask(Long id) throws AccessDeniedException {
        Task task = getTaskOrThrow(id);
        taskPermissionService.validateTaskDeletePermission(task);
        taskRepository.delete(task);
    }

    private void validateLinkedTasks(List<Long> linkedTaskIds) {
        linkedTaskIds.forEach(this::getTaskOrThrow);
    }

    private Task getTaskOrThrow(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id " + id));
    }

    private Task updateTaskFields(Task existingTask, TaskDto taskDto) {
        Task.TaskBuilder builder = existingTask.toBuilder()
                .name(taskDto.getName())
                .description(taskDto.getDescription())
                .status(taskDto.getStatus())
                .performerUserId(taskDto.getPerformerUserId());

        if (taskDto.getParentTaskId() != null) {
            Task parentTask = getTaskOrThrow(taskDto.getParentTaskId());
            builder.parentTask(parentTask);
        } else {
            builder.parentTask(null);
        }

        if (taskDto.getLinkedTaskIds() != null && !taskDto.getLinkedTaskIds().isEmpty()) {
            List<Task> linkedTasks = taskDto.getLinkedTaskIds().stream()
                    .map(this::getTaskOrThrow)
                    .toList();
            builder.linkedTasks(linkedTasks);
        } else {
            builder.linkedTasks(Collections.emptyList());
        }
        return builder.build();
    }
}
