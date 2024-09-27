package faang.school.projectservice.service.jira;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.jira.Assignee;
import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.dto.jira.IssueFilterDto;
import faang.school.projectservice.dto.jira.IssueUpdateDto;
import faang.school.projectservice.dto.jira.JiraResponse;
import faang.school.projectservice.exception.EntityFieldNotFoundException;
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
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private final String issueKey = "TEST-123";

    @Test
    void registrationProjectInJira_WhenOk() {
        Project project = Project.builder()
                .id(projectId)
                .build();
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        project.setJiraKey(jiraKey);
        ProjectDto correctDto = new ProjectDto(projectId, jiraKey);
        when(projectRepository.save(project)).thenReturn(project);

        ProjectDto result = jiraService.registrationProjectInJira(projectId, jiraKey);

        assertEquals(correctDto, result);
        assertEquals(jiraKey, result.getJiraKey());
        verify(projectRepository).save(project);
        verify(projectRepository).findById(projectId);
    }

    @Test
    void registrationProjectInJira_NotExistsProject() {
        String correctMessage = "Project with id %d not found".formatted(projectId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(EntityNotFoundException.class,
                () -> jiraService.registrationProjectInJira(projectId, jiraKey));

        assertEquals(correctMessage, exception.getMessage());
        verify(projectRepository, never()).save(any(Project.class));
    }

    @ParameterizedTest
    @MethodSource("provideJiraResponses")
    void getAllIssuesByProjectId(JiraResponse response, List<IssueDto> expectedIssues) {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        mockFetchWithJql(response);

        List<IssueDto> result = jiraService.getAllIssuesByProjectId(projectId);

        assertEquals(expectedIssues, result);
    }

    @Test
    void getAllIssuesByProjectId_getJiraProjectKey_NotExistsJiraKey() {
        String correctMessage = "Данный проект не связан с Jira";
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(EntityFieldNotFoundException.class,
                () -> jiraService.getAllIssuesByProjectId(projectId));

        assertEquals(correctMessage, exception.getMessage());
    }

    @Test
    void getIssueByKey() {
        String issueKey = "KEY";
        IssueDto correctDto = IssueDto.builder()
                .key(issueKey)
                .build();
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), eq(issueKey))).thenReturn(headerSpec);
        when(headerSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(IssueDto.class)).thenReturn(Mono.just(correctDto));

        IssueDto result = jiraService.getIssueByKey(issueKey);

        assertEquals(correctDto, result);
    }

    @Test
    void getAllIssuesWithFilterByProjectId_WhenOk() {
        JiraResponse response = JiraResponse.builder()
                .issues(ISSUES)
                .build();
        mockFilters(filterDto);
        mockFetchWithJql(response);

        List<IssueDto> result = jiraService.getAllIssuesWithFilterByProjectId(projectId, filterDto);

        assertEquals(ISSUES, result);
    }

    @Test
    void getAllIssuesWithFilterByProjectId_WhenIssuesIsNull() {
        JiraResponse response = new JiraResponse();
        mockFilters(filterDto);
        mockFetchWithJql(response);

        List<IssueDto> result = jiraService.getAllIssuesWithFilterByProjectId(projectId, filterDto);

        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void createIssue() {
        IssueDto issueDto = new IssueDto();
        var requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);

        when(webClient.post()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri("/issue")).thenReturn(headerSpec);
        when(headerSpec.bodyValue(issueDto)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(IssueDto.class)).thenReturn(Mono.just(issueDto));

        IssueDto result = jiraService.createIssue(issueDto);

        assertEquals(issueDto, result);
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

    private void mockFetchWithJql(JiraResponse response) {
        var requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headerSpec);
        when(responseSpec.bodyToMono(JiraResponse.class)).thenReturn(Mono.just(response));
        when(requestHeadersUriSpec.uri(any(Function.class))).thenAnswer(invocation -> requestHeadersSpec);
        when(headerSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void updateIssueByKey_ShouldCallWebClientForIssueLinks() {
        IssueUpdateDto issueUpdateDto = new IssueUpdateDto();
        IssueUpdateDto.Fields fields = new IssueUpdateDto.Fields();
        fields.setIssueLinks(List.of(new IssueUpdateDto.IssueLink()));
        issueUpdateDto.setFields(fields);
        var requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        var requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);

        when(webClient.post()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(eq("/issueLink"))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any(IssueUpdateDto.IssueLink.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/issue/{key}", issueKey)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(eq(issueUpdateDto))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(eq(IssueDto.class))).thenReturn(Mono.just(new IssueDto()));

        jiraService.updateIssueByKey(issueKey, issueUpdateDto);

        verify(webClient).post();
        verify(requestBodyUriSpec).uri("/issue/{key}", issueKey);
        verify(requestBodySpec).bodyValue(any(IssueUpdateDto.IssueLink.class));
    }

    @Test
    void updateIssueByKey_ShouldCallWebClientForTransition() {
        IssueUpdateDto issueUpdateDto = new IssueUpdateDto();
        IssueUpdateDto.Fields fields = new IssueUpdateDto.Fields();
        fields.setIssueLinks(List.of(new IssueUpdateDto.IssueLink()));
        issueUpdateDto.setFields(fields);
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);

        when(webClient.post()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(eq("/issueLink"))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any(IssueUpdateDto.IssueLink.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/issue/{key}", issueKey)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(eq(issueUpdateDto))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(eq(IssueDto.class))).thenReturn(Mono.just(new IssueDto()));

        jiraService.updateIssueByKey(issueKey, issueUpdateDto);

        verify(webClient).post();
        verify(requestBodyUriSpec).uri("/issue/{key}", issueKey);
        verify(requestBodySpec).bodyValue(any(IssueUpdateDto.IssueLink.class));
    }

    @Test
    void updateIssueByKey_ShouldCallWebClientForUpdateIssue() {
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        IssueUpdateDto issueUpdateDto = new IssueUpdateDto();
        IssueUpdateDto.Fields fields = new IssueUpdateDto.Fields();
        issueUpdateDto.setFields(fields);
        fields.setIssueLinks(List.of(new IssueUpdateDto.IssueLink()));

        when(webClient.post()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(eq("/issueLink"))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any(IssueUpdateDto.IssueLink.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/issue/{key}", issueKey)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(eq(issueUpdateDto))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(eq(IssueDto.class))).thenReturn(Mono.just(new IssueDto()));

        jiraService.updateIssueByKey(issueKey, issueUpdateDto);

        verify(webClient).post();
        verify(requestBodyUriSpec).uri("/issue/{key}", issueKey);
        verify(requestBodySpec).bodyValue(any(IssueUpdateDto.IssueLink.class));
    }
}