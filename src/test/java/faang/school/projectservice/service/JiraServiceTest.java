package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.TaskDto;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JiraServiceTest {
    @Mock
    private OkHttpClient client;

    @Mock
    private TaskService taskService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private JiraService jiraService;

    private final String jiraBaseUrl = "http://example.com";
    private final String authToken = "authToken";
    private final String email = "email@example.com";

    @BeforeEach
    void setUp() {
        this.jiraService = new JiraService(client, taskService, userContext, jiraBaseUrl, authToken, email);
    }

    @Test
    void createIssue_Success() throws IOException {
        String issueJson = "{\"fields\": {\"summary\": \"Test summary\"}}";
        String responseJson = "{\"id\": \"10001\", \"key\": \"TEST-1\"}";
        prepareMockResponse(200, responseJson);

        String result = jiraService.createIssue(issueJson, 1L, 1L);

        assertNotNull(result);
        assertTrue(result.contains("\"id\": \"10001\""));
    }

    @Test
    void updateIssue_Success() throws IOException {
        String issueJson = "{\"fields\": {\"summary\": \"Updated summary\"}}";
        String issueKey = "TEST-1";
        prepareMockResponse(200, "{}");

        String result = jiraService.updateIssue(issueKey, issueJson);

        assertNotNull(result);
    }

    @Test
    void getIssueById_Success() throws IOException {
        String issueId = "TEST-1";
        String responseJson = "{\"id\": \"10001\", \"key\": \"TEST-1\", \"fields\": {\"summary\": \"Test summary\"}}";
        prepareMockResponse(200, responseJson);

        String result = jiraService.getIssueById(issueId);

        assertNotNull(result);
        assertTrue(result.contains("\"id\": \"10001\""));
    }

    @Test
    void getIssues_Success() throws IOException {
        String jql = "project = TEST";
        String responseJson = "{\"issues\": [{\"id\": \"10001\", \"key\": \"TEST-1\", \"fields\": {\"summary\": \"Issue summary\"}}]}";
        prepareMockResponse(200, responseJson); // Метод для мокирования ответа, как показано выше

        String result = jiraService.getIssues(jql);

        assertNotNull(result);
        assertTrue(result.contains("\"id\": \"10001\""));
        assertTrue(result.contains("\"key\": \"TEST-1\""));
        assertTrue(result.contains("\"summary\": \"Issue summary\""));
    }

    @Test
    void convertJsonToDto_Success() throws IOException {
        String requestJson = "{\"fields\": {\"summary\": \"Test summary\", \"description\": \"Test description\"}}";
        String responseJson = "{\"id\": \"10001\", \"key\": \"TEST-1\"}";

        TaskDto result = jiraService.convertJsonToDto(requestJson, responseJson);

        assertNotNull(result);
        assertEquals(10001, result.getId());
        assertEquals("Test summary", result.getName());
        assertEquals("Test description", result.getDescription());
    }

    private void prepareMockResponse(int code, String responseJson) throws IOException {
        Response response = new Response.Builder()
                .request(new Request.Builder().url(jiraBaseUrl).build())
                .protocol(Protocol.HTTP_1_1)
                .code(code)
                .message("OK")
                .body(ResponseBody.create(responseJson, MediaType.get("application/json")))
                .build();

        Call mockCall = mock(Call.class);
        when(mockCall.execute()).thenReturn(response);
        when(client.newCall(any(Request.class))).thenReturn(mockCall);
    }
}
