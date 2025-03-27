package projectservice.jira;

import faang.school.projectservice.client.JiraClient;
import faang.school.projectservice.dto.jira.request.IssueRequestDto;
import faang.school.projectservice.dto.jira.response.IssueResponseDto;
import faang.school.projectservice.filter.jira.IssueFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.jira.JiraServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JiraServiceTest {
    @InjectMocks
    private JiraServiceImpl jiraService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private List<IssueFilter> issueFilters;

    @Spy
    private ProjectMapper projectMapper;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private JiraClient jiraClient;

    private final IssueRequestDto issueRequestDto = new IssueRequestDto()

    @Test
    public void testCreateIssue_createdSuccessfully(){
        IssueResponseDto issueResponseDto = new IssueResponseDto();
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/issue")).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(issueRequestDto)).thenReturn(requestBodySpec);
    }
    //then/theReturn
}
