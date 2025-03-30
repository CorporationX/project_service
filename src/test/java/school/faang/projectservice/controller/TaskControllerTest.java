package school.faang.projectservice.controller;

import faang.school.projectservice.controller.task.TaskController;
import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskResponseDto;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private TaskDto taskDto;
    private TaskResponseDto responseDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
        objectMapper = new ObjectMapper();

        taskDto = TaskDto.builder()
                .name("Test Task")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .build();

        responseDto = TaskResponseDto.builder()
                .id(1L)
                .name("Test Task")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .build();
    }

    @Test
    void testCreateTaskWithValidInput_ReturnsCreatedTask() throws Exception {
        when(taskService.createTask(any(TaskDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Task"));
    }

    @Test
    void testUpdateTaskWithValidInput_ReturnsUpdatedTask() throws Exception {
        when(taskService.updateTask(anyLong(), any(TaskDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGetTaskByIdWithExistingId_ReturnsTask() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGetAllTasks_ReturnsTaskList() throws Exception {
        when(taskService.getAllTasks()).thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testGetTasksFilteredWithFilters_ReturnsFilteredTasks() throws Exception {
        when(taskService.getFilteredTasks(any())).thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/api/v1/tasks/search")
                        .param("status", "TODO")
                        .param("performerId", "1")
                        .param("keyword", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testDeleteTaskWithExistingId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/tasks/1"))
                .andExpect(status().isNoContent());
    }
}
