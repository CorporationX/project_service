package faang.school.projectservice.service.task;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.task.TaskFilterStrategy;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserContext userContext;
    private final List<TaskFilterStrategy> taskFilterStrategies;
    private final ProjectRepository projectRepository;

    @Transactional
    @Override
    public void createTask(TaskDto taskDto) {
        validateTaskAndProjectExistence(taskDto);
        taskRepository.save(taskMapper.toTask(taskDto));
    }

    @Transactional
    @Override
    public void updateTask(Long taskId, TaskDto taskDto) {
        Task task = taskRepository.findById(taskId).orElseThrow(
                ()-> new EntityNotFoundException(String.format("Task with id %d not found", taskId))
        );
        validateTaskAndProjectExistence(taskDto);
        task.setDescription(taskDto.getDescription());
        task.setStatus(taskDto.getStatus());
        task.setParentTask(taskDto.getParentTask());
        log.debug("Updating task: {} at {} : was updated by User with ID {}", task, LocalDateTime.now(), userContext.getUserId());
        task.setUpdatedAt(LocalDateTime.now());
        taskRepository.save(task);
    }

    @Override
    public List<TaskDto> getAllTasks(Long projectId, TaskFilterDto taskFilterDto) {
        hasAccessToTasks(projectId);
        if (taskFilterDto == null) {
            return taskMapper.toTaskDtoList(taskRepository.findAllByProjectId(projectId));
        }
        List<Task> allTasks = taskRepository.findAllByProjectId(projectId).stream()
                .filter(task -> filterTasks(taskFilterDto, task)).toList();
        return taskMapper.toTaskDtoList(allTasks);
    }

    @Override
    public TaskDto getTask(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Task with id %d not found", taskId))
        );
       Long projectId = task.getProject().getId();
       hasAccessToTasks(projectId);
       return taskMapper.toTaskDto(task);
    }

    private void validateTaskAndProjectExistence(TaskDto taskDto) {
        if (taskDto.getProject() == null) {
            log.error("No project for task with ID={}, taskName={}", taskDto.getId(), taskDto.getName());
            throw new DataValidationException(String.format("No project for task with ID=%s, taskName=%s"
                    , taskDto.getId(), taskDto.getName()));
        }
    }

    private boolean filterTasks(TaskFilterDto taskFilterDto, Task task) {
        return taskFilterStrategies.stream()
                .filter(strategy -> strategy.isApplicable(taskFilterDto))
                .allMatch(taskToFilter -> taskToFilter.filter(task, taskFilterDto));
    }

    private void hasAccessToTasks(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Project with id %d not found", projectId)));
        boolean isWorking = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .allMatch(teamMember -> teamMember.getId().equals(userContext.getUserId()));
        if (!isWorking) {
            log.error("User does not have permission to access tasks of project with ID = {}", projectId);
            throw new AccessDeniedException(String.format("User does not have permission to access tasks of project with ID = %d", projectId));
        }
    }


}
