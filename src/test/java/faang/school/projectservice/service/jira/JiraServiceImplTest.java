package faang.school.projectservice.service.jira;

import faang.school.projectservice.client.JiraClient;
import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.dto.jira.Assignee;
import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.dto.jira.IssueFilterDto;
import faang.school.projectservice.dto.jira.IssueUpdateDto;
import faang.school.projectservice.dto.jira.JiraResponse;
import faang.school.projectservice.filter.IssueAssigneeFilter;
import faang.school.projectservice.filter.IssueFilter;
import faang.school.projectservice.filter.IssueStatusFilter;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class JiraServiceImplTest {

    private static final List<IssueDto> ISSUES = List.of(
            new IssueDto(),
            new IssueDto(),
            new IssueDto()
    );

    @Mock
    private WebClient webClient;

    @Mock
    private JiraClient jiraClient;

    @Mock
    private WebClient.RequestHeadersUriSpec uriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.RequestBodyUriSpec headerSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private WebClient.RequestBodyUriSpec bodyUriSpec;

    @Mock
    private IssueStatusFilter issueStatusFilter;

    @Mock
    private IssueAssigneeFilter issueAssigneeFilter;

    @Spy
    private List<IssueFilter> issueFilters;

    @Mock
    private ProjectJpaRepository projectRepository;

    @Spy
    private ProjectMapperImpl projectMapper;

    @InjectMocks
    private JiraServiceImpl jiraService;

    private final long projectId = 1L;
    private final String jiraKey = "KEY";
    private final Project project = Project.builder()
            .jiraKey(jiraKey)
            .build();
    private final Assignee assignee = Assignee.builder()
            .accountId("accountId")
            .build();
    private final IssueDto.Status status = IssueDto.Status.builder()
            .name("status")
            .build();
    private final IssueFilterDto filterDto = IssueFilterDto.builder()
            .status(status)
            .assignee(assignee)
            .build();

    @Test
    void registrationInJira_WhenOk() {
        Project project = Project.builder()
                .id(projectId)
                .build();
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        project.setJiraKey(jiraKey);
        ProjectDto correctDto = ProjectDto.builder()
                .id(projectId)
                .jiraKey(jiraKey)
                .build();
        when(projectRepository.save(project)).thenReturn(project);

        ProjectDto result = jiraService.registrationInJira(projectId, jiraKey);

        assertEquals(correctDto, result);
        assertEquals(jiraKey, result.getJiraKey());
        verify(projectRepository).save(project);
        verify(projectRepository).findById(projectId);
    }

    @Test
    void registrationProjectInJira_NotExists() {
        String correctMessage = "Project with id %d not found".formatted(projectId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(EntityNotFoundException.class,
                () -> jiraService.registrationInJira(projectId, jiraKey));

        assertEquals(correctMessage, exception.getMessage());
        verify(projectRepository, never()).save(any(Project.class));
    }

    @ParameterizedTest
    @MethodSource("provideJiraResponses")
    void getAllIssuesByProjectId(JiraResponse response, List<IssueDto> expectedIssues) {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        List<IssueDto> result = jiraService.getAllIssuesByProjectId(projectId);

        assertEquals(expectedIssues, result);
    }

    @Test
    void getAllIssuesByProjectId_getJiraProjectKey_NotExistsJiraKey() {
        String correctMessage = "Проект с id 1 не связан с Jira";
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(EntityNotFoundException.class,
                () -> jiraService.getAllIssuesByProjectId(projectId));

        assertEquals(correctMessage, exception.getMessage());
    }

    @Test
    void getIssueByKey() {
        String issueKey = "KEY";
        IssueDto correctDto = IssueDto.builder()
                .key(issueKey)
                .build();
        when(jiraClient.getIssueByKey(issueKey)).thenReturn(correctDto);

        IssueDto result = jiraService.getIssueByKey(issueKey);

        assertEquals(correctDto, result);
    }

    @Test
    void getAllIssuesWithFilterByProjectId_WhenOk() {
        JiraResponse response = JiraResponse.builder()
                .issues(ISSUES)
                .build();
        mockFilters(filterDto);
        when(jiraClient.getProjectInfoByJql(anyString())).thenReturn(response);

        List<IssueDto> result = jiraService.getAllIssuesWithFilterByProjectId(projectId, filterDto);

        assertEquals(ISSUES, result);
    }

    @Test
    void getAllIssuesWithFilterByProjectId_WhenIssuesIsNull() {
        JiraResponse response = new JiraResponse();
        mockFilters(filterDto);
        when(jiraClient.getProjectInfoByJql(anyString())).thenReturn(response);

        List<IssueDto> result = jiraService.getAllIssuesWithFilterByProjectId(projectId, filterDto);

        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void createIssue() {
        IssueDto issueDto = new IssueDto();
        when(jiraClient.createIssue(issueDto)).thenReturn(issueDto);

        IssueDto result = jiraService.createIssue(issueDto);

        assertEquals(issueDto, result);
    }

    @Test
    void testUpdateIssueWithLinks() {
        String issueKey = "ISSUE-123";
        IssueUpdateDto issueDto = new IssueUpdateDto();
        IssueUpdateDto.Fields fields = new IssueUpdateDto.Fields();
        var issueLinks = List.of(new IssueUpdateDto.IssueLink(), new IssueUpdateDto.IssueLink());
        fields.setIssueLinks(issueLinks);
        issueDto.setFields(fields);

        jiraService.updateIssueByKey(issueKey, issueDto);

        verify(jiraClient).createIssueLinks(issueLinks);
        verify(jiraClient).updateIssueByKey(issueKey, issueDto);
        assertNull(fields.getIssueLinks());
    }

    @Test
    void testUpdateIssueWithTransition() {
        String issueKey = "ISSUE-456";
        IssueUpdateDto.Transition transition = new IssueUpdateDto.Transition();
        IssueUpdateDto issueDto = new IssueUpdateDto();
        issueDto.setTransition(transition);

        jiraService.updateIssueByKey(issueKey, issueDto);

        verify(jiraClient).setTransitionByKey(issueKey, transition);
        verify(jiraClient).updateIssueByKey(issueKey, issueDto);
        assertNull(issueDto.getTransition());
    }

    @Test
    void testUpdateIssueWithoutLinksAndTransition() {
        String issueKey = "ISSUE-789";
        IssueUpdateDto issueDto = new IssueUpdateDto();
        issueDto.setFields(new IssueUpdateDto.Fields());

        jiraService.updateIssueByKey(issueKey, issueDto);

        verify(jiraClient, never()).createIssueLinks(any());
        verify(jiraClient, never()).setTransitionByKey(anyString(), any());
        verify(jiraClient).updateIssueByKey(issueKey, issueDto);
    }

    private static Stream<Arguments> provideJiraResponses() {
        JiraResponse responseWithIssues = JiraResponse.builder()
                .issues(ISSUES)
                .build();
        return Stream.of(
                Arguments.of(responseWithIssues, ISSUES),
                Arguments.of(new JiraResponse(), Collections.emptyList())
        );
    }

    private void mockFilters(IssueFilterDto filterDto) {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(issueFilters.stream()).thenReturn(Stream.of(issueStatusFilter, issueAssigneeFilter));
        when(issueStatusFilter.isApplicable(filterDto)).thenReturn(true);
        when(issueStatusFilter.createJql(filterDto)).thenReturn("status = status");
        when(issueAssigneeFilter.isApplicable(filterDto)).thenReturn(true);
        when(issueAssigneeFilter.createJql(filterDto)).thenReturn("transition = accountId");
    }
}