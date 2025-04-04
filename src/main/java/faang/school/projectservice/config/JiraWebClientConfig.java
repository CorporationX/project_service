package faang.school.projectservice.config;

import faang.school.projectservice.exception.JiraClientException;
import faang.school.projectservice.util.JiraTokenGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JiraWebClientConfig {
    @Value("${services.jira.email}")
    private String jiraEmail;

    @Value("${services.jira.token}")
    private String jiraToken;

    @Value("${services.jira.endpoint}")
    private String jiraBaseUrl;

    @Bean
    public WebClient jiraWebClient() {
        return WebClient.builder()
                .baseUrl(jiraBaseUrl)
                .filter(addAuthorizationHeader())
                .filter(errorHandlingFilter())
                .build();
    }

    private ExchangeFilterFunction addAuthorizationHeader() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            String token = JiraTokenGenerator.generate(jiraEmail, jiraToken);
            return Mono.just(ClientRequest.from(clientRequest)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .build());
        });
    }

    private ExchangeFilterFunction errorHandlingFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().isError()) {
                return clientResponse
                        .bodyToMono(String.class)
                        .defaultIfEmpty("No error details")
                        .flatMap(errorBody -> Mono.error(
                                new JiraClientException(
                                        "Jira API error: " + errorBody,
                                        clientResponse.statusCode()
                                )
                        ));
            }
            return Mono.just(clientResponse);
        });
    }
}