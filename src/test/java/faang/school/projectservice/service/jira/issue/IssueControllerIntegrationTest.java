package faang.school.projectservice.service.jira.issue;

import faang.school.projectservice.ProjectServiceApplication;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@SpringBootTest(classes = ProjectServiceApplication.class)
@AutoConfigureMockMvc
class IssueControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final String projectKey = "testProjectKey";
    private final String username = "testUsername";
    private final String password = "testPassword";
    private final String baseUrl = "testBaseUrl";

    @Test
    void testCreateIssue_shouldReturnValidData() throws Exception {
        String requestBody = """
            {
                "typeId": 10005,
                "summary": "Some summary"
            }
            """;

        mockMvc.perform(post("/jira/issue/" + projectKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-jira-username", username)
                        .header("x-jira-password", password)
                        .header("x-jira-base-url", baseUrl)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.typeId").value(10005))
                .andExpect(jsonPath("$.summary").value("Some summary"));
    }
}
