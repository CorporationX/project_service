package faang.school.projectservice.service.jira;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.config.jira.WebClientConfig;
import faang.school.projectservice.model.dto.jira.JiraDto;
import faang.school.projectservice.model.dto.jira.JiraStatusDto;
import faang.school.projectservice.filter.jira.JiraFilter;
import faang.school.projectservice.model.dto.client.UserDto;
import faang.school.projectservice.service.jira.response.JiraResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JiraServiceImplTest {

    @Mock
    private WebClientConfig webClientConfig;
    @Mock
    private JiraResponse jiraResponse;
    @Mock
    private List<JiraFilter> jiraFilter;
    @Mock
    private UserContext userContext;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;
    @Mock
    private WebClient.RequestBodySpec requestBodySpec;
    @InjectMocks
    private JiraServiceImpl jiraService;

    private UserDto userDto;
    private JiraDto jiraDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setToken("test-token");
        userDto.setProjectUrl("https://jira.example.com");

        jiraDto = new JiraDto();
        jiraDto.setKey("TEST-123");
    }

    @Test
    void getIssueString() {
        when(userContext.getUserId()).thenReturn(1L);
        when(userServiceClient.getUser(1L)).thenReturn(userDto);

        when(webClientConfig.jiraWebClient(any(), any(), any()))
                .thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(eq("/rest/api/3/issue/{issueKey}"), eq("TEST-123")))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(any())).thenReturn(Mono.just("Success"));

        Mono<Object> result = jiraService.getIssue("TEST-123");

        result.subscribe(response -> assertEquals("Success", response));
    }

    @Test
    void createTask() {
        when(userContext.getUserId()).thenReturn(1L);
        when(userServiceClient.getUser(1L)).thenReturn(userDto);

        when(webClientConfig.jiraWebClient(any(), any(), any()))
                .thenReturn(webClient);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(eq("/rest/api/2/issue"))).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(eq(MediaType.APPLICATION_JSON))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(eq(jiraDto))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(any())).thenReturn(Mono.just(jiraDto));

        Mono<Object> result = jiraService.createTask(jiraDto);

        result.subscribe(response -> assertEquals(jiraDto, response));

    }

    @Test
    void updateTask() {
        when(userContext.getUserId()).thenReturn(1L);
        when(userServiceClient.getUser(1L)).thenReturn(userDto);

        when(webClientConfig.jiraWebClient(any(), any(), any()))
                .thenReturn(webClient);
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(eq("/rest/api/2/issue/{issueKey}"), eq("TEST-123")))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(eq(MediaType.APPLICATION_JSON))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(eq(jiraDto))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(any())).thenReturn(Mono.just(jiraDto));

        Mono<Object> result = jiraService.updateTask(jiraDto);

        result.subscribe(response -> assertEquals(jiraDto, response));
    }

    @Test
    void updateTaskLink() {
        when(userContext.getUserId()).thenReturn(1L);
        when(userServiceClient.getUser(1L)).thenReturn(userDto);

        when(webClientConfig.jiraWebClient(any(), any(), any()))
                .thenReturn(webClient);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(eq("/rest/api/3/issueLink")))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(eq(MediaType.APPLICATION_JSON))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(eq(jiraDto))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(any())).thenReturn(Mono.just(jiraDto));

        Mono<Object> result = jiraService.updateTaskLink(jiraDto);

        result.subscribe(response -> assertEquals(jiraDto, response));

    }

    @Test
    void changeTaskStatus() {
        JiraStatusDto jiraStatusDto = new JiraStatusDto();
        jiraStatusDto.setId("31");
        jiraStatusDto.setName("In Progress");
        jiraDto.setTransitions(List.of(jiraStatusDto));
        jiraDto.setStatus("In Progress");

        Map<String, Object> body = Map.of(
                "transition", Map.of("id", "31")
        );

        when(userContext.getUserId()).thenReturn(1L);
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(webClientConfig.jiraWebClient(any(), any(), any()))
                .thenReturn(webClient);

        doReturn(requestHeadersUriSpec).when(webClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString(), anyString());
        doReturn(Mono.just(jiraDto)).when(responseSpec).bodyToMono(JiraDto.class);

        doReturn(requestBodyUriSpec).when(webClient).post();
        doReturn(requestBodySpec).when(requestBodyUriSpec).uri(anyString(), anyString());
        doReturn(requestHeadersSpec).when(requestBodySpec).bodyValue(body);
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        doReturn(Mono.empty()).when(responseSpec).bodyToMono(Void.class);

        String result = jiraService.changeTaskStatus(jiraDto).block();

        assertEquals("Status changed to: " + jiraDto.getStatus(), result);
    }

    @Test
    void getAllTasks() {
        when(userContext.getUserId()).thenReturn(1L);
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(webClientConfig.jiraWebClient(any(), any(), any()))
                .thenReturn(webClient);

        when(webClientConfig.jiraWebClient(any(), any(), any()))
                .thenReturn(webClient);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(any())).thenReturn(Mono.just("Success"));

        Mono<Object> result = jiraService.getAllTasks("Test");

        result.subscribe(response -> assertEquals("Success", response));
    }

    @Test
    void getTaskByFilters() {

        when(userContext.getUserId()).thenReturn(1L);
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(webClientConfig.jiraWebClient(any(), any(), any()))
                .thenReturn(webClient);

        when(webClientConfig.jiraWebClient(any(), any(), any()))
                .thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        JiraDto expectedJiraDto = new JiraDto();
        when(responseSpec.bodyToMono(JiraDto.class)).thenReturn(Mono.just(expectedJiraDto));


        JiraDto result = jiraService.getTaskByFilters(jiraDto).block();

        assertEquals(expectedJiraDto, result);
    }
}