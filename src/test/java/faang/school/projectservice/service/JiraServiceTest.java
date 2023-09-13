package faang.school.projectservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.cache.JiraProjectCache;
import faang.school.projectservice.client.JiraClient;
import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.dto.jira.IssueFetchingDto;
import faang.school.projectservice.dto.jira.IssueFilterDto;
import faang.school.projectservice.dto.jira.IssueLinkCreationDto;
import faang.school.projectservice.dto.jira.IssueReadOnlyDto;
import faang.school.projectservice.dto.jira.IssueStatusTransition;
import faang.school.projectservice.dto.jira.IssueStatusUpdateDto;
import faang.school.projectservice.dto.jira.JiraProjectDto;
import faang.school.projectservice.filter.jira.IssueFilter;
import faang.school.projectservice.jpa.JiraProjectRepository;
import faang.school.projectservice.mapper.jira.IssueStatusMapper;
import faang.school.projectservice.mapper.jira.JiraProjectMapper;
import faang.school.projectservice.model.jira.JiraProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JiraServiceTest {
    @InjectMocks
    private JiraService jiraService;
    @Mock
    private JiraProjectRepository jiraProjectRepository;
    @Mock
    private JiraProjectCache jiraProjectCache;
    @Mock
    private JiraClient jiraClient;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private JiraProjectMapper jiraProjectMapper;
    @Mock
    private IssueStatusMapper issueStatusMapper;
    @Mock
    private List<IssueFilter> filters;
    private JiraProject jiraProject;
    private IssueReadOnlyDto issueReadOnlyDtoExpected;
    private IssueFetchingDto issueFetchingDtoExpected;
    private IssueDto issueDto;
    private IssueFilterDto filterDto;
    private String filter;
    private String body;


    @BeforeEach
    void setUp() throws JsonProcessingException {
        jiraProject = JiraProject.builder()
                .id(1L)
                .key("projectKey")
                .username("username")
                .password("password")
                .url("url")
                .build();

        issueReadOnlyDtoExpected = IssueReadOnlyDto.builder()
                .id("id")
                .key("issueKey")
                .build();

        issueFetchingDtoExpected = IssueFetchingDto.builder()
                .total(1L)
                .issues(List.of(issueReadOnlyDtoExpected))
                .build();

        issueDto = IssueDto.builder()
                .fields(IssueDto.IssueFields.builder()
                        .summary("")
                        .build())
                .build();

        filterDto = IssueFilterDto.builder().build();

        filter = "";
        body = "body";

        String issueJson = "issueJson";

        when(jiraClient.getIssue(jiraProject, "issueKey")).thenReturn(issueJson);
        when(jiraClient.createIssue(jiraProject, body)).thenReturn("issueJson");
        when(jiraClient.updateIssue(jiraProject, "issueKey", body)).thenReturn("success");
        when(objectMapper.readValue(issueJson, IssueReadOnlyDto.class)).thenReturn(issueReadOnlyDtoExpected);
        when(jiraProjectCache.get("projectKey")).thenReturn(Optional.of(jiraProject));
        when(jiraProjectRepository.save(jiraProject)).thenReturn(jiraProject);
        when(jiraClient.getIssuesWithFilter(jiraProject, filter)).thenReturn("issueFetchingJson");
        when(objectMapper.readValue("issueFetchingJson", IssueFetchingDto.class))
                .thenReturn(issueFetchingDtoExpected);
        when(objectMapper.writeValueAsString(issueDto)).thenReturn(body);
    }

    @Test
    void testSaveProject() {
        JiraProjectDto expected = JiraProjectDto.builder()
                .id(1L)
                .key("projectKey")
                .username("username")
                .password("password")
                .url("url")
                .build();

        when(jiraProjectMapper.toEntity(expected)).thenReturn(jiraProject);
        when(jiraProjectMapper.toDto(jiraProject)).thenReturn(expected);

        JiraProjectDto actual = jiraService.saveProject(expected);
        verify(jiraProjectRepository).save(jiraProject);
        assertEquals(expected, actual);
    }

    @Test
    void testGetIssue() throws JsonProcessingException {
        IssueReadOnlyDto actual = jiraService.getIssue("projectKey", "issueKey");

        verify(jiraClient).getIssue(jiraProject, "issueKey");
        verify(objectMapper).readValue("issueJson", IssueReadOnlyDto.class);
        assertEquals(issueReadOnlyDtoExpected, actual);
    }

    @Test
    void testGetIssues_shouldInvokeFilterIsApplicable() {
        jiraService.getIssuesWithFilter("projectKey", filterDto);
        filters.forEach(f -> verify(f).isApplicable(filterDto));
    }

    @Test
    void testGetIssues_shouldReturnIssueFetchingDto() throws JsonProcessingException {
        IssueFetchingDto actual = jiraService.getIssuesWithFilter("projectKey", filterDto);

        verify(jiraClient).getIssuesWithFilter(jiraProject, filter);
        verify(objectMapper).readValue("issueFetchingJson", IssueFetchingDto.class);
        assertEquals(issueFetchingDtoExpected, actual);
    }

    @Test
    void testGetIssuesWithFilter_shouldInvokeFilterIsApplicable() {
        jiraService.getIssuesWithFilter("projectKey", filterDto);
        filters.forEach(f -> verify(f).isApplicable(filterDto));
    }

    @Test
    void testGetIssuesWithFilter_shouldInvokeFilterApply() {
        filterDto = IssueFilterDto.builder()
                .assignee("assignee")
                .status("status")
                .project("projectKey")
                .build();
        StringBuilder filterBuilder = new StringBuilder();

        jiraService.getIssuesWithFilter("projectKey", filterDto);
        filters.forEach(f -> verify(f).apply(filterBuilder, filterDto));
    }

    @Test
    void testGetIssuesWithFilter_shouldReturnIssueFetchingDto() throws JsonProcessingException {
        IssueFetchingDto actual = jiraService.getIssuesWithFilter("projectKey", filterDto);

        verify(jiraClient).getIssuesWithFilter(jiraProject, filter);
        verify(objectMapper).readValue("issueFetchingJson", IssueFetchingDto.class);
        assertEquals(issueFetchingDtoExpected, actual);
    }

    @Test
    void testCreateIssue() throws JsonProcessingException {
        IssueReadOnlyDto actual = jiraService.createIssue("projectKey", issueDto);

        verify(objectMapper).writeValueAsString(issueDto);
        verify(jiraClient).createIssue(jiraProject, body);
        verify(objectMapper).readValue("issueJson", IssueReadOnlyDto.class);
        assertEquals(actual, issueReadOnlyDtoExpected);
    }

    @Test
    void testUpdateIssue() throws JsonProcessingException {
        String actual = jiraService.updateIssue("projectKey", "issueKey", issueDto);

        verify(objectMapper).writeValueAsString(issueDto);
        verify(jiraClient).updateIssue(jiraProject, "issueKey", body);
        assertEquals(actual, "success");
    }

    @Test
    void testChangeIssueStatus() throws JsonProcessingException {
        IssueStatusUpdateDto statusUpdateDto = IssueStatusUpdateDto.builder().build();

        IssueStatusTransition transition = IssueStatusTransition.builder().build();

        when(issueStatusMapper.toTransition(statusUpdateDto)).thenReturn(transition);
        when(objectMapper.writeValueAsString(transition)).thenReturn(body);
        when(jiraClient.changeIssueStatus(jiraProject, "issueKey", body)).thenReturn("success");

        String actual = jiraService.changeIssueStatus("projectKey", "issueKey", statusUpdateDto);
        verify(issueStatusMapper).toTransition(statusUpdateDto);
        verify(objectMapper).writeValueAsString(transition);
        verify(jiraClient).changeIssueStatus(jiraProject, "issueKey", body);
        assertEquals(actual, "success");
    }

    @Test
    void testCreateIssueLink() throws JsonProcessingException {
        IssueLinkCreationDto issueLinkCreationDto = IssueLinkCreationDto.builder().build();

        when(objectMapper.writeValueAsString(issueLinkCreationDto)).thenReturn(body);
        when(jiraClient.createIssueLink(jiraProject, body)).thenReturn("success");

        String actual = jiraService.createIssueLink("projectKey", issueLinkCreationDto);

        verify(objectMapper).writeValueAsString(issueLinkCreationDto);
        verify(jiraClient).createIssueLink(jiraProject, body);
        assertEquals(actual, "success");
    }
}