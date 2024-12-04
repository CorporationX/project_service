package faang.school.projectservice.service.jira;

import faang.school.projectservice.config.jira.JiraUriConfig;
import faang.school.projectservice.dto.jira.issue.IssueResponse;
import faang.school.projectservice.dto.jira.issue.create.JiraCreateIssueRequest;
import faang.school.projectservice.dto.jira.issue.enums.IssueStatus;
import faang.school.projectservice.dto.jira.issue.search.JiraSearchRequest;
import faang.school.projectservice.dto.jira.issue.search.JiraSearchResponse;
import faang.school.projectservice.dto.jira.issue.transition.TransitionNestedResponse;
import faang.school.projectservice.dto.jira.issue.transition.TransitionRequest;
import faang.school.projectservice.dto.jira.issue.transition.TransitionsResponse;
import faang.school.projectservice.dto.jira.issue.update.JiraUpdateIssueRequest;
import faang.school.projectservice.exceptions.jira.JiraBadRequestException;
import faang.school.projectservice.exceptions.jira.JiraNotFoundException;
import faang.school.projectservice.service.jira.builders.JiraCreateIssueBuilder;
import faang.school.projectservice.service.jira.builders.JiraSearchRequestBuilder;
import faang.school.projectservice.service.jira.builders.JiraUpdateIssueBuilder;
import faang.school.projectservice.service.jira.search.conditions.impl.ProjectCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JiraCloudReactiveClientTest {

    private static final String BODY_AS_STRING = "bodyAsString";
    private static final String BAD_REQUEST_MESSAGE = String.format("Jira bad request error: %s", BODY_AS_STRING);
    private static final String NOT_FOUND_MESSAGE = String.format("Jira not found error: %s", BODY_AS_STRING);

    @Mock
    private WebClient webClient;

    @Mock
    private JiraUriConfig uriConfig;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private JiraCloudReactiveClient jiraClient;

    private JiraCreateIssueRequest createIssueRequest;
    private JiraUpdateIssueRequest updateIssueRequest;
    private String issueKey;
    private IssueResponse expectedResponse;
    private IssueStatus newIssueStatus;
    private TransitionRequest transitionRequest;
    private JiraSearchRequest searchRequest;

    @BeforeEach
    void setUp() {
        createIssueRequest = new JiraCreateIssueBuilder()
                .setProjectKey("TEST")
                .setSummary("Test Issue")
                .setDescription("Test Description")
                .build();

        updateIssueRequest = new JiraUpdateIssueBuilder()
                .setSummary("test")
                .setDescription("test description")
                .build();

        issueKey = "TEST-1";

        expectedResponse = new IssueResponse();
        expectedResponse.setKey(issueKey);

        newIssueStatus = IssueStatus.DONE;
        transitionRequest = TransitionRequest.builder()
                .transition(new TransitionRequest.TransitionNested(newIssueStatus.getId()))
                .build();

        searchRequest = new JiraSearchRequestBuilder()
                .withDefaultFields()
                .addCondition(new ProjectCondition("TP"))
                .build();
    }

    @Test
    void testCreateIssue_Success() {
        when(uriConfig.getCreateIssueUri())
                .thenReturn("https://foodwise.atlassian.net/rest/api/3/issue");
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uriConfig.getCreateIssueUri())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(createIssueRequest)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(IssueResponse.class)).thenReturn(Mono.just(expectedResponse));

        Mono<IssueResponse> actualResponse = jiraClient.createIssue(createIssueRequest);

        StepVerifier.create(actualResponse)
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void testCreateIssue_BadRequest() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uriConfig.getCreateIssueUri())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(createIssueRequest)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(IssueResponse.class))
                .thenReturn(Mono.error(new JiraBadRequestException(BODY_AS_STRING)));

        Mono<IssueResponse> actualResponse = jiraClient.createIssue(createIssueRequest);

        StepVerifier.create(actualResponse)
                .expectErrorMatches(throwable -> throwable instanceof JiraBadRequestException &&
                        throwable.getMessage().equals(BAD_REQUEST_MESSAGE))
                .verify();
    }

    @Test
    void testCreateIssue_NotFound() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uriConfig.getCreateIssueUri())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(createIssueRequest)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(IssueResponse.class))
                .thenReturn(Mono.error(new JiraNotFoundException(BODY_AS_STRING)));

        Mono<IssueResponse> actualResponse = jiraClient.createIssue(createIssueRequest);

        StepVerifier.create(actualResponse)
                .expectErrorMatches(throwable -> throwable instanceof JiraNotFoundException &&
                        throwable.getMessage().equals(NOT_FOUND_MESSAGE))
                .verify();
    }

    @Test
    void testUpdateIssue_success() {
        when(uriConfig.getUpdateIssueUri())
                .thenReturn("https://foodwise.atlassian.net/rest/api/3/issue/%s");
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(String.format(uriConfig.getUpdateIssueUri(), issueKey))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(updateIssueRequest)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(IssueResponse.class)).thenReturn(Mono.just(expectedResponse));

        Mono<IssueResponse> actualResponse = jiraClient.updateIssue(issueKey, updateIssueRequest);

        StepVerifier.create(actualResponse)
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void testUpdateIssue_badRequest() {
        when(uriConfig.getUpdateIssueUri())
                .thenReturn("https://foodwise.atlassian.net/rest/api/3/issue/%s");
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(String.format(uriConfig.getUpdateIssueUri(), issueKey))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(updateIssueRequest)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(IssueResponse.class))
                .thenReturn(Mono.error(new JiraBadRequestException(BODY_AS_STRING)));

        Mono<IssueResponse> actualResponse = jiraClient.updateIssue(issueKey, updateIssueRequest);

        StepVerifier.create(actualResponse)
                .expectErrorMatches(throwable -> throwable instanceof JiraBadRequestException &&
                        throwable.getMessage().equals(BAD_REQUEST_MESSAGE))
                .verify();
    }

    @Test
    void testUpdateIssue_NotFound() {
        when(uriConfig.getUpdateIssueUri())
                .thenReturn("https://foodwise.atlassian.net/rest/api/3/issue/%s");
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(String.format(uriConfig.getUpdateIssueUri(), issueKey))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(updateIssueRequest)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(IssueResponse.class))
                .thenReturn(Mono.error(new JiraNotFoundException(BODY_AS_STRING)));

        Mono<IssueResponse> actualResponse = jiraClient.updateIssue(issueKey, updateIssueRequest);

        StepVerifier.create(actualResponse)
                .expectErrorMatches(throwable -> throwable instanceof JiraNotFoundException &&
                        throwable.getMessage().equals(NOT_FOUND_MESSAGE))
                .verify();
    }

    @Test
    void testTransitionIssue_success() {
        when(uriConfig.getTransitionIssueUri())
                .thenReturn("https://foodwise.atlassian.net/rest/api/3/issue/{issueKey}/transitions");
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(String.format(uriConfig.getTransitionIssueUri(), issueKey)))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(transitionRequest)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());

        Mono<Void> monoResponse = jiraClient.transitionIssue(issueKey, newIssueStatus);

        StepVerifier.create(monoResponse)
                .verifyComplete();
    }

    @Test
    void testTransitionIssue_badRequest() {
        when(uriConfig.getTransitionIssueUri())
                .thenReturn("https://foodwise.atlassian.net/rest/api/3/issue/{issueKey}/transitions");
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(String.format(uriConfig.getTransitionIssueUri(), issueKey)))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(transitionRequest)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.error(new JiraBadRequestException(BODY_AS_STRING)));

        Mono<Void> monoResponse = jiraClient.transitionIssue(issueKey, newIssueStatus);

        StepVerifier.create(monoResponse)
                .verifyErrorMatches(throwable -> throwable instanceof JiraBadRequestException &&
                        throwable.getMessage().equals(BAD_REQUEST_MESSAGE));
    }

    @Test
    void testTransitionIssue_notFound() {
        when(uriConfig.getTransitionIssueUri())
                .thenReturn("https://foodwise.atlassian.net/rest/api/3/issue/{issueKey}/transitions");
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(String.format(uriConfig.getTransitionIssueUri(), issueKey)))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(transitionRequest)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.error(new JiraNotFoundException(BODY_AS_STRING)));

        Mono<Void> monoResponse = jiraClient.transitionIssue(issueKey, newIssueStatus);

        StepVerifier.create(monoResponse)
                .verifyErrorMatches(throwable -> throwable instanceof JiraNotFoundException &&
                        throwable.getMessage().equals(NOT_FOUND_MESSAGE));
    }

    @Test
    void testGetAvailableTransitions_success() {
        TransitionNestedResponse responseNested1 = TransitionNestedResponse.builder()
                .id("1")
                .name("test1")
                .build();
        TransitionNestedResponse responseNested2 = TransitionNestedResponse.builder()
                .id("2")
                .name("test2")
                .build();
        List<TransitionNestedResponse> expectedNestedTransitions = List.of(responseNested1, responseNested2);
        TransitionsResponse expectedTransitionResponse =
                new TransitionsResponse("test", expectedNestedTransitions);

        when(uriConfig.getTransitionIssueUri())
                .thenReturn("https://foodwise.atlassian.net/rest/api/3/issue/{issueKey}/transitions");
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(String.format(uriConfig.getTransitionIssueUri(), issueKey)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(TransitionsResponse.class)).thenReturn(Mono.just(expectedTransitionResponse));

        Mono<List<TransitionNestedResponse>> actualTransitions = jiraClient.getAvailableTransitions(issueKey);

        StepVerifier.create(actualTransitions)
                .expectNext(expectedNestedTransitions)
                .verifyComplete();
    }

    @Test
    void testSearchIssuesByFilter() {
        IssueResponse expectedResponse1 = new IssueResponse();
        expectedResponse1.setKey("TP-1");
        IssueResponse expectedResponse2 = new IssueResponse();
        expectedResponse2.setKey("TP-1");

        List<IssueResponse> expectedIssues = List.of(expectedResponse1, expectedResponse2);
        JiraSearchResponse expectedResponse = new JiraSearchResponse();
        expectedResponse.setIssues(expectedIssues);

        when(uriConfig.getSearchUri())
                .thenReturn("https://foodwise.atlassian.net/rest/api/3/search");
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uriConfig.getSearchUri())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(searchRequest)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(JiraSearchResponse.class)).thenReturn(Mono.just(expectedResponse));

        Mono<List<IssueResponse>> actualIssuesResponses = jiraClient.searchIssuesByFilter(searchRequest);

        StepVerifier.create(actualIssuesResponses)
                .expectNext(expectedIssues)
                .verifyComplete();
    }
}

