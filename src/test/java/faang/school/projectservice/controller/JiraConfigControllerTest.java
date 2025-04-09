package faang.school.projectservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.client.jira.JiraProperties;
import faang.school.projectservice.service.JiraConfigService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = JiraConfigController.class)
public class JiraConfigControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @MockBean
    private JiraConfigService jiraConfigService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testPositiveAddConfig() throws Exception {
        JiraProperties properties = createProperties();

        doNothing().when(jiraConfigService).addJiraConfig(properties);

        mockMvc.perform(post("/jira/config")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(properties)))
                .andExpect(status().isOk());
    }

    private JiraProperties createProperties() {
        return JiraProperties.builder()
                .baseUrl("https://test.atlassian.net/")
                .email("test@test.com")
                .apiToken("test-token")
                .build();
    }
}
