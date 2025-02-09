package faang.school.projectservice.service.jira;

import faang.school.projectservice.client.jira.JiraClientImpl;
import faang.school.projectservice.client.jira.JiraRestClientConfig;
import faang.school.projectservice.dto.jira.request.create.IssueCreateRequestDto;
import faang.school.projectservice.dto.jira.request.create.IssueFieldsCreateRequestDto;
import faang.school.projectservice.dto.jira.request.update.IssueFieldsUpdateRequestDto;
import faang.school.projectservice.dto.jira.request.update.IssueUpdateRequestDto;
import faang.school.projectservice.dto.jira.response.IssueCreateResponseDto;
import faang.school.projectservice.dto.jira.response.IssueDto;
import faang.school.projectservice.dto.jira.response.IssueResponseDto;
import faang.school.projectservice.properties.JiraProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(JiraClientImpl.class)
@Import(JiraRestClientConfig.class)
class JiraClientImplTest {

    @MockBean
    private JiraProperties jiraProperties;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    private RestClient restClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private JiraClientImpl jiraService;

    @BeforeEach
    public void init() {
        when(jiraProperties.getUrl()).thenReturn("http://example.com/api");
        when(jiraProperties.getUsername()).thenReturn("username");
        when(jiraProperties.getToken()).thenReturn("token");
    }

    @Test
    public void testGetAllIssuesByProject() {
        String projectId = "PROJ";
        String responseBody = "{\"issues\":[{\"key\":\"1\",\"summary\":\"Test Issue\"}]}";
        mockServer.expect(requestTo("/search?jql=project%3DPROJ"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        IssueResponseDto response = jiraService.getAllIssuesByProject(projectId);

        assertNotNull(response);
        assertEquals(1, response.issues().size());
        assertEquals("1", response.issues().get(0).key());
        mockServer.verify();
    }

    @Test
    public void testGetIssueById() {
        String issueId = "PROJ-123";
        String responseBody = "{\"key\":\"1\",\"summary\":\"Test Issue\"}";
        mockServer.expect(requestTo("/issue/" + issueId))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        IssueDto response = jiraService.getIssueById(issueId);

        assertNotNull(response);
        assertEquals("1", response.key());
        mockServer.verify();
    }

    @Test
    public void testGetIssuesByAssignee() {
        String assigneeId = "user";
        String responseBody = "{\"issues\":[{\"key\":\"1\",\"summary\":\"Test Issue\"}]}";
        mockServer.expect(requestTo("/search?jql=assignee%3Duser"))
//                String.format("/search?jql=assignee=%s", assigneeId)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        IssueResponseDto response = jiraService.getIssuesByAssignee(assigneeId);

        assertNotNull(response);
        assertEquals(1, response.issues().size());
        assertEquals("1", response.issues().get(0).key());
        mockServer.verify();
    }

    @Test
    public void testGetIssuesByStatus() {
        String issueStatus = "In Progress";
        String responseBody = "{\"issues\":[{\"key\":\"1\",\"summary\":\"Test Issue\"}]}";
        mockServer.expect(requestTo("/search?jql=status%3DIn%20Progress"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        IssueResponseDto response = jiraService.getIssuesByStatus(issueStatus);

        assertNotNull(response);
        assertEquals(1, response.issues().size());
        assertEquals("1", response.issues().get(0).key());
        mockServer.verify();
    }

    @Test
    public void testCreateIssue() {
        String responseBody = "{\"id\":\"123\",\"key\":\"PROJ-123\"}";
        mockServer.expect(requestTo("/issue"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        IssueCreateResponseDto response = jiraService.createIssue(getIssueCreateRequestDto());

        assertNotNull(response);
        assertEquals("123", response.id());
        assertEquals("PROJ-123", response.key());
        mockServer.verify();
    }

    @Test
    public void testEditIssue() {
        String issueId = "PROJ-123";
        IssueUpdateRequestDto requestDto = getIssueUpdateRequestDto();
        mockServer.expect(requestTo("/issue/" + issueId))
                .andRespond(MockRestResponseCreators.withNoContent());

        jiraService.editIssue(issueId, requestDto);

        mockServer.verify();
    }

    private IssueUpdateRequestDto getIssueUpdateRequestDto() {
        return new IssueUpdateRequestDto(
                IssueFieldsUpdateRequestDto.builder()
                        .summary("Updated Summary")
                        .description("Updated Description")
                        .build());
    }

    private IssueCreateRequestDto getIssueCreateRequestDto() {
        return new IssueCreateRequestDto(
                IssueFieldsCreateRequestDto.builder()
                        .summary("Test issue")
                        .description("Test Description")
                        .build()
        );
    }
}