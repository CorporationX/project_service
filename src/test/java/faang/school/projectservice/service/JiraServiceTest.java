package faang.school.projectservice.service;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import faang.school.projectservice.dto.issue.IssueDto;
import faang.school.projectservice.mapper.IssueMapper;
import io.atlassian.util.concurrent.Promise;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JiraServiceTest {

    @Mock
    private JiraRestClient jiraRestClient;

    @Mock
    private IssueRestClient issueRestClient;

    @Mock
    private SearchRestClient searchRestClient;

    @Mock
    private IssueMapper issueMapper;

    @InjectMocks
    private JiraService jiraService;

    @BeforeEach
    void setUp() {
        lenient().when(jiraRestClient.getIssueClient()).thenReturn(issueRestClient);
        lenient().when(jiraRestClient.getSearchClient()).thenReturn(searchRestClient);
    }

    @Test
    @DisplayName("Create Issue Test")
    void createIssueTest() {
        String projectKey = "PROJ";
        IssueDto issueDto = IssueDto.builder()
                .typeId(10001)
                .summary("Summary")
                .description("Description")
                .dueDate(new LocalDateTime())
                .build();

        BasicIssue basicIssue = new BasicIssue(null, "1", 100L);
        Promise<BasicIssue> promise = mock(Promise.class);
        when(promise.claim()).thenReturn(basicIssue);
        when(issueRestClient.createIssue(any(IssueInput.class))).thenReturn(promise);

        ResponseEntity<Void> response = jiraService.createIssue(projectKey, issueDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Update Issue Description Test")
    void updateIssueDescriptionTest() {
        String issueKey = "ISSUE-1";
        String description = "New Description";
        Promise<Void> promise = mock(Promise.class);

        when(promise.claim()).thenReturn(null);
        when(issueRestClient.updateIssue(eq(issueKey), any(IssueInput.class))).thenReturn(promise);

        jiraService.updateIssueDescription(issueKey, description);

        ArgumentCaptor<IssueInput> captor = ArgumentCaptor.forClass(IssueInput.class);
        verify(issueRestClient).updateIssue(eq(issueKey), captor.capture());
    }

    @Test
    @DisplayName("Get Issue DTO Test")
    void getIssueDtoTest() {
        String issueKey = "ISSUE-1";
        Issue issue = mock(Issue.class);
        IssueDto issueDto = mock(IssueDto.class);

        Promise<Issue> promise = mock(Promise.class);
        when(promise.claim()).thenReturn(issue);
        when(issueRestClient.getIssue(issueKey)).thenReturn(promise);
        when(issueMapper.toIssueDto(issue)).thenReturn(issueDto);

        IssueDto result = jiraService.getIssueDto(issueKey);
        assertEquals(issueDto, result);
    }

    @Test
    @DisplayName("Delete Issue Test")
    void deleteIssueTest() {
        String issueKey = "ISSUE-1";
        boolean deleteSubtask = true;

        when(issueRestClient.deleteIssue(issueKey, deleteSubtask)).thenReturn(mock(Promise.class));

        ResponseEntity<Void> response = jiraService.deleteIssue(issueKey, deleteSubtask);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(issueRestClient).deleteIssue(issueKey, deleteSubtask);
    }

    @Test
    @DisplayName("Search Issues Test")
    void searchIssuesTest() {
        String jql = "project = PROJ";
        SearchResult searchResult = mock(SearchResult.class);
        Iterable<Issue> issues = mock(Iterable.class);
        when(searchResult.getIssues()).thenReturn(issues);
        Promise<SearchResult> searchPromise = mock(Promise.class);
        when(searchPromise.claim()).thenReturn(searchResult);
        when(searchRestClient.searchJql(jql)).thenReturn(searchPromise);

        Iterable<IssueDto> issueDtos = mock(Iterable.class);
        when(issueMapper.toIssueDtoIterable(issues)).thenReturn(issueDtos);

        Iterable<IssueDto> result = jiraService.searchIssues(jql);

        assertEquals(issueDtos, result);
        verify(searchRestClient).searchJql(jql);
        verify(issueMapper).toIssueDtoIterable(issues);
    }
}