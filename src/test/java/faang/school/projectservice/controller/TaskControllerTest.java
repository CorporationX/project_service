package faang.school.projectservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.task.TaskCreateRequest;
import faang.school.projectservice.dto.task.TaskResponse;
import faang.school.projectservice.dto.task.TaskUpdateRequest;
import faang.school.projectservice.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {TaskControllerTest.class})
public class TaskControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @MockBean
    private TaskService taskService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testPositiveCreateTask() throws Exception {

    }

    @Test
    public void testPositiveUpdateTask() throws Exception {

    }

    @Test
    public void testPositiveGetAllTasksByFilters() throws Exception {

    }

    @Test
    public void testPositiveGetAllTasks() throws Exception {

    }

    @Test
    public void testPositiveGetTaskById() throws Exception {

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
}
