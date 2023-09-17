package faang.school.projectservice.service;

import faang.school.projectservice.dto.task.CreateTaskDto;
import faang.school.projectservice.dto.task.ResponseTaskDto;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.task.TaskMapper;
import faang.school.projectservice.model.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private JiraApiService jiraApiService;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    @Test
    public void testCreateTask() {
        CreateTaskDto createTaskDto = new CreateTaskDto();
        Task taskEntity = new Task();
        when(taskMapper.createDtoToEntity(createTaskDto)).thenReturn(taskEntity);
        when(taskRepository.save(taskEntity)).thenReturn(taskEntity);
        when(taskMapper.entityToResponseDto(taskEntity)).thenReturn(new ResponseTaskDto());


        ResponseTaskDto response = taskService.createTask(createTaskDto);

        assertNotNull(response);

        verify(taskMapper).createDtoToEntity(createTaskDto);
        verify(taskRepository).save(taskEntity);
        verify(taskMapper).entityToResponseDto(taskEntity);
        verify(jiraApiService).createTask(createTaskDto);
    }
}