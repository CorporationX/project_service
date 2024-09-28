package faang.school.projectservice.service.jira.response;

import faang.school.projectservice.dto.jira.JiraDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;

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
                            Mono.error(new RuntimeException("Client error "
                                    + clientResponse.statusCode()
                                    + " "
                                    + errorBody)));
        } else if (clientResponse.statusCode().is5xxServerError()) {
            return clientResponse.bodyToMono(String.class)
                    .flatMap(errorBody ->
                            Mono.error(new RuntimeException("Server error "
                                    + clientResponse.statusCode()
                                    + " "
                                    + errorBody)));
        }
        return clientResponse.bodyToMono(String.class)
                .flatMap(errorBody ->
                        Mono.error(new RuntimeException("Unexpected error "
                                + clientResponse.statusCode()
                                + " "
                                + errorBody)));
    }

    public Mono<Object> handlerErrorConnection(Throwable e){
        if (e instanceof WebClientResponseException) {
            WebClientResponseException ex = (WebClientResponseException) e;
            return Mono.error(new RuntimeException("Request failed with status: "
                    + ex.getStatusCode()
                    + " and message: "
                    + ex.getResponseBodyAsString()));
        } else if (e instanceof IOException) {
            return Mono.error(new RuntimeException("Connection error: " + e.getMessage()));
        } else {
            return Mono.error(new RuntimeException("An unexpected error occurred: " + e.getMessage()));
        }
    }
}
