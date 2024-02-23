package faang.school.projectservice.controller;
import faang.school.projectservice.service.JiraService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ProjectTaskControllerTest {
    private MockMvc mockMvc;

    @Mock
    private JiraService jiraService;

    @InjectMocks
    private ProjectTaskController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void createTask_success() throws Exception {
        String taskJson = "{\"summary\": \"New Task\"}";
        when(jiraService.createIssue(taskJson, 1L, 1L)).thenReturn("Task Created");

        mockMvc.perform(post("/projects/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson)
                        .param("performerUserId", "1")
                        .param("projectId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Task Created"));
    }

    @Test
    void updateTask_success() throws Exception {
        String taskJson = "{\"summary\": \"Updated Task\"}";
        String issueKey = "TEST-1";
        when(jiraService.updateIssue(issueKey, taskJson)).thenReturn("Task Updated");

        mockMvc.perform(put("/projects/tasks/{issueKey}", issueKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Task Updated"));
    }

    @Test
    void getAllTasks_success() throws Exception {
        String jqlResponse = "[{\"id\": \"10001\", \"key\": \"TEST-1\"}]";
        String jqlQuery = "project = BJS2 AND status = \"TODO\" AND assignee = \"user@example.com\"";
        given(jiraService.getIssues(jqlQuery)).willReturn(jqlResponse);

        mockMvc.perform(get("/projects/tasks")
                        .param("status", "TODO")
                        .param("assignee", "user@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string(jqlResponse));
    }

    @Test
    void getTaskById_success() throws Exception {
        String issueId = "TEST-1";
        String issueDetails = "{\"id\": \"10001\", \"key\": \"TEST-1\"}";
        when(jiraService.getIssueById(issueId)).thenReturn(issueDetails);

        mockMvc.perform(get("/projects/tasks/{issueId}", issueId))
                .andExpect(status().isOk())
                .andExpect(content().string(issueDetails));
    }
}
