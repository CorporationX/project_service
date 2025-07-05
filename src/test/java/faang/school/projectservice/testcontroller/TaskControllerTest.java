package faang.school.projectservice.testcontroller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.controller.task.TaskController;
import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskRequestDto;
import faang.school.projectservice.dto.task.TaskResponseDto;
import faang.school.projectservice.mapper.task.TaskMapperImpl;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @Spy
    private TaskMapperImpl taskMapper;

    @InjectMocks
    private TaskController taskController;

    @Mock
    private UserContext userContext;

    @Mock
    private ProjectService projectService;

    @Test
    void updateTaskShouldReturnUpdatedTaskResponseDto() {
        Long taskId = 1L;
        Long projectId = 2L;
        Long reporterUserId = 4L;
        TaskRequestDto taskRequestDto = new TaskRequestDto(
                "Updated Task",
                "Updated Description",
                TaskStatus.IN_PROGRESS,
                5L,
                reporterUserId,
                10,
                null,
                null,
                projectId,
                2L
        );
        TaskDto updatedTaskDto = TaskDto.builder()
                .id(taskId)
                .name("Updated Task")
                .description("Updated Description")
                .status(TaskStatus.IN_PROGRESS)
                .performerUserId(5L)
                .reporterUserId(reporterUserId)
                .minutesTracked(10)
                .projectId(projectId)
                .stageId(2L)
                .parentTaskId(null)
                .linkedTaskIds(null)
                .build();

        final TaskResponseDto expectedTaskResponseDto = new TaskResponseDto(
                taskId,
                "Updated Task",
                "Updated Description",
                TaskStatus.IN_PROGRESS,
                5L,
                10,
                null,
                null,
                2L
        );

        when(taskService.updateTask(taskId, taskRequestDto)).thenReturn(updatedTaskDto);
        when(userContext.getUserId()).thenReturn(reporterUserId);

        ResponseEntity<TaskResponseDto> response = taskController.updateTask(taskId, taskRequestDto);

        assertEquals(OK, response.getStatusCode());
        assertEquals(expectedTaskResponseDto, response.getBody());
        verify(taskService).updateTask(taskId, taskRequestDto);
    }

    @Test
    void getAllTasksByProjectIdWithFiltersShouldReturnListOfTaskResponseDto() {

        Long projectId = 1L;
        TaskStatus status = TaskStatus.OPEN;
        Long performerUserId = 3L;
        String keyword = "test";
        Long reporterUserId = 4L;

        TaskDto taskDto1 = TaskDto.builder()
                .id(1L)
                .name("Test Task 1")
                .description("Test Description 1")
                .status(TaskStatus.OPEN)
                .performerUserId(3L)
                .reporterUserId(reporterUserId)
                .minutesTracked(10)
                .projectId(projectId)
                .stageId(1L)
                .parentTaskId(null)
                .linkedTaskIds(null)
                .build();

        TaskResponseDto taskResponseDto1 = new TaskResponseDto(
                1L,
                "Test Task 1",
                "Test Description 1",
                TaskStatus.OPEN,
                3L,
                10,
                null,
                null,
                1L
        );
        List<TaskDto> taskDtos = List.of(taskDto1);
        List<TaskResponseDto> expectedTaskResponseDtos = List.of(taskResponseDto1);

        when(taskService.getAllTasksByProjectIdWithFilters(projectId, status, performerUserId, keyword))
                .thenReturn(taskDtos);

        ResponseEntity<List<TaskResponseDto>> response = taskController.getAllTasksByProjectIdWithFilters(
                projectId, status, performerUserId, keyword);

        assertEquals(OK, response.getStatusCode());
        assertEquals(expectedTaskResponseDtos, response.getBody());
        verify(taskService).getAllTasksByProjectIdWithFilters(projectId, status, performerUserId, keyword);
    }

    @Test
    void getAllTasksByProjectIdShouldReturnListOfTaskResponseDto() {

        Long projectId = 1L;
        Long reporterUserId = 4L;
        TaskDto taskDto1 = TaskDto.builder()
                .id(1L)
                .name("Test Task 1")
                .description("Test Description 1")
                .status(TaskStatus.OPEN)
                .performerUserId(3L)
                .reporterUserId(reporterUserId)
                .minutesTracked(10)
                .projectId(projectId)
                .stageId(1L)
                .parentTaskId(null)
                .linkedTaskIds(null)
                .build();

        TaskResponseDto taskResponseDto1 = new TaskResponseDto(
                1L,
                "Test Task 1",
                "Test Description 1",
                TaskStatus.OPEN,
                3L,
                10,
                null,
                null,
                1L
        );
        List<TaskDto> taskDtos = List.of(taskDto1);
        List<TaskResponseDto> expectedTaskResponseDtos = List.of(taskResponseDto1);

        when(taskService.getAllTasksByProjectId(projectId)).thenReturn(taskDtos);

        ResponseEntity<List<TaskResponseDto>> response = taskController.getAllTasksByProjectId(projectId);

        assertEquals(OK, response.getStatusCode());
        assertEquals(expectedTaskResponseDtos, response.getBody());
        verify(taskService).getAllTasksByProjectId(projectId);
    }

    @Test
    void getTaskByIdShouldReturnTaskResponseDto() {

        Long taskId = 1L;
        Long reporterUserId = 4L;
        TaskDto taskDto = TaskDto.builder()
                .id(1L)
                .name("Test Task")
                .description("Test Description")
                .status(TaskStatus.OPEN)
                .performerUserId(3L)
                .reporterUserId(reporterUserId)
                .minutesTracked(10)
                .projectId(1L)
                .stageId(1L)
                .parentTaskId(null)
                .linkedTaskIds(null)
                .build();

        TaskResponseDto expectedTaskResponseDto = new TaskResponseDto(
                1L,
                "Test Task",
                "Test Description",
                TaskStatus.OPEN,
                3L,
                10,
                null,
                null,
                1L
        );

        when(taskService.getTaskById(taskId)).thenReturn(taskDto);

        ResponseEntity<TaskResponseDto> response = taskController.getTaskById(taskId);

        assertEquals(OK, response.getStatusCode());
        assertEquals(expectedTaskResponseDto, response.getBody());
        verify(taskService).getTaskById(taskId);
    }
}