package faang.school.projectservice.task;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.filter.task.TaskFilterStrategy;
import faang.school.projectservice.filter.task.TaskKeyWordFilter;
import faang.school.projectservice.filter.task.TaskPerformerFilter;
import faang.school.projectservice.filter.task.TaskStatusFilter;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.mapper.TaskMapperImpl;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.service.task.TaskService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
public class TaskServiceTest {
    @Mock
    private  TaskRepository taskRepository;
    private TaskKeyWordFilter taskKeyWordFilter;
    private TaskStatusFilter taskStatusFilter;
    private TaskPerformerFilter taskPerformerFilter;
    private final TaskMapper taskMapper = new TaskMapperImpl();
    @Mock
    private  UserContext userContext;
    @Spy
    private  List<TaskFilterStrategy> taskFilterStrategies;
    @Mock
    private  ProjectRepository projectRepository;
    @InjectMocks
    private  TaskService taskService;

    @BeforeEach
    void setUp() {
        taskFilterStrategies = List.of(
                new TaskKeyWordFilter(),
                new TaskPerformerFilter(),
                new TaskStatusFilter()
        );
    }

    @Test
    public void test
}
