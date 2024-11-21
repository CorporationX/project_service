package faang.school.projectservice.service.jira;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.dto.jira.IssueFilterDto;
import faang.school.projectservice.dto.jira.IssueTypeDto;
import faang.school.projectservice.filter.jirafilter.IssueFilter;
import faang.school.projectservice.mapper.IssueMapper;
import io.atlassian.util.concurrent.Promise;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JiraServiceTest {

    @InjectMocks
    private JiraServiceImpl jiraService;

    @Mock
    private JiraRestClient jiraRestClient;

    @Mock
    private IssueRestClient issueRestClient;

    @Mock
    private SearchRestClient searchRestClient;

    @Mock
    private List<IssueFilter> issueFilters;

    @Spy
    private IssueMapper issueMapper;

    private IssueDto issueDto;
    private IssueTypeDto issueTypeDto;
    private final List<IssueDto> issueDtos = new ArrayList<>();

    @BeforeEach
    void setUp() {
        lenient().when(jiraRestClient.getIssueClient()).thenReturn(issueRestClient);
        lenient().when(jiraRestClient.getSearchClient()).thenReturn(searchRestClient);
        issueTypeDto = IssueTypeDto.builder()
                .id(1L)
                .name("name")
                .description("test")
                .isSubtask(true)
                .build();
        issueDto = IssueDto.builder()
                .issueKey("BJS2-274")
                .typeId(1001)
                .summary("ProjectTest")
                .description("TestD")
                .dueDate(new LocalDateTime())
                .issueType(issueTypeDto)
                .build();
        IssueFilter filter = mock(IssueFilter.class);
    }

    @Test
    public void testCreateIssueSuccess() {
        String projectKey = "BJS2";
        BasicIssue basicIssue = new BasicIssue(null, "1", 100L);
        Issue issue = mock(Issue.class);
        IssueType issueType = mock(IssueType.class);
        when(issueMapper.toIssueType(any(IssueTypeDto.class))).thenReturn(issueType);
        Promise<BasicIssue> promise = mock(Promise.class);
        Promise<Issue> promise1 = mock(Promise.class);
        when(promise.claim()).thenReturn(basicIssue);
        when(issueRestClient.createIssue(any(IssueInput.class))).thenReturn(promise);
        when(promise1.claim()).thenReturn(issue);
        when(issueRestClient.getIssue("1")).thenReturn(promise1);

        when(issueMapper.toIssueDto(issue)).thenReturn(issueDto);

        IssueDto result = jiraService.createIssue(projectKey, issueDto);
        verify(promise, times(1)).claim();
    }

    @Test
    public void testGetAllIssuesByFilter() {
        String projectKey = "BJS2";
        String jql = "res";
        IssueFilterDto filterDto = new IssueFilterDto("Project","Status", "Assignee");
        SearchResult result = mock(SearchResult.class);
        Promise<SearchResult> promise = mock(Promise.class);
        when(searchRestClient.searchJql(anyString())).thenReturn(promise);
        when(promise.claim()).thenReturn(result);
        when(issueMapper.toIssueDtos(any(List.class))).thenReturn(issueDtos);

        List<IssueDto> dtoList = jiraService.getAllIssueByFilter(filterDto);

        assertEquals(dtoList, issueDtos);
    }

    @Test
    public void testGetIssue() {
        String issueKey = "BJS2-274";
        Issue issue = mock(Issue.class);
        Promise<Issue> promise = mock(Promise.class);
        when(promise.claim()).thenReturn(issue);
        when(issueRestClient.getIssue(issueKey)).thenReturn(promise);
        when(issueMapper.toIssueDto(issue)).thenReturn(issueDto);

        IssueDto result = jiraService.getIssue(issueKey);
        assertEquals(issueDto, result);
    }
}
