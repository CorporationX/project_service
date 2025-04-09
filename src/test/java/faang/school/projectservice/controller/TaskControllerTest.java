package faang.school.projectservice.controller;

import faang.school.projectservice.controller.task.TaskController;
import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.dto.task.TaskResponseDto;
import faang.school.projectservice.exception.GlobalExceptionHandler;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.service.task.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private TaskDto taskDto;
    private TaskResponseDto taskResponseDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController)
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        taskDto = TaskDto.builder()
                .name("Test Task")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .performerUserId(1L)
                .projectId(1L)
                .deadline(LocalDateTime.now().plusDays(1))
                .build();

        taskResponseDto = TaskResponseDto.builder()
                .id(1L)
                .name("Test Task")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .performerUserId(1L)
                .projectId(1L)
                .deadline(LocalDateTime.now().plusDays(1))
                .build();
    }

    @Test
    void createTaskValidInput() throws Exception {
        when(taskService.createTask(any(TaskDto.class))).thenReturn(taskResponseDto);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Task")))
                .andExpect(jsonPath("$.status", is("TODO")));
    }

    @Test
    void createTaskInvalidInput() throws Exception {
        TaskDto invalidTaskDto = TaskDto.builder().build();

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTaskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Failed")));
    }

    @Test
    void updateTaskValidInput() throws Exception {
        when(taskService.updateTask(any(Long.class), any(TaskDto.class))).thenReturn(taskResponseDto);

        mockMvc.perform(put("/api/v1/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Task")));
    }

    @Test
    void getFilteredTasksValidParams() throws Exception {
        TaskFilterDto filterDto = TaskFilterDto.builder()
                .status(TaskStatus.TODO)
                .performerId(1L)
                .keyword("test")
                .build();

        when(taskService.getFilteredTasks(any(TaskFilterDto.class))).thenReturn(List.of(taskResponseDto));

        mockMvc.perform(get("/api/v1/tasks/search")
                        .param("status", "TODO")
                        .param("performerId", "1")
                        .param("keyword", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].status", is("TODO")));
    }

    @Test
    void getFilteredTasksNoParams() throws Exception {
        when(taskService.getFilteredTasks(any(TaskFilterDto.class))).thenReturn(List.of(taskResponseDto));

        mockMvc.perform(get("/api/v1/tasks/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getAllTasks() throws Exception {
        when(taskService.getAllTasks()).thenReturn(List.of(taskResponseDto));

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void getAllTasksByProjectId() throws Exception {
        when(taskService.getAllTasksByProjectId(1L)).thenReturn(List.of(taskResponseDto));

        mockMvc.perform(get("/api/v1/tasks/project/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].projectId", is(1)));
    }

    @Test
    void getTaskByIdExistingId() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(taskResponseDto);

        mockMvc.perform(get("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Task")));
    }

    @Test
    void deleteTaskExistingId() throws Exception {
        mockMvc.perform(delete("/api/v1/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void createTaskInvalidDeadline() throws Exception {
        TaskDto invalidTaskDto = taskDto.builder()
                .deadline(LocalDateTime.now().minusDays(1))
                .build();

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTaskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Failed")));
    }

    @Test
    void createTaskMissingRequiredFields() throws Exception {
        TaskDto invalidTaskDto = TaskDto.builder()
                .status(TaskStatus.TODO)
                .build();

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTaskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.message", containsString("name")))
                .andExpect(jsonPath("$.message", containsString("performerUserId")));
    }
}