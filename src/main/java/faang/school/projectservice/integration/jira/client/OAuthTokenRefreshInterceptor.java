package faang.school.projectservice.integration.jira.client;

import faang.school.projectservice.integration.jira.oauth.JiraOAuthTokenManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

/**
 * Interceptor для автоматического refresh OAuth токена при 401
 * можно добавить в WebClient filter chain
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthTokenRefreshInterceptor implements ExchangeFilterFunction {

    private final JiraOAuthTokenManager tokenManager;

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        return next.exchange(request)
                .flatMap(response -> {
                    if (response.statusCode() == HttpStatus.UNAUTHORIZED) {
                        log.warn("Received 401 Unauthorized. Token might be expired.");

                        // Извлекаем userId из контекста (нужно передавать отдельно)
                        // Long userId = getUserIdFromContext(request);

                        // Обновляем токен
                        // String newToken = tokenManager.getValidToken(userId);

                        // Повторяем запрос с новым токеном
                        // ClientRequest retryRequest = ClientRequest.from(request)
                        //     .header("Authorization", "Bearer " + newToken)
                        //     .build();

                        // return next.exchange(retryRequest);
                    }

                    return Mono.just(response);
                });
    }
}