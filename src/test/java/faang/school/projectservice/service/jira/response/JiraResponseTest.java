package faang.school.projectservice.service.jira.response;

import faang.school.projectservice.model.dto.jira.JiraDto;
import faang.school.projectservice.exception.JiraException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class JiraResponseTest {
    @Mock
    private ClientResponse clientResponse;

    private JiraResponse jiraResponse;

    @BeforeEach
    void setUp() {
        jiraResponse = new JiraResponse();
    }

    @Test
    @DisplayName("Проверка успешного ответа с возвратом dto")
    void handlerWithSuccessAndReturnDtoTrue() {
        JiraResponse jiraResponse = new JiraResponse();
        JiraDto expectedDto = new JiraDto();

        Mockito.when(clientResponse.statusCode()).thenReturn(HttpStatus.OK);
        Mockito.when(clientResponse.bodyToMono(JiraDto.class))
                .thenReturn(Mono.just(expectedDto));

        Mono<Object> result = jiraResponse.handler(clientResponse, true);

        result.subscribe(response -> assertEquals(expectedDto, response));
    }

    @Test
    @DisplayName("Проверка успешного ответа с возвратом строки")
    void handlerWithSuccessAndReturnDtoFalse() {
        Mockito.when(clientResponse.statusCode()).thenReturn(HttpStatus.OK);

        Mono<Object> result = jiraResponse.handler(clientResponse, false);

        result.subscribe(response -> assertEquals("Success: 200 OK", response));
    }

    @Test
    @DisplayName("Проверка ошибки 400 типа")
    void handlerWithClientError() {
        JiraResponse jiraResponse = new JiraResponse();
        ClientResponse clientResponse = Mockito.mock(ClientResponse.class);

        Mockito.when(clientResponse.statusCode()).thenReturn(org.springframework.http.HttpStatus.BAD_REQUEST);
        Mockito.when(clientResponse.bodyToMono(String.class)).thenReturn(Mono.just("Client-side error"));

        Mono<Object> result = jiraResponse.handler(clientResponse, true);

        result.subscribe(response -> assertThrows(JiraException.class, () -> jiraResponse.handler(clientResponse, true)));
        result.subscribe(response -> assertEquals("Client-side error", response));
    }

    @Test
    @DisplayName("Проверка ошибки 500 типа")
    void handlerWithServerError() {
        JiraResponse jiraResponse = new JiraResponse();
        ClientResponse clientResponse = Mockito.mock(ClientResponse.class);

        Mockito.when(clientResponse.statusCode()).thenReturn(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
        Mockito.when(clientResponse.bodyToMono(String.class)).thenReturn(Mono.just("Server-side error"));

        Mono<Object> result = jiraResponse.handler(clientResponse, true);

        result.subscribe(response -> assertThrows(JiraException.class, () -> jiraResponse.handler(clientResponse, true)));
        result.subscribe(response -> assertEquals("Server-side error", response));
    }
}