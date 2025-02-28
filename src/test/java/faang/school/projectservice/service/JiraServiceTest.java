package faang.school.projectservice.service;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import faang.school.projectservice.dto.issue.IssueDto;
import faang.school.projectservice.mapper.IssueMapper;
import io.atlassian.util.concurrent.Promise;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private IssueDto issueDto;
    private Issue issue;
    private BasicIssue basicIssue;
    private String projectKey;
    private String issueKey;
    private Promise promise;

    @Value("${jira.max-results}")
    private int maxResults;

    @BeforeEach
    void setUp() {
        projectKey = "PROJ";
        issueKey = "PROJ-123";
        issueDto = IssueDto.builder()
                .key("PROJ-123")
                .summary("Test issue")
                .description("This is a test issue")
                .statusId(1L)
                .typeId(1L)
                .dueDate(LocalDateTime.now())
                .build();
        basicIssue = new BasicIssue(null, issueKey, 100L);
        issue = mock(Issue.class);
        promise = mock(Promise.class);
        lenient().when(jiraRestClient.getIssueClient()).thenReturn(issueRestClient);
        lenient().when(jiraRestClient.getSearchClient()).thenReturn(searchRestClient);
    }

    @Test
    void testCreateIssue() {
        when(promise.claim()).thenReturn(basicIssue);
        when(issueRestClient.createIssue(any(IssueInput.class))).thenReturn(promise);

        String result = jiraService.createIssue(projectKey, issueDto);

        assertEquals(issueKey, result);
        verify(promise).claim();
    }

    @Test
    void testUpdateIssueDescription() {
        String newDescription = "Updated description";
        when(issueRestClient.updateIssue(eq(issueKey), any(IssueInput.class))).thenReturn(promise);

        jiraService.updateIssueDescription(issueKey, newDescription);

        ArgumentCaptor<IssueInput> captor = ArgumentCaptor.forClass(IssueInput.class);
        verify(issueRestClient).updateIssue(eq(issueKey), captor.capture());
    }

    @Test
    void testUpdateIssueStatus() {
        int statusId = 2;
        when(promise.claim()).thenReturn(issue);
        when(issueRestClient.getIssue(any())).thenReturn(promise);
        when(jiraRestClient.getIssueClient().transition(eq(issue), any(TransitionInput.class))).thenReturn(promise);

        jiraService.updateIssueStatus(issueKey, statusId);

        ArgumentCaptor<TransitionInput> captor = ArgumentCaptor.forClass(TransitionInput.class);
        verify(jiraRestClient.getIssueClient()).transition(eq(issue), captor.capture());
    }

    @Test
    void testUpdateParentIssue() {
        String parentKey = "PROJ_MASTER";
        when(issueRestClient.updateIssue(eq(issueKey), any(IssueInput.class))).thenReturn(promise);

        jiraService.updateParentIssue(issueKey, parentKey);

        ArgumentCaptor<IssueInput> captor = ArgumentCaptor.forClass(IssueInput.class);
        verify(issueRestClient).updateIssue(eq(issueKey), captor.capture());
    }

    @Test
    void testGetIssueDto() {
        when(promise.claim()).thenReturn(issue);
        when(issueRestClient.getIssue(issueKey)).thenReturn(promise);
        when(issueMapper.toIssueDto(issue)).thenReturn(issueDto);

        IssueDto result = jiraService.getIssueDto(issueKey);
        assertEquals(issueDto, result);
    }

    @Test
    void testGetIssueWithFilter() {
        String jql = "filter";
        SearchResult searchResult = mock(SearchResult.class);
        Iterable<Issue> issues = mock(Iterable.class);
        Promise<SearchResult> searchPromise = mock(Promise.class);
        Set<String> fields = Set.of("summary", "assignee", "status", "dueDate", "comments");
        Iterable<IssueDto> issuesDto = List.of(issueDto);

        when(searchResult.getIssues()).thenReturn(issues);
        when(searchPromise.claim()).thenReturn(searchResult);
        when(searchRestClient.searchJql(eq(jql), eq(maxResults), eq(0), eq(fields))).thenReturn(searchPromise);
        when(issueMapper.toIterableIssueDto(issues)).thenReturn(issuesDto);

        Iterable<IssueDto> result = jiraService.getIssueWithFilter(jql);

        assertEquals(issuesDto, result);
        verify(searchRestClient).searchJql(eq(jql), eq(maxResults), eq(0), eq(fields));
        verify(issueMapper).toIterableIssueDto(issues);
    }

    @Test
    void testAddComment() {
        String commentBody = "Test comment";
        Comment comment = Comment.valueOf(commentBody);
        Promise<Void> promiseVoid = mock(Promise.class);
        when(promise.claim()).thenReturn(issue);
        when(issueRestClient.getIssue(any())).thenReturn(promise);
        when(issue.getVotesUri()).thenReturn(URI.create("http://example.com"));
        when(issueRestClient.addComment(any(URI.class), eq(comment))).thenReturn(promiseVoid);

        jiraService.addComment(issueKey, commentBody);
        verify(issueRestClient).addComment(eq(issue.getVotesUri()), eq(comment));
    }

    @Test
    void testDeleteIssue() {
        boolean deleteSubtask = true;
        when(issueRestClient.deleteIssue(issueKey, deleteSubtask)).thenReturn(promise);

        jiraService.deleteIssue(issueKey, deleteSubtask);
        verify(issueRestClient).deleteIssue(issueKey, deleteSubtask);
    }
}
