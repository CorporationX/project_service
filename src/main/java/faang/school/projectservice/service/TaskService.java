package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.task.TaskCreateRequest;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.dto.task.TaskResponse;
import faang.school.projectservice.dto.task.TaskUpdateRequest;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.task.TaskFilter;
import faang.school.projectservice.mapper.task.TaskRequestMapper;
import faang.school.projectservice.mapper.task.TaskResponseMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private static final String PROJECT_ENTITY_NAME = "Project";
    private static final String TASK_ENTITY_NAME = "Task";
    private static final String USER_ENTITY_NAME = "User";
    private static final String MESSAGE_ENTITY_NOT_FOUND = "%s with id %d not found";

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskRequestMapper taskRequestMapper;
    private final TaskResponseMapper taskResponseMapper;
    private final List<TaskFilter> filters;
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;

    public TaskResponse createTask(TaskCreateRequest taskDto) {
        Long userId = checkUserId();
        Project project = findProjectById(taskDto.projectId());
        checkUserOnMembership(userId, project);
        checkPerformerExisting(taskDto.performerUserId());

        Task task = taskRequestMapper.createdDtoToEntity(taskDto);
        task.setStatus(TaskStatus.TODO);
        task.setProject(project);
        setParentTask(task, taskDto.parentTaskId());
        setLinkedTasks(task, taskDto.linkedTasksIds());

        task = taskRepository.save(task);
        log.info("Successful created task with id {}\nDate created: {}. User id who created: {}",
                task.getId(), task.getCreatedAt(), userId);
        return taskResponseMapper.entityToDto(task);
    }

    public TaskResponse updateTask(TaskUpdateRequest taskDto) {
        Long userId = checkUserId();
        Task task = findTaskById(taskDto.id());
        Project project = task.getProject();

        if (taskDto.performerUserId() != null) {
            checkPerformerExisting(taskDto.performerUserId());
        }
        checkUserOnMembership(userId, project);

        taskRequestMapper.updateTaskFromDto(taskDto, task);
        setParentTask(task, taskDto.parentTaskId());
        setLinkedTasks(task, taskDto.linkedTasksIds());

        task = taskRepository.save(task);
        log.info("Successful updated task with id {}\nDate updated: {}. User id who updated: {}",
                task.getId(), task.getUpdatedAt(), userId);
        return taskResponseMapper.entityToDto(task);
    }

    public List<TaskResponse> getAllTasksByFilters(Long projectId, TaskFilterDto filter) {
        validateUserAccess(projectId);

        List<Long> taskIds = taskRepository.findTaskIdsByProjectId(projectId);
        Specification<Task> idSpecification = (root, query, cb) ->
                root.get("id").in(taskIds);

        Specification<Task> specifications = filters.stream()
                .filter(taskFilter -> taskFilter.isApplicable(filter))
                .map(taskFilter -> taskFilter.apply(filter))
                .reduce(Specification::and)
                .orElse(null);

        Specification<Task> finalSpecifications = specifications != null
                ? Specification.where(idSpecification).and(specifications)
                : idSpecification;

        return taskResponseMapper.listEntityToListDto(taskRepository.findAll(finalSpecifications));
    }

    public List<TaskResponse> getAllTasks(Long projectId) {
        validateUserAccess(projectId);
        return taskResponseMapper.listEntityToListDto(taskRepository.findAllByProjectId(projectId));
    }

    public TaskResponse getTaskById(Long taskId) {
        return taskResponseMapper.entityToDto(findTaskById(taskId));
    }

    private void checkPerformerExisting(Long userId) {
        if (userServiceClient.getUser(userId) == null) {
            throw new EntityNotFoundException(MESSAGE_ENTITY_NOT_FOUND, USER_ENTITY_NAME, userId);
        }
    }

    private void validateUserAccess(Long projectId) {
        Long userId = checkUserId();
        Project project = findProjectById(projectId);
        checkUserOnMembership(userId, project);
    }

    private Long checkUserId() {
        return userServiceClient.getUser(userContext.getUserId()).id();
    }

    private Project findProjectById(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow(() ->
                new EntityNotFoundException(MESSAGE_ENTITY_NOT_FOUND, PROJECT_ENTITY_NAME, projectId));
    }

    private void checkUserOnMembership(Long userId, Project project) {
        boolean isProjectMember = project.getTeams().stream()
                .anyMatch(team -> team.getTeamMembers().stream()
                        .anyMatch(member -> member.getUserId().equals(userId)));
        if (!isProjectMember) {
            throw new AccessDeniedException(
                    "You cannot change tasks of the project with id %d, because you aren't a member.", project.getId());
        }
    }

    private void setParentTask(Task task, Long parentTaskId) {
        if (parentTaskId != null) {
            Task parentTask = findTaskById(parentTaskId);
            task.setParentTask(parentTask);
        }
    }

    private void setLinkedTasks(Task task, List<Long> linkedTasksIds) {
        if (linkedTasksIds != null && !linkedTasksIds.isEmpty()) {
            List<Task> linkedTasks = taskRepository.findAllById(linkedTasksIds);
            task.setLinkedTasks(linkedTasks);
        }
    }

    private Task findTaskById(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow(() ->
                new EntityNotFoundException(MESSAGE_ENTITY_NOT_FOUND, TASK_ENTITY_NAME, taskId));
    }
}
