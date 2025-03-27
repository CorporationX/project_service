package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.task.TaskCreateRequest;
import faang.school.projectservice.dto.task.TaskResponse;
import faang.school.projectservice.dto.task.TaskUpdateRequest;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.filter.task.TaskFilter;
import faang.school.projectservice.mapper.task.TaskRequestMapperImpl;
import faang.school.projectservice.mapper.task.TaskResponseMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

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

    private TaskCreateRequest createRequestOnCreate() {
        return TaskCreateRequest.builder()
                .build();
    }

    private TaskUpdateRequest createRequestOnUpdate() {
        return TaskUpdateRequest.builder()
                .build();
    }

    private TaskResponse createResponse() {
        return TaskResponse.builder()
                .build();
    }

    private Task createTask() {
        return Task.builder()
                .build();
    }

    private Project createProject() {
        return Project.builder()
                .build();
    }

    private UserDto createUser() {
        return UserDto.builder()
                .build();
    }
}
