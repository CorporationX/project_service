package projectservice.jira;

import faang.school.projectservice.client.JiraClient;
import faang.school.projectservice.dto.jira.Fields;
import faang.school.projectservice.dto.jira.request.IssueRequestDto;
import faang.school.projectservice.dto.jira.request.ProjectRequestDto;
import faang.school.projectservice.dto.jira.response.IssueCreateResponseDto;
import faang.school.projectservice.dto.jira.response.IssueResponseDto;
import faang.school.projectservice.dto.jira.response.IssuesResponseDto;
import faang.school.projectservice.dto.jira.update.InwardIssue;
import faang.school.projectservice.dto.jira.update.IssueLinkDto;
import faang.school.projectservice.dto.jira.update.IssueLinkType;
import faang.school.projectservice.dto.jira.update.IssueUpdateDto;
import faang.school.projectservice.dto.jira.update.OutwardIssue;
import faang.school.projectservice.dto.jira.update.TransitionDto;
import faang.school.projectservice.exception.JiraClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JiraClientTest {
    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);

    @InjectMocks
    private JiraClient jiraWebClient;

    private IssueRequestDto issueRequestDto;
    private IssueCreateResponseDto createResponseDto;
    private IssueResponseDto responseDto;
    private final String issueKey = "IssueKey";
    private final List<IssueLinkDto> links = new ArrayList<>();
    private final TransitionDto transition = new TransitionDto("id");
    private IssueUpdateDto issueUpdateDto;

    @BeforeEach
    public void setUp() {
        issueRequestDto = new IssueRequestDto(new Fields());
        issueRequestDto.getFields().setSummary("summary");
        issueRequestDto.getFields().setDescription("description");
        issueRequestDto.getFields().setProject(new ProjectRequestDto("key"));

        createResponseDto = new IssueCreateResponseDto("1234", "JIRA-KEY");
        responseDto = new IssueResponseDto(new Fields());

        links.add(new IssueLinkDto(new IssueLinkType("type1"), new OutwardIssue("outward1"),
                new InwardIssue("inward1")));
        links.add(new IssueLinkDto(new IssueLinkType("type2"), new OutwardIssue("outward2"),
                new InwardIssue("inward2")));

        issueUpdateDto = new IssueUpdateDto(new IssueUpdateDto.Fields(), null);
    }

    @Test
    public void testCreateIssue_success() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/issue")).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(issueRequestDto))
                .thenAnswer(inv -> requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(IssueCreateResponseDto.class))
                .thenReturn(Mono.just(createResponseDto));

        IssueCreateResponseDto response = jiraWebClient.createIssue(issueRequestDto);

        assertNotNull(response);
        verify(webClient, times(1)).post();
        verify(requestBodyUriSpec, times(1)).uri("/issue");
        verify(requestBodySpec, times(1)).bodyValue(issueRequestDto);
        verify(requestBodySpec, times(1)).retrieve();
        verify(responseSpec, times(1)).bodyToMono(IssueCreateResponseDto.class);
    }

    public void testCreateIssue_withError(HttpStatusCode status) {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/issue")).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(issueRequestDto))
                .thenAnswer(inv -> requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(IssueCreateResponseDto.class))
                .thenReturn(Mono.error(new JiraClientException("Jira API error", status)));

        JiraClientException exception = assertThrows(JiraClientException.class,
                () -> jiraWebClient.createIssue(issueRequestDto)
        );

        assertEquals(status, exception.getStatusCode());
        verify(webClient, times(1)).post();
        verify(requestBodyUriSpec, times(1)).uri("/issue");
        verify(requestBodySpec, times(1)).bodyValue(issueRequestDto);
        verify(requestBodySpec, times(1)).retrieve();
        verify(responseSpec, times(1)).bodyToMono(IssueCreateResponseDto.class);
    }

    @Test
    public void testCreateIssue_400() {
        testCreateIssue_withError(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testCreateIssue_401() {
        testCreateIssue_withError(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void testCreateIssue_403() {
        testCreateIssue_withError(HttpStatus.FORBIDDEN);
    }

    @Test
    public void testCreateIssue_422() {
        testCreateIssue_withError(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    public void testGetIssueByKey_success() {
        when(webClient.get()).thenAnswer(inv -> requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/issue/{key}", issueKey))
                .thenAnswer(inv -> requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(IssueResponseDto.class))
                .thenReturn(Mono.just(responseDto));

        IssueResponseDto response = jiraWebClient.getIssueByKey(issueKey);

        assertNotNull(response);
        verify(webClient, times(1)).get();
        verify(requestHeadersUriSpec, times(1)).uri("/issue/{key}", issueKey);
        verify(requestHeadersSpec, times(1)).retrieve();
        verify(responseSpec, times(1)).onStatus(any(), any());
        verify(responseSpec, times(1)).bodyToMono(IssueResponseDto.class);
    }

    @Test
    public void testGetIssueByKey_notFound() {
        when(webClient.get()).thenAnswer(inv -> requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/issue/{key}", issueKey))
                .thenAnswer(inv -> requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(IssueResponseDto.class))
                .thenReturn(Mono.error(new IllegalArgumentException("Issue with key " + issueKey + " not found")));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> jiraWebClient.getIssueByKey(issueKey)
        );

        assertEquals("Issue with key " + issueKey + " not found", exception.getMessage());
        verify(webClient, times(1)).get();
    }

    @Test
    public void testGetIssueByKey_unauthorized() {
        when(webClient.get()).thenAnswer(inv -> requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/issue/{key}", issueKey))
                .thenAnswer(inv -> requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(IssueResponseDto.class))
                .thenReturn(Mono.error(new JiraClientException("Jira API error", HttpStatus.UNAUTHORIZED)));

        JiraClientException exception = assertThrows(JiraClientException.class,
                () -> jiraWebClient.getIssueByKey(issueKey)
        );

        verify(webClient, times(1)).get();
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        verify(requestHeadersUriSpec, times(1)).uri("/issue/{key}", issueKey);
        verify(requestHeadersSpec, times(1)).retrieve();
        verify(responseSpec, times(1)).onStatus(any(), any());
        verify(responseSpec, times(1)).bodyToMono(IssueResponseDto.class);
    }

    @Test
    public void testCreateIssueLinks_success() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/issueLink")).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(links.get(0)))
                .thenAnswer(inv -> requestBodySpec);
        when(requestBodySpec.bodyValue(links.get(1)))
                .thenAnswer(inv -> requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        jiraWebClient.createIssueLinks(links);

        verify(webClient, times(2)).post();
        verify(requestBodyUriSpec, times(2)).uri("/issueLink");
        verify(requestBodySpec, times(1)).bodyValue(links.get(0));
        verify(requestBodySpec, times(1)).bodyValue(links.get(1));
        verify(requestBodySpec, times(2)).retrieve();
        verify(responseSpec, times(2)).toBodilessEntity();
    }

    public void testCreateIssueLinks_withError(HttpStatusCode status) {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/issueLink")).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(links.get(0)))
                .thenAnswer(inv -> requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity())
                .thenReturn(Mono.error(new JiraClientException("Jira API error", status)));

        JiraClientException exception = assertThrows(JiraClientException.class,
                () -> jiraWebClient.createIssueLinks(links)
        );

        assertEquals(status, exception.getStatusCode());
        verify(webClient, times(1)).post();
        verify(requestBodyUriSpec, times(1)).uri("/issueLink");
        verify(requestBodySpec, times(1)).bodyValue(links.get(0));
        verify(requestBodySpec, times(1)).retrieve();
        verify(responseSpec, times(1)).toBodilessEntity();
    }

    @Test
    public void testCreateIssueLinks_400() {
        testCreateIssueLinks_withError(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testCreateIssueLinks_401() {
        testCreateIssueLinks_withError(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void testCreateIssueLinks_404() {
        testCreateIssueLinks_withError(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testCreateIssueLinks_413() {
        testCreateIssueLinks_withError(HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @Test
    public void testSetTransitionByKey_success() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/issue/{issueKey}/transitions", issueKey))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(Map.of("transition", transition)))
                .thenAnswer(inv -> requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        jiraWebClient.setTransitionByKey(issueKey, transition);

        verify(webClient, times(1)).post();
        verify(requestBodyUriSpec, times(1))
                .uri("/issue/{issueKey}/transitions", issueKey);
        verify(requestBodySpec, times(1)).bodyValue(Map.of("transition", transition));
        verify(requestBodySpec, times(1)).retrieve();
        verify(responseSpec, times(1)).toBodilessEntity();
    }

    public void testSetTransitionByKey_withError(HttpStatusCode status) {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/issue/{issueKey}/transitions", issueKey))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(Map.of("transition", transition)))
                .thenAnswer(inv -> requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity())
                .thenReturn(Mono.error(new JiraClientException("Jira API error", status)));

        JiraClientException exception = assertThrows(JiraClientException.class,
                () -> jiraWebClient.setTransitionByKey(issueKey, transition)
        );

        assertEquals(status, exception.getStatusCode());
        verify(webClient, times(1)).post();
        verify(requestBodyUriSpec, times(1))
                .uri("/issue/{issueKey}/transitions", issueKey);
        verify(requestBodySpec, times(1)).bodyValue(Map.of("transition", transition));
        verify(requestBodySpec, times(1)).retrieve();
        verify(responseSpec, times(1)).toBodilessEntity();
    }

    @Test
    public void testSetTransitionByKey_400() {
        testSetTransitionByKey_withError(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testSetTransitionByKey_401() {
        testSetTransitionByKey_withError(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void testSetTransitionByKey_404() {
        testSetTransitionByKey_withError(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testSetTransitionByKey_409() {
        testSetTransitionByKey_withError(HttpStatus.CONFLICT);
    }

    @Test
    public void testSetTransitionByKey_413() {
        testSetTransitionByKey_withError(HttpStatus.CONFLICT);
    }

    @Test
    public void testUpdateIssueByKey_success() {
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/issue/{key}", issueKey))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(issueUpdateDto))
                .thenAnswer(inv -> requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        jiraWebClient.updateIssueByKey(issueKey, issueUpdateDto);

        verify(webClient, times(1)).put();
        verify(requestBodyUriSpec, times(1))
                .uri("/issue/{key}", issueKey);
        verify(requestBodySpec, times(1)).bodyValue(issueUpdateDto);
        verify(requestBodySpec, times(1)).retrieve();
        verify(responseSpec, times(1)).toBodilessEntity();
    }

    public void testUpdateIssueByKey_withError(HttpStatusCode status) {
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/issue/{key}", issueKey))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(issueUpdateDto))
                .thenAnswer(inv -> requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity())
                .thenReturn(Mono.error(new JiraClientException("Jira API error", status)));

        JiraClientException exception = assertThrows(JiraClientException.class,
                () -> jiraWebClient.updateIssueByKey(issueKey, issueUpdateDto)
        );

        assertEquals(status, exception.getStatusCode());
        verify(webClient, times(1)).put();
        verify(requestBodyUriSpec, times(1))
                .uri("/issue/{key}", issueKey);
        verify(requestBodySpec, times(1)).bodyValue(issueUpdateDto);
        verify(requestBodySpec, times(1)).retrieve();
        verify(responseSpec, times(1)).toBodilessEntity();
    }

    @Test
    public void testUpdateIssueByKey_400() {
        testUpdateIssueByKey_withError(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testUpdateIssueByKey_401() {
        testUpdateIssueByKey_withError(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void testUpdateIssueByKey_403() {
        testUpdateIssueByKey_withError(HttpStatus.FORBIDDEN);
    }

    @Test
    public void testUpdateIssueByKey_404() {
        testUpdateIssueByKey_withError(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testUpdateIssueByKey_409() {
        testUpdateIssueByKey_withError(HttpStatus.CONFLICT);
    }

    @Test
    public void testUpdateIssueByKey_422() {
        testUpdateIssueByKey_withError(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetInfoByJql_success() {
        String jql = "project = JIR";
        when(webClient.get()).thenAnswer(inv -> requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class)))
                .thenAnswer(inv -> requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(IssuesResponseDto.class))
                .thenReturn(Mono.just(new IssuesResponseDto()));

        IssuesResponseDto response = jiraWebClient.getInfoByJql(jql);

        assertNotNull(response);
        verify(webClient, times(1)).get();
        verify(requestHeadersUriSpec, times(1)).uri(any(Function.class));
        verify(requestHeadersSpec, times(1)).retrieve();
        verify(responseSpec, times(1)).bodyToMono(IssuesResponseDto.class);
    }

    @SuppressWarnings("unchecked")
    public void testGetInfoByJql_withError(HttpStatusCode status) {
        String jql = "project = JIR";
        when(webClient.get()).thenAnswer(inv -> requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class)))
                .thenAnswer(inv -> requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(IssuesResponseDto.class))
                .thenReturn(Mono.error(new JiraClientException("Jira API error", status)));

        JiraClientException exception = assertThrows(JiraClientException.class,
                () -> jiraWebClient.getInfoByJql(jql)
        );

        assertEquals(status, exception.getStatusCode());
        verify(webClient, times(1)).get();
        verify(requestHeadersUriSpec, times(1)).uri(any(Function.class));
        verify(requestHeadersSpec, times(1)).retrieve();
        verify(responseSpec, times(1)).bodyToMono(IssuesResponseDto.class);
    }

    @Test
    public void testGetInfoByJql_400() {
        testGetInfoByJql_withError(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testGetInfoByJql_401() {
        testGetInfoByJql_withError(HttpStatus.UNAUTHORIZED);
    }
}
