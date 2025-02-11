package faang.school.projectservice.controller.jira;

import faang.school.projectservice.dto.jira.request.create.IssueCreateRequestDto;
import faang.school.projectservice.dto.jira.request.create.IssueFieldsCreateRequestDto;
import faang.school.projectservice.dto.jira.request.update.IssueFieldsUpdateRequestDto;
import faang.school.projectservice.dto.jira.request.update.IssueUpdateRequestDto;
import faang.school.projectservice.dto.jira.response.IssueCreateResponseDto;
import faang.school.projectservice.dto.jira.response.IssueDto;
import faang.school.projectservice.dto.jira.response.IssueFieldsResponseDto;
import faang.school.projectservice.dto.jira.response.IssueResponseDto;
import faang.school.projectservice.client.jira.JiraClientImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"domain.path=/api/v1"})
class JiraControllerTest {

    @Mock
    private JiraClientImpl jiraClientImpl;

    @InjectMocks
    private JiraController jiraController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        Properties properties = new Properties();
        properties.setProperty("domain.path", "/api/v1");
        configurer.setProperties(properties);

        mockMvc = MockMvcBuilders.standaloneSetup(jiraController)
                .addPlaceholderValue("domain.path", "/api/v1")
                .build();
    }

    private final String basePath = "/api/v1/jira/issues";

    @Test
    public void testGetAllIssuesByProject() throws Exception {
        String projectId = "PROJ";
        when(jiraClientImpl.getAllIssuesByProject(projectId)).thenReturn(getIssueResponseDto());

        mockMvc.perform(MockMvcRequestBuilders.get(basePath + "/project/{projectId}", projectId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetIssuesById() throws Exception {
        String issueId = "1";
        IssueDto issueDto = new IssueDto("1",
                IssueFieldsResponseDto.builder().summary("Test Issue").build());

        when(jiraClientImpl.getIssueById(issueId)).thenReturn(issueDto);

        mockMvc.perform(MockMvcRequestBuilders.get(basePath + "/{id}", issueId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetIssuesByAssignee() throws Exception {
        String userId = "user123";
        when(jiraClientImpl.getIssuesByAssignee(userId)).thenReturn(getIssueResponseDto());

        mockMvc.perform(MockMvcRequestBuilders.get(basePath + "/assignee/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetIssuesByStatus() throws Exception {
        String status = "In Progress";
        when(jiraClientImpl.getIssuesByStatus(status)).thenReturn(getIssueResponseDto());

        mockMvc.perform(MockMvcRequestBuilders.get(basePath + "/status/{status}", status)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateIssue() throws Exception {
        when(jiraClientImpl.createIssue(eq(getIssueCreateRequestDto()))).thenReturn(getIssueCreateResponseDto());
        String requestBody = """
                {
                  "fields" : {
                    "project" : null,
                    "summary" : "Test Issue",
                    "description" : "Test Description",
                    "issuetype" : null
                  }
                }
                """;

        mockMvc.perform(post(basePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void testEditIssue() throws Exception {
        String issueId = "1";
        doNothing().when(jiraClientImpl).editIssue(eq(issueId), eq(getIssueUpdateRequestDto()));

        String requestBody = """
                {
                  "fields" : {
                    "summary" : "Updated Summary",
                    "description" : "Updated Description",
                    "assignee" : null,
                    "issuetype" : null,
                    "parent" : null,
                    "duedate" : null
                  }
                }
                """;

        mockMvc.perform(put(String.format("%s/%s", basePath, issueId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNoContent());

        verify(jiraClientImpl).editIssue(issueId, getIssueUpdateRequestDto());
    }

    private IssueCreateRequestDto getIssueCreateRequestDto() {
        return new IssueCreateRequestDto(
                IssueFieldsCreateRequestDto.builder()
                        .summary("Test Issue")
                        .description("Test Description")
                        .build());
    }

    private IssueCreateResponseDto getIssueCreateResponseDto() {
        return new IssueCreateResponseDto("key", "PROJ");
    }

    private IssueUpdateRequestDto getIssueUpdateRequestDto() {
        return new IssueUpdateRequestDto(
                IssueFieldsUpdateRequestDto.builder()
                        .summary("Updated Summary")
                        .description("Updated Description")
                        .build());
    }

    private IssueResponseDto getIssueResponseDto() {
        return new IssueResponseDto(
                Collections.singletonList(new IssueDto("1",
                        IssueFieldsResponseDto.builder()
                                .summary("Test Issue")
                                .build())));
    }
}