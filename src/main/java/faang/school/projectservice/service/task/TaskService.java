package faang.school.projectservice.service.task;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.filter.TaskFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.task.TaskMapper;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.validator.task.TaskValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskMapper taskMapper;
    private final TaskValidator taskValidator;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectService projectService;
    private final List<Filter<TaskFilterDto, Task>> taskFilters;

    @Transactional
    public TaskDto createTask(TaskDto taskDto) {
        taskValidator.validateTeamMember(taskDto.getReporterUserId(), taskDto.getProjectId());
        taskValidator.validateTeamMember(taskDto.getPerformerUserId(), taskDto.getProjectId());
        Task task = taskMapper.toEntity(taskDto);
        task.setProject(projectRepository.getProjectById(taskDto.getProjectId()));
        if (!taskValidator.isNull(taskDto.getParentTaskId())) {
            task.setParentTask(taskValidator.validateTask(taskDto.getParentTaskId()));
        }
        return taskMapper.toDto(taskRepository.save(task));
    }

    @Transactional
    public TaskDto updateTask(Long taskId, TaskDto taskDto) {
        Task task = taskValidator.validateTask(taskId);
        updateFields(task, taskDto);
        return taskMapper.toDto(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public TaskDto getTaskById(Long taskId, Long userId) {
        Task task = taskValidator.validateTask(taskId);
        validateUserRights(userId, task.getProject().getId());
        return taskMapper.toDto(task);
    }

    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByProjectId(Long projectId, Long userId) {
        projectService.getProjectById(projectId);
        validateUserRights(userId, projectId);
        return taskRepository.findAllByProjectId(projectId).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByProjectIdAndFilter(Long projectId, Long userId, TaskFilterDto filters) {
        projectService.getProjectById(projectId);
        validateUserRights(userId, projectId);
        Stream<Task> tasks = taskRepository.findAllByProjectId(projectId).stream();
        return taskFilters.stream()
                .filter(taskFilter -> taskFilter.isApplicable(filters))
                .flatMap(taskFilter -> taskFilter.apply(tasks, filters))
                .map(taskMapper::toDto)
                .collect(Collectors.toList());

    }

    private void validateUserRights(Long userId, Long projectId) {
        UserDto userDto = userServiceClient.getUser(userId);
        taskValidator.validateTeamMember(userDto.getId(), projectId);
    }

    private void updateFields(Task task, TaskDto taskDto) {
        if (!taskValidator.isNull(taskDto.getDescription())) {
            task.setDescription(taskDto.getDescription());
        }
        if (!taskValidator.isNull(taskDto.getStatus())) {
            task.setStatus(TaskStatus.valueOf(taskDto.getStatus()));
        }
        if (!taskValidator.isNull(taskDto.getPerformerUserId())) {
            task.setPerformerUserId(taskDto.getPerformerUserId());
        }
        if (!taskValidator.isNull(taskDto.getParentTaskId())) {
            Task parentTask = taskValidator.validateTask(taskDto.getParentTaskId());
            task.setParentTask(parentTask);
        }
        if (!taskValidator.isNull(taskDto.getLinkedTasksIds())) {
            List<Task> linkedTasks = taskDto.getLinkedTasksIds().stream()
                    .map(taskValidator::validateTask)
                    .collect(Collectors.toList());
            task.setLinkedTasks(linkedTasks);
        }
    }

    public void delete(Task task) {
        taskRepository.delete(task);
    }

    public void save(Task task) {
        taskRepository.save(task);
    }
}
