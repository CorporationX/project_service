package faang.school.projectservice.service.task;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.task.TaskCreateDto;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.service.filter.task.TaskFilterServiceImpl;
import faang.school.projectservice.util.task.TaskUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static faang.school.projectservice.util.project.ProjectUtil.isInTeam;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для проверки логики создания, обновления и получения задач проектов")
public class TaskServiceImplTest {
    @InjectMocks
    private TaskServiceImpl service;
    @Mock
    private TaskFilterServiceImpl filterService;
    @Mock
    private TaskRepository repository;
    @Spy
    private TaskMapper mapper;
    @Mock
    private UserContext userContext;
    @Mock
    private TaskUtil taskUtil;
    @Mock
    private Task someTask;

    @Test
    @DisplayName("Тест для проверки успешного сохранения задачи в БД")
    void createTaskTest() {
        TaskCreateDto createDto = new TaskCreateDto(
                "someName",
                "someDescription",
                TaskStatus.TODO,
                5L,
                new ArrayList<Long>(List.of(1L, 2L, 3L)),
                1L,
                1L
        );
        Task task = mapper.toEntity(createDto);
        when(isInTeam(1L, 1L, Project.builder()
                .id(1L)
                .build()))
                .thenReturn(true);

        assertEquals(mapper.toViewDto(task), service.createTask(createDto));
    }
}
