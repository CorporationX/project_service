package faang.school.projectservice.service.task;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.task.TaskCreateDto;
import faang.school.projectservice.dto.client.task.TaskFilterDto;
import faang.school.projectservice.dto.client.task.TaskUpdateDto;
import faang.school.projectservice.dto.client.task.TaskViewDto;
import faang.school.projectservice.mapper.TaskMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.service.filter.task.TaskFilterServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static faang.school.projectservice.service.task.TaskServiceImplTestData.getCreateDto;
import static faang.school.projectservice.service.task.TaskServiceImplTestData.getUpdateDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для проверки логики создания, обновления и получения задач проектов")
public class TaskServiceImplTest {
    @Mock
    private TaskFilterServiceImpl filterService;
    @Spy
    private TaskMapperImpl mapper;
    @Mock
    private UserContext userContext;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private StageRepository stageRepository;
    @InjectMocks
    private TaskServiceImpl service;

    private Long projectId = 1L;
    private Project project;

    @BeforeEach
    void init() {
        TeamMember teamMember = TeamMember.builder()
                .id(100L)
                .build();

        Team team = Team.builder()
                .teamMembers(List.of(teamMember))
                .build();

        project = Project.builder()
                .id(projectId)
                .teams(List.of(team))
                .build();
    }

    @Test
    @DisplayName("Тест для проверки успешного сохранения задачи в БД")
    void createTaskTest() {
        Long parentTaskId = 5L;
        Long linkedTaskId = 10L;
        Long stageId = 1L;
        Long projectId = 1L;

        TaskCreateDto createDto = getCreateDto(parentTaskId, linkedTaskId, stageId, projectId);

        Task task = mapper.toEntity(createDto);

        Task parentTask = Task.builder()
                .id(parentTaskId)
                .build();

        Task linkedTask = Task.builder()
                .id(linkedTaskId)
                .build();

        Stage stage = Stage.builder()
                .stageId(stageId)
                .build();

        task.setParentTask(parentTask);
        task.setLinkedTasks(List.of(linkedTask));
        task.setProject(project);
        task.setStage(stage);

        when(projectRepository.getByIdOrThrow(projectId)).thenReturn(project);
        when(taskRepository.getByIdOrThrow(parentTaskId)).thenReturn(parentTask);
        when(taskRepository.getByIdOrThrow(linkedTaskId)).thenReturn(linkedTask);
        when(stageRepository.getByIdOrThrow(stageId)).thenReturn(stage);
        when(userContext.getUserId()).thenReturn(100L);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskViewDto expectedTaskViewDto = mapper.toViewDto(task);
        TaskViewDto resultTaskViewDto = service.createTask(createDto);

        assertEquals(expectedTaskViewDto, resultTaskViewDto);
    }

    @Test
    @DisplayName("Проверка успешного сценария обновления задачи")
    void updateTaskTest() {
        Long linkedTaskId = 10L;
        Long stageId = 1L;
        Long taskId = 1L;

        TaskUpdateDto updateDto = getUpdateDto(linkedTaskId, stageId, projectId);

        Task task = new Task();

        mapper.update(updateDto, task);

        Task linkedTask = Task.builder()
                .id(linkedTaskId)
                .build();

        Stage stage = Stage.builder()
                .stageId(stageId)
                .build();

        task.setLinkedTasks(List.of(linkedTask));
        task.setProject(project);
        task.setStage(stage);

        when(projectRepository.getByIdOrThrow(projectId)).thenReturn(project);
        when(taskRepository.getByIdOrThrow(linkedTaskId)).thenReturn(linkedTask);
        when(taskRepository.getByIdOrThrow(taskId)).thenReturn(task);
        when(stageRepository.getByIdOrThrow(stageId)).thenReturn(stage);
        when(userContext.getUserId()).thenReturn(100L);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskViewDto expectedTaskViewDto = mapper.toViewDto(task);
        TaskViewDto resultTaskViewDto = service.updateTask(taskId, updateDto);

        assertEquals(expectedTaskViewDto, resultTaskViewDto);
    }

    @Test
    @DisplayName("Проверка успешного сценария фильтрации по статусу")
    void getTaskFilteredByStatusTest() {
        TaskFilterDto filterDto =
                new TaskFilterDto(1L, TaskStatus.IN_PROGRESS, null, null);

        Task task = new Task();
        task.setStatus(TaskStatus.IN_PROGRESS);
        TaskViewDto taskViewDto = mapper.toViewDto(task);

        Task taskWithAnotherStatus = new Task();
        taskWithAnotherStatus.setStatus(TaskStatus.DONE);


        when(projectRepository.getByIdOrThrow(projectId)).thenReturn(project);
        when(taskRepository.findAllByProjectId(projectId))
                .thenReturn(List.of(task, taskWithAnotherStatus));
        when(filterService.getFilteredList(List.of(task, taskWithAnotherStatus), filterDto))
                .thenReturn(List.of(task));
        when(userContext.getUserId()).thenReturn(100L);

        assertEquals(List.of(taskViewDto), service.getByFilter(filterDto));
    }

    @Test
    @DisplayName("Проверка успешного сценария фильтрации по исполнителю")
    void getTaskFilteredByPerformerTest() {
        TaskFilterDto filterDto =
                new TaskFilterDto(1L, null, 1L, null);

        Task task = new Task();
        task.setPerformerUserId(1L);
        TaskViewDto taskViewDto = mapper.toViewDto(task);

        Task taskWithAnotherPerformer = new Task();
        taskWithAnotherPerformer.setPerformerUserId(7L);

        when(projectRepository.getByIdOrThrow(projectId)).thenReturn(project);
        when(taskRepository.findAllByProjectId(projectId))
                .thenReturn(List.of(task, taskWithAnotherPerformer));
        when(filterService.getFilteredList(List.of(task, taskWithAnotherPerformer), filterDto))
                .thenReturn(List.of(task));
        when(userContext.getUserId()).thenReturn(100L);

        assertEquals(List.of(taskViewDto), service.getByFilter(filterDto));
    }

    @Test
    @DisplayName("Проверка успешного сценария фильтрации по ключевому слову")
    void getTaskFilteredByKeywordTest() {
        TaskFilterDto filterDto =
                new TaskFilterDto(1L, null, null, "some");

        Task task = new Task();
        task.setName("someName");
        TaskViewDto taskViewDto = mapper.toViewDto(task);

        Task taskWithAnotherName = new Task();
        taskWithAnotherName.setName("nameWithoutKeyword");

        when(projectRepository.getByIdOrThrow(projectId)).thenReturn(project);
        when(taskRepository.findAllByProjectId(projectId)).thenReturn(List.of(task, taskWithAnotherName));
        when(filterService.getFilteredList(List.of(task, taskWithAnotherName), filterDto))
                .thenReturn(List.of(task));
        when(userContext.getUserId()).thenReturn(100L);

        assertEquals(List.of(taskViewDto), service.getByFilter(filterDto));
    }

    @Test
    @DisplayName("Проверка успешного сценария получения задачи по её id")
    void getByIdTest() {
        Long taskId = 500L;
        Task task = Task.builder()
                .id(taskId)
                .name("someName")
                .project(project)
                .build();

        TaskViewDto expectedTaskViewDto = mapper.toViewDto(task);

        when(projectRepository.getByIdOrThrow(projectId)).thenReturn(project);
        when(userContext.getUserId()).thenReturn(100L);
        when(taskRepository.getByIdOrThrow(taskId)).thenReturn(task);

        assertEquals(expectedTaskViewDto, service.getById(taskId));
    }
}
