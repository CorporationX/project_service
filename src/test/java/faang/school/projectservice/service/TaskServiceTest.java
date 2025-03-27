package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.task.TaskCreateRequest;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.dto.task.TaskResponse;
import faang.school.projectservice.dto.task.TaskUpdateRequest;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.task.TaskFilter;
import faang.school.projectservice.mapper.task.TaskRequestMapperImpl;
import faang.school.projectservice.mapper.task.TaskResponseMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TaskRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    private final Long firstId = 1L;
    private final Long secondId = 2L;
    private final String firstName = "Task";
    private final String firstDescription = "description";
    private final TaskStatus firstStatus = TaskStatus.TODO;
    private final LocalDateTime firstDate = LocalDateTime.now();
    private final Integer minutes = 20;
    private final Project project = createProject(firstId, List.of(createTeam(List.of(createMember(firstId)))));
    private final List<TaskResponse> responseList = List.of(
            createResponse(secondId, firstId, null, null,
                    null, Collections.emptyList()),
            createResponse(secondId, firstId, firstDate, firstDate.plusMinutes(minutes),
                    firstId, List.of(firstId, secondId))
    );
    private final List<Task> taskList = List.of(
            createTask(secondId, firstId, project)
    );

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Spy
    private TaskRequestMapperImpl requestMapper;

    @Spy
    private TaskResponseMapperImpl responseMapper;

    @Mock
    private TaskFilter keywordFilter;

    @Mock
    private TaskFilter performerFilter;

    @Mock
    private TaskFilter statusFilter;

    @Mock
    private UserContext userContext;

    @Mock
    private UserServiceClient userClient;

    @BeforeEach
    public void setUp() {
        taskService = new TaskService(taskRepository, projectRepository, requestMapper, responseMapper,
                List.of(keywordFilter, performerFilter, statusFilter), userContext, userClient);
    }

    @Test
    public void testNegativeCreateWhenUserNotFound() {
        when(userContext.getUserId()).thenReturn(firstId);
        when(userClient.getUser(firstId)).thenThrow(FeignException.class);

        assertThrows(FeignException.class, () -> taskService.createTask(createRequestOnCreate()));
    }

    @Test
    public void testNegativeCreateWhenProjectNotFound() {
        includeUserCheck();

        assertThrows(EntityNotFoundException.class, () -> taskService.createTask(createRequestOnCreate()));
    }

    @Test
    public void testNegativeCreateWhenUserNotMember() {
        includeUserCheck();
        when(projectRepository.findById(firstId)).thenReturn(Optional.of(createProject(firstId, null)));

        assertThrows(AccessDeniedException.class, () -> taskService.createTask(createRequestOnCreate()));
    }

    @Test
    public void testNegativeCreateWhenPerformerNotFound() {
        includeUserCheck();
        TaskCreateRequest request = createRequestOnCreate();
        when(projectRepository.findById(firstId)).thenReturn(Optional.of(project));
        when(userClient.getUser(request.performerUserId())).thenThrow(FeignException.class);

        assertThrows(FeignException.class, () -> taskService.createTask(request));
    }

    @Test
    public void testPositiveCreateTask() {
        includeUserCheck();
        Task task = taskList.get(0);
        TaskCreateRequest request = createRequestOnCreate();
        when(projectRepository.findById(firstId)).thenReturn(Optional.of(project));
        when(userClient.getUser(request.performerUserId())).thenReturn(createUser(secondId));
        when(taskRepository.save(task)).thenReturn(task);

        TaskResponse response = taskService.createTask(request);

        verify(taskRepository, times(1)).save(task);
        assertEquals(response, responseList.get(0));
    }

    @Test
    public void testNegativeUpdateWhenTaskNotFound() {
        includeUserCheck();
        TaskUpdateRequest request = createRequestOnUpdate();

        assertThrows(EntityNotFoundException.class, () -> taskService.updateTask(request));
    }

    @Test
    public void testPositiveUpdateTask() {
        includeUserCheck();
        Task task = taskList.get(0);
        TaskUpdateRequest request = createRequestOnUpdate();
        when(taskRepository.findById(firstId)).thenReturn(Optional.of(task));
        when(userClient.getUser(request.performerUserId())).thenReturn(createUser(secondId));
        when(taskRepository.save(task)).thenReturn(task);

        TaskResponse response = taskService.updateTask(request);

        verify(taskRepository, times(1)).save(task);
        assertEquals(response, responseList.get(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPositiveGetAllTasksByFilters() {
        includeUserCheck();
        TaskFilterDto filter = createFilterDto();
        when(projectRepository.findById(firstId)).thenReturn(Optional.of(project));
        when(taskRepository.findTaskIdsByProjectId(firstId)).thenReturn(List.of(firstId, secondId));
        when(statusFilter.isApplicable(filter)).thenReturn(true);
        when(statusFilter.apply(filter)).thenReturn((root, query, builder) ->
                builder.equal(root.get("status"), TaskStatus.IN_PROGRESS));
        when(keywordFilter.isApplicable(filter)).thenReturn(true);
        when(keywordFilter.apply(filter)).thenReturn((root, query, builder) ->
                builder.like(root.get("title"), "%keyword%"));
        when(performerFilter.isApplicable(filter)).thenReturn(true);
        when(performerFilter.apply(filter)).thenReturn((root, query, builder) ->
                builder.equal(root.get("performerId"), 123L));
        List<Task> filteredTasks = List.of(taskList.get(0));
        when(taskRepository.findAll(any(Specification.class))).thenReturn(filteredTasks);

        List<TaskResponse> result = taskService.getAllTasksByFilters(firstId, filter);

        assertEquals(1, result.size());
        verify(taskRepository).findAll(any(Specification.class));
    }

    @Test
    public void testPositiveGetAllTasks() {
        includeUserCheck();
        when(projectRepository.findById(firstId)).thenReturn(Optional.of(project));
        when(taskRepository.findAllByProjectId(firstId)).thenReturn(taskList);

        List<TaskResponse> responseList = taskService.getAllTasks(firstId);

        assertEquals(responseList.get(0).name(), taskList.get(0).getName());
        assertEquals(responseList.get(0).description(), taskList.get(0).getDescription());
        assertEquals(responseList.get(0).status(), taskList.get(0).getStatus());
        assertEquals(responseList.get(0).performerUserId(), taskList.get(0).getPerformerUserId());
        assertEquals(responseList.get(0).reporterUserId(), taskList.get(0).getReporterUserId());
        assertEquals(firstId, taskList.get(0).getProject().getId());
    }

    @Test
    public void testPositiveGetTaskById() {
        includeUserCheck();
        when(projectRepository.findById(firstId)).thenReturn(Optional.of(project));
        when(taskRepository.findById(firstId)).thenReturn(Optional.of(taskList.get(0)));

        TaskResponse response = taskService.getTaskById(firstId);

        assertEquals(response, responseList.get(0));
    }

    private TaskCreateRequest createRequestOnCreate() {
        return TaskCreateRequest.builder()
                .name(firstName)
                .description(firstDescription)
                .performerUserId(secondId)
                .reporterUserId(firstId)
                .projectId(firstId)
                .build();
    }

    private TaskUpdateRequest createRequestOnUpdate() {
        return TaskUpdateRequest.builder()
                .id(firstId)
                .status(firstStatus)
                .performerUserId(secondId)
                .minutesTracked(minutes)
                .linkedTasksIds(List.of(firstId))
                .build();
    }

    private TaskResponse createResponse(Long performerId, Long reporterId, LocalDateTime createdAt,
                                        LocalDateTime deadline, Long parentTaskId, List<Long> linkedTasksIds) {
        return TaskResponse.builder()
                .name(firstName)
                .description(firstDescription)
                .status(firstStatus)
                .performerUserId(performerId)
                .reporterUserId(reporterId)
                .createdAt(createdAt)
                .deadline(deadline)
                .parentTaskId(parentTaskId)
                .linkedTasksIds(linkedTasksIds)
                .build();
    }

    private TaskFilterDto createFilterDto() {
        return TaskFilterDto.builder()
                .status(firstStatus)
                .performerId(firstId)
                .keyword("a")
                .build();
    }

    private Task createTask(Long performerId, Long reporterId, Project project) {
        return Task.builder()
                .name(firstName)
                .description(firstDescription)
                .status(firstStatus)
                .performerUserId(performerId)
                .reporterUserId(reporterId)
                .project(project)
                .build();
    }

    private Project createProject(Long id, List<Team> teams) {
        return Project.builder()
                .id(id)
                .teams(teams)
                .build();
    }

    private Team createTeam(List<TeamMember> members) {
        return Team.builder()
                .teamMembers(members)
                .build();
    }

    private TeamMember createMember(Long id) {
        return TeamMember.builder()
                .userId(id)
                .build();
    }

    private UserDto createUser(Long id) {
        return UserDto.builder()
                .id(id)
                .build();
    }

    private void includeUserCheck() {
        when(userContext.getUserId()).thenReturn(firstId);
        when(userClient.getUser(firstId)).thenReturn(createUser(firstId));
    }
}
