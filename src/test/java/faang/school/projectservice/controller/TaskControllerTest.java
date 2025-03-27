package faang.school.projectservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.projectservice.dto.task.TaskCreateRequest;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.dto.task.TaskResponse;
import faang.school.projectservice.dto.task.TaskUpdateRequest;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
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
@ContextConfiguration(classes = {TaskController.class})
public class TaskControllerTest {

    private final Long firstId = 1L;
    private final Long secondId = 2L;
    private final String firstName = "Task";
    private final String firstDescription = "description";
    private final TaskStatus firstStatus = TaskStatus.TODO;
    private final LocalDateTime firstDate = LocalDateTime.now();
    private final Integer minutes = 20;
    private final ObjectMapper mapper = new ObjectMapper();
    private final List<TaskResponse> responseList = List.of(
            createResponse(firstId, secondId, firstDate, firstDate.plusMinutes(minutes),
                    null, null),
            createResponse(secondId, firstId, firstDate, firstDate.plusMinutes(minutes),
                    firstId, List.of(firstId, secondId))
    );

    @MockBean
    private TaskService taskService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testPositiveCreateTask() throws Exception {
        TaskCreateRequest request = createRequestOnCreate();
        String jsonBody = mapper.writeValueAsString(request);
        when(taskService.createTask(request)).thenReturn(responseList.get(0));

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(responseList.get(0))));
    }

    @Test
    public void testPositiveUpdateTask() throws Exception {
        TaskUpdateRequest request = createRequestOnUpdate();
        String jsonBody = mapper.writeValueAsString(request);
        when(taskService.updateTask(request)).thenReturn(responseList.get(0));

        mockMvc.perform(put("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(responseList.get(0))));
    }

    @Test
    public void testPositiveGetAllTasksByFilters() throws Exception {
        TaskFilterDto filter = createFilterDto();
        String jsonBody = mapper.writeValueAsString(filter);
        when(taskService.getAllTasksByFilters(firstId, filter)).thenReturn(responseList);

        mockMvc.perform(post("/tasks/{projectId}", firstId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(responseList.size())))
                .andExpect(content().json(mapper.writeValueAsString(responseList)));
    }

    @Test
    public void testPositiveGetAllTasks() throws Exception {
        when(taskService.getAllTasks(firstId)).thenReturn(responseList);

        mockMvc.perform(get("/tasks/{projectId}", firstId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(responseList.size())));
    }

    @Test
    public void testPositiveGetTaskById() throws Exception {
        when(taskService.getTaskById(firstId)).thenReturn(responseList.get(0));

        mockMvc.perform(get("/tasks/task-{taskId}", firstId))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(responseList.get(0))));
    }

    private TaskCreateRequest createRequestOnCreate() {
        return TaskCreateRequest.builder()
                .name(firstName)
                .performerUserId(firstId)
                .reporterUserId(secondId)
                .projectId(firstId)
                .build();
    }

    private TaskUpdateRequest createRequestOnUpdate() {
        return TaskUpdateRequest.builder()
                .id(firstId)
                .description(firstDescription)
                .status(firstStatus)
                .minutesTracked(minutes)
                .parentTaskId(secondId)
                .linkedTasksIds(List.of(firstId, secondId))
                .build();
    }

    private TaskResponse createResponse(Long performerId, Long reporterId, LocalDateTime createdAt,
                                        LocalDateTime deadline, Long parentTaskId, List<Long> linkedTasksIds) {
        return TaskResponse.builder()
                .name(firstName)
                .description(firstDescription)
                .status(firstStatus)
                .performerUserId(performerId)
                .reporterUserId(reporterId)
                .createdAt(createdAt)
                .deadline(deadline)
                .parentTaskId(parentTaskId)
                .linkedTasksIds(linkedTasksIds)
                .build();
    }

    private TaskFilterDto createFilterDto() {
        return TaskFilterDto.builder()
                .build();
    }
}
