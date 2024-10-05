package faang.school.projectservice.service.jira.response;

import faang.school.projectservice.dto.jira.JiraDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.JiraException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class JiraResponse {
    public Mono<Object> handler(ClientResponse clientResponse, boolean returnDto) {
        if (clientResponse.statusCode().is2xxSuccessful()) {
            if (returnDto) {
                return clientResponse.bodyToMono(JiraDto.class);
            } else {
                return Mono.just("Success: " + clientResponse.statusCode());
            }
        }
        return handlerError(clientResponse);
    }

    private Mono<Object> handlerError(ClientResponse clientResponse) {
        if (clientResponse.statusCode().is4xxClientError()) {
            return clientResponse.bodyToMono(String.class)
                    .flatMap(errorBody ->
                            Mono.error(new JiraException("Client error "
                                    + errorBody, clientResponse.statusCode())));
        } else if (clientResponse.statusCode().is5xxServerError()) {
            return clientResponse.bodyToMono(String.class)
                    .flatMap(errorBody ->
                            Mono.error(new JiraException("Server error "
                                    + errorBody, clientResponse.statusCode())));
        }
        return clientResponse.bodyToMono(String.class)
                .flatMap(errorBody ->
                        Mono.error(new JiraException("Unexpected error "
                                + errorBody, clientResponse.statusCode())));
    }
}
