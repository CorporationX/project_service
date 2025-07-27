package faang.school.projectservice.service.task;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.task.TaskCreateDto;
import faang.school.projectservice.dto.client.task.TaskViewDto;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.service.filter.task.TaskFilterServiceImpl;
import faang.school.projectservice.util.project.ProjectUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для проверки логики создания, обновления и получения задач проектов")
@SpringBootTest
public class TaskServiceImplTest {
    @Mock
    private TaskFilterServiceImpl filterService;
    //private TaskMapper mapper = Mappers.getMapper(TaskMapper.class);
    @Autowired
    private TaskMapper mapper;
    @Mock
    private UserContext userContext;
    @Mock
    private Task someTask;
    @Mock
    private ProjectUtil projectUtil;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private StageRepository stageRepository;
    @InjectMocks
    private TaskServiceImpl service;

    @Test
    @DisplayName("Тест для проверки успешного сохранения задачи в БД")
    void createTaskTest() {
        Long projectId = 1L;
        Long parentTaskId = 5L;
        Long linkedTaskId = 10L;
        Long stageId = 1L;

        TaskCreateDto createDto = new TaskCreateDto(
                "someName",
                "someDescription",
                TaskStatus.TODO,
                parentTaskId,
                new ArrayList<Long>(List.of(linkedTaskId)),
                projectId,
                stageId
        );

        Task task = mapper.toEntity(createDto);

        Task parentTask = Task.builder()
                .id(parentTaskId)
                .build();

        Task linkedTask = Task.builder()
                .id(linkedTaskId)
                .build();

        Project project = Project.builder()
                .id(projectId)
                .build();

        Stage stage = Stage.builder()
                .stageId(stageId)
                .build();

        task.setParentTask(parentTask);
        task.setLinkedTasks(List.of(linkedTask));
        task.setProject(project);
        task.setStage(stage);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.findById(parentTaskId)).thenReturn(Optional.of(parentTask));
        when(taskRepository.findById(linkedTaskId)).thenReturn(Optional.of(linkedTask));
        when(stageRepository.findById(stageId)).thenReturn(Optional.of(stage));

        TaskViewDto expectedTaskViewDto = mapper.toViewDto(task);
        TaskViewDto resultTaskViewDto = service.createTask(createDto);

        assertEquals(expectedTaskViewDto, resultTaskViewDto);
    }
}
