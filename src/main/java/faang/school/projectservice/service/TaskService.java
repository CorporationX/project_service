package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.task.CreateTaskDto;
import faang.school.projectservice.dto.task.TaskGettingDto;
import faang.school.projectservice.dto.task.TaskResult;
import faang.school.projectservice.dto.task.UpdateTaskDto;
import faang.school.projectservice.exception.*;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.service.filter.task.TaskGetting;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Log4j2
@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final StageRepository stageRepository;
    private final TaskMapper taskMapper;
    private final UserServiceClient userServiceClient;
    private final List<TaskGetting> filters;

    @Transactional
    public TaskResult createTask(CreateTaskDto createTaskDto) {
        Long taskCreatorId = createTaskDto.reporterUserId();
        Project project = findByProjectId(createTaskDto.projectId());
        areUsersInSystem(createTaskDto.performerUserId(), createTaskDto.reporterUserId());
        isItOnePerson(createTaskDto.performerUserId(), createTaskDto.reporterUserId());
        isUserInProject(project, taskCreatorId);

        Stage stage = findStageById(createTaskDto.stageId());
        Task parentTask = findTaskById(createTaskDto.parentTaskId());

        Task task = taskMapper.toTask(createTaskDto);
        task.setProject(project);
        task.setParentTask(parentTask);
        task.setStage(stage);

        if (!createTaskDto.linkedTaskIds().isEmpty()) {
            List<Task> linkedTasks = taskRepository.findAllById(createTaskDto.linkedTaskIds());
            task.setLinkedTasks(linkedTasks);
        }

        project.getTasks().add(task);
        task = taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    @Transactional
    public TaskResult updateTask(UpdateTaskDto updateTaskDto,
                                 Long taskId,
                                 Long userId) {
        Task task = findTaskById(taskId);
        isUserInProject(task.getProject(), userId);
        areUsersInSystem(userId);

        taskMapper.updateTaskFromDto(updateTaskDto, task);

        if (!updateTaskDto.linkedTaskIds().isEmpty()) {
            List<Task> linkedTasks = findLinkedTasksByIds(updateTaskDto.linkedTaskIds());
            task.setLinkedTasks(linkedTasks);
        }

        log.info("Task with id : {}, was updated by user with id: {}", taskId, userId);
        return taskMapper.toDto(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResult> getTasksFilter(@Valid TaskGettingDto taskGettingDto,
                                           Long userId,
                                           Long projectId) {
        Project project = findByProjectId(projectId);
        areUsersInSystem(userId);
        isUserInProject(project, userId);

        Stream<Task> taskStream = project.getTasks().stream();

        for (TaskGetting filter : filters) {
            if (filter.isApplicable(taskGettingDto)) {
                taskStream = filter.filter(taskStream, taskGettingDto);
            }
        }

        return taskStream
                .map(taskMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResult> getProjectTasks(Long userId, Long projectId) {
        Project project = findByProjectId(projectId);
        areUsersInSystem(userId);
        isUserInProject(project, userId);
        return project.getTasks().stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskResult findTaskByIdToDto(Long taskId) {
        return taskMapper.toDto(findTaskById(taskId));
    }

    @Transactional(readOnly = true)
    private Task findTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("task with was not found -> id : " + taskId));
    }

    private void isItOnePerson(Long performerUserId, Long reporterUserId) {
        if (performerUserId.equals(reporterUserId)) {
            log.error("PerformerUserId and reporterUserId is equals -> {}, {}",
                    performerUserId,
                    reporterUserId);
            throw new OnePersonException("PerformerUserId and reporterUserId is equals");
        }
    }

    private void areUsersInSystem(Long... userIds) {
        boolean isError = userServiceClient.getUsersByIds(Arrays.asList(userIds)).stream()
                .anyMatch(Objects::isNull);

        if (isError) {
            log.error("One or more users were not found -> userIds: {}", List.of(userIds));
            throw new UserWasNotFoundException("One or more users were not found in the database");
        }
    }

    private void isUserInProject(Project project, Long userId) {
        boolean isUserInProject = project.getTeams().stream()
                .anyMatch(team -> team.getTeamMembers()
                        .stream().anyMatch(teamMember -> teamMember.getUserId().equals(userId)));
        if (!isUserInProject) {
            throw new UserIsNotInThatProjectException("User is not in that project -> user id : " + userId);
        }
    }

    @Transactional(readOnly = true)
    private Stage findStageById(Long stageId) {
        return stageRepository.findById(stageId)
                .orElseThrow(() -> new StageWasNotFoundException("Stage was not found -> id : " + stageId));
    }

    @Transactional(readOnly = true)
    private List<Task> findLinkedTasksByIds(List<Long> linkedTaskIds) {
        return taskRepository.findAllById(linkedTaskIds);
    }

    @Transactional(readOnly = true)
    private Project findByProjectId(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectWasNotFoundException("Project was not found with id : " + id));
    }
}
