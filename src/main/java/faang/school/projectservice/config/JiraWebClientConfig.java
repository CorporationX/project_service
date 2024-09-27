package faang.school.projectservice.config;

import faang.school.projectservice.config.context.JiraContext;
import faang.school.projectservice.dto.ErrorResponse;
import faang.school.projectservice.exception.JiraException;
import faang.school.projectservice.util.TokenGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JiraWebClientConfig {

    private final JiraContext jiraContext;
    private final TokenGenerator jiraTokenGenerator;

    @Value("${services.jira.base-url}")
    private String jiraBaseUrl;

    @Bean
    public WebClient jiraWebClient() {
        return WebClient.builder()
                .baseUrl(jiraBaseUrl + "/rest/api/2/")
                .filter(addAuthorizationHeader())
                .filter(errorHandlingFilter())
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024))
                .build();
    }

    private ExchangeFilterFunction addAuthorizationHeader() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            String token = jiraTokenGenerator.generate(Map.of(
                    "email", jiraContext.getEmail(),
                    "token", jiraContext.getToken())
            );
            return Mono.just(ClientRequest.from(clientRequest)
                    .header("Authorization", token)
                    .build());
        });
    }

    private ExchangeFilterFunction errorHandlingFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().isError()) {
                return clientResponse
                        .bodyToMono(ErrorResponse.class)
                        .flatMap(error -> Mono.error(
                                new JiraException(error, clientResponse.statusCode().value())
                        ));
            }
            return Mono.just(clientResponse);
        });
    }
}
