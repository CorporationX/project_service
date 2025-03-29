package faang.school.projectservice.service;

import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.exception.TaskNotFoundException;
import faang.school.projectservice.filter.PerformerFilter;
import faang.school.projectservice.filter.KeywordFilter;
import faang.school.projectservice.filter.StatusFilter;
import faang.school.projectservice.filter.TaskFilter;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.validator.TaskValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
@Qualifier("taskServiceImpl")
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskValidator taskValidator;

    @Override
    @Transactional
    public TaskDto createTask(TaskDto taskDto) {
        long currentUserId = taskValidator.validateUserParticipationAndGetUserId();
        Task task = taskMapper.toEntity(taskDto);
        task.setReporterUserId(currentUserId);
        Task savedTask = taskRepository.save(task);
        return taskMapper.toDto(savedTask);
    }

    @Override
    @Transactional
    public TaskDto updateTask(Long id, TaskDto taskDto) {
        long currentUserId = taskValidator.validateUserParticipationAndGetUserId();
        Task existingTask = getTaskOrThrow(id);
        Task updatedTask = updateTaskFields(existingTask, taskDto);
        Task savedTask = taskRepository.save(updatedTask);
        return taskMapper.toDto(savedTask);
    }

    @Override
    public List<TaskDto> getFilteredTasks(TaskFilterDto filterDto) {
        long currentUserId = taskValidator.validateUserParticipationAndGetUserId();
        List<Task> tasks = taskRepository.findAll();
        List<TaskFilter> filters = createFilters(filterDto);
        List<Task> filteredTasks = tasks.stream()
                .filter(task -> filters.stream().allMatch(filter -> filter.test(task)))
                .collect(toList());
        return taskMapper.toDtoList(filteredTasks);
    }

    @Override
    public List<TaskDto> getAllTasks() {
        long currentUserId = taskValidator.validateUserParticipationAndGetUserId();
        return taskMapper.toDtoList(taskRepository.findAll());
    }

    @Override
    public TaskDto getTaskById(long id) {
        long currentUserId = taskValidator.validateUserParticipationAndGetUserId();
        Task task = getTaskOrThrow(id);
        return taskMapper.toDto(task);
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

    private List<TaskFilter> createFilters(TaskFilterDto filterDto) {
        if (filterDto == null) {
            return Collections.emptyList();
        }
        List<TaskFilter> filters = new ArrayList<>();
        if (filterDto.getStatus() != null && !filterDto.getStatus().trim().isEmpty()) {
            filters.add(new StatusFilter(filterDto.getStatus()));
        }
        if (filterDto.getPerformerId() != null) {
            filters.add(new PerformerFilter(filterDto.getPerformerId()));
        }
        if (filterDto.getKeyword() != null && !filterDto.getKeyword().trim().isEmpty()) {
            filters.add(new KeywordFilter(filterDto.getKeyword()));
        }
        return filters;
    }
}
