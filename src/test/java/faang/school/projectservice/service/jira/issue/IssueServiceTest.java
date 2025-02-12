package faang.school.projectservice.service.jira.issue;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import faang.school.projectservice.client.jira.JiraClient;
import faang.school.projectservice.dto.jira.issue.IssueFilterDto;
import faang.school.projectservice.utils.parser.JqlParser;
import io.atlassian.util.concurrent.Promise;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IssueServiceTest {

    @InjectMocks
    private IssueService issueService;

    @Mock
    private JiraClient jiraClient;

    @Mock
    private JqlParser jqlParser;

    @Mock
    private IssueRestClient issueRestClient;

    @Mock
    private SearchRestClient searchRestClient;

    @Mock
    private Promise<BasicIssue> basicIssuePromise;

    @Mock
    private Promise<Issue> issuePromise;

    @Mock
    private Promise<Void> voidPromise;

    @Mock
    private Promise<SearchResult> searchResultPromise;

    @Mock
    private BasicIssue basicIssue;

    @Mock
    private Issue issue;

    @Captor
    private ArgumentCaptor<IssueInput> issueInputCaptor;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    @Test
    void testCreateIssue() {
        IssueInput issueInput = new IssueInputBuilder().build();

        when(jiraClient.getIssueClient()).thenReturn(issueRestClient);
        when(issueRestClient.createIssue(issueInput)).thenReturn(basicIssuePromise);
        when(issueRestClient.getIssue(anyString())).thenReturn(issuePromise);
        when(issuePromise.claim()).thenReturn(issue);
        when(basicIssuePromise.claim()).thenReturn(basicIssue);
        when(basicIssue.getKey()).thenReturn("");

        issueService.createIssue(issueInput);

        verify(jiraClient, times(2))
                .getIssueClient();

        verify(issueRestClient, times(1))
                .createIssue(issueInputCaptor.capture());

        assertEquals(issueInput, issueInputCaptor.getValue());
    }

    @Test
    void testUpdateIssue() {
        IssueInput issueInput = new IssueInputBuilder().build();
        String issueKey = "BJS2-12342";

        when(jiraClient.getIssueClient()).thenReturn(issueRestClient);
        when(issueRestClient.updateIssue(issueKey, issueInput)).thenReturn(voidPromise);
        when(issueRestClient.getIssue(issueKey)).thenReturn(issuePromise);

        issueService.updateIssue(issueKey, issueInput);

        verify(jiraClient, times(2))
                .getIssueClient();

        verify(issueRestClient, times(1))
                .updateIssue(stringCaptor.capture(), issueInputCaptor.capture());

        assertEquals(issueKey, stringCaptor.getValue());
        assertEquals(issueInput, issueInputCaptor.getValue());
    }

    @Test
    void testGetAllIssues() {
        IssueFilterDto filters = new IssueFilterDto();
        Iterable<Issue> issueIterable = Collections.emptyList();

        SearchResult searchResult = new SearchResult(1, 1, 1, issueIterable);

        when(jiraClient.getSearchClient()).thenReturn(searchRestClient);
        when(jqlParser.buildJql(any(IssueFilterDto.class))).thenReturn("");
        when(searchRestClient.searchJql(anyString())).thenReturn(searchResultPromise);
        when(searchResultPromise.claim()).thenReturn(searchResult);

        issueService.getAllIssues(filters);

        verify(jiraClient, times(1))
                .getSearchClient();

        verify(searchRestClient, times(1))
                .searchJql(anyString());
    }

    @Test
    void testGetIssueByKey() {
        String issueKey = "BJS2-12342";

        when(jiraClient.getIssueClient()).thenReturn(issueRestClient);
        when(issueRestClient.getIssue(issueKey)).thenReturn(issuePromise);
        when(issuePromise.claim()).thenReturn(issue);

        issueService.getIssueByKey(issueKey);

        verify(jiraClient, times(1))
                .getIssueClient();

        verify(issueRestClient, times(1))
                .getIssue(issueKey);
    }
}
