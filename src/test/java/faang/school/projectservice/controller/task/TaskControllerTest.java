package faang.school.projectservice.controller.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.config.context.UserHeaderFilter;
import faang.school.projectservice.dto.client.task.TaskCreateDto;
import faang.school.projectservice.dto.client.task.TaskUpdateDto;
import faang.school.projectservice.dto.client.task.TaskViewDto;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.service.task.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserHeaderFilter userHeaderFilter;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Проверка успешного создания задачи через контроллер")
    void shouldCreateSuccessfully() throws Exception {
        TaskCreateDto createDto = new TaskCreateDto(
                "someName",
                "someDescription",
                TaskStatus.TODO,
                1L,
                new ArrayList<Long>(List.of(1L)),
                1L,
                1L
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Проверка успешного обновления задачи через контроллер")
    void shouldUpdateSuccessfully() throws Exception {
        TaskUpdateDto updateDto = new TaskUpdateDto(
                "someName",
                "someDescription",
                TaskStatus.TODO,
                1L,
                1L,
                10,
                new ArrayList<Long>(List.of(1L)),
                1L,
                1L
        );

        mockMvc.perform(MockMvcRequestBuilders.put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Проверка успешного получения отфильтрованных задач через контроллер")
    void getProjectsByFilterTest() throws Exception {


        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/filter/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Проверка успешного получения задачи по id")
    void getProjectByIdTest() throws Exception {
        TaskViewDto viewDto = new TaskViewDto(
                "someName",
                "someDescription",
                TaskStatus.DONE,
                1L,
                1L,
                1,
                1L,
                List.of(1L),
                1L,
                1L
        );

        when(taskService.getById(1L)).thenReturn(viewDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("someName"))
                .andExpect(jsonPath("$.description").value("someDescription"));
    }
}
