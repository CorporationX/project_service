package faang.school.projectservice.integration.jira.service;

import faang.school.projectservice.integration.jira.config.JiraProperties;
import faang.school.projectservice.integration.jira.dto.response.OauthTokenResponse;
import faang.school.projectservice.integration.jira.exception.JiraOauthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class JiraOauthService {

    private final JiraProperties jiraProperties;
    private final JiraTokenService jiraTokenService;
    private final WebClient.Builder webClientBuilder;

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generate random state for CSRF protection
     */
    public String generateState() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String state = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        log.debug("Generated state: {}", state);
        return state;
    }

    public boolean validateState(String receivedState, String sessionState) {
        if (receivedState == null || sessionState == null) {
            log.warn("State validation failed: null values");
            return false;
        }

        boolean valid = receivedState.equals(sessionState);

        if (!valid) {
            log.warn("State mismatch. Expected: {}, Got: {}", sessionState, receivedState);
        }

        return valid;
    }

    public String generateAuthorizationUrl(String state) {
        JiraProperties.OauthConfig Oauth = jiraProperties.getOauth();

        String url = String.format(
                "%s?client_id=%s&redirect_uri=%s&scope=%s&response_type=code&state=%s&prompt=consent",
                Oauth.getAuthorizationUri(),
                Oauth.getClientId(),
                URLEncoder.encode(Oauth.getRedirectUri(), StandardCharsets.UTF_8),
                URLEncoder.encode(Oauth.getScope(), StandardCharsets.UTF_8),
                state
        );

        log.debug("Authorization URL: {}", url);
        return url;
    }

    public void exchangeCodeForToken(String code, Long userId) {
        log.info("Exchanging authorization code for tokens, user: {}", userId);

        JiraProperties.OauthConfig Oauth = jiraProperties.getOauth();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("code", code);
        formData.add("redirect_uri", Oauth.getRedirectUri());
        formData.add("client_id", Oauth.getClientId());
        formData.add("client_secret", Oauth.getClientSecret());

        try {
            WebClient webClient = webClientBuilder
                    .baseUrl(Oauth.getTokenUri())
                    .build();

            OauthTokenResponse tokenResponse = webClient.post()
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError(),
                            response -> response.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("Oauth token exchange failed (4xx): {}", errorBody);
                                        return Mono.<Throwable>error(new JiraOauthException(
                                                "Token exchange failed: " + errorBody
                                        ));
                                    })
                    )
                    .onStatus(
                            status -> status.is5xxServerError(),
                            response -> {
                                log.error("Oauth token endpoint error (5xx)");
                                return Mono.error(new JiraOauthException(
                                        "Jira Oauth server error"
                                ));
                            }
                    )
                    .bodyToMono(OauthTokenResponse.class)
                    .block();

            if (tokenResponse == null) {
                throw new JiraOauthException("No response from token endpoint");
            }

            log.info("Successfully received tokens for user: {}", userId);
            log.debug("Token type: {}, Expires in: {} seconds",
                    tokenResponse.getTokenType(),
                    tokenResponse.getExpiresIn());

            LocalDateTime expiresAt = LocalDateTime.now()
                    .plusSeconds(tokenResponse.getExpiresIn());

            jiraTokenService.saveToken(
                    userId,
                    tokenResponse.getAccessToken(),
                    tokenResponse.getRefreshToken(),
                    tokenResponse.getTokenType(),
                    expiresAt,
                    tokenResponse.getScope()
            );

            log.info("Tokens saved for user: {}", userId);

        } catch (JiraOauthException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during token exchange", e);
            throw new JiraOauthException("Token exchange failed", e);
        }

    }

    public OauthTokenResponse refreshAccessToken(String refreshToken) {
        log.info("Refreshing access token");

        JiraProperties.OauthConfig Oauth = jiraProperties.getOauth();

        // Prepare request body
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("refresh_token", refreshToken);
        formData.add("client_id", Oauth.getClientId());
        formData.add("client_secret", Oauth.getClientSecret());

        try {
            WebClient webClient = webClientBuilder
                    .baseUrl(Oauth.getTokenUri())
                    .build();

            OauthTokenResponse tokenResponse = webClient.post()
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .onStatus(
                            status -> status.value() == 400,
                            response -> response.bodyToMono(String.class)
                                    .flatMap(error -> {
                                        log.error("Token refresh failed (invalid_grant): {}", error);
                                        return Mono.error(new JiraOauthException(
                                                "invalid_grant",
                                                "Refresh token is invalid or expired"
                                        ));
                                    })
                    )
                    .onStatus(
                            status -> status.is4xxClientError(),
                            response -> response.bodyToMono(String.class)
                                    .flatMap(error -> {
                                        log.error("Token refresh failed (4xx): {}", error);
                                        return Mono.<Throwable>error(new JiraOauthException(
                                                "Token refresh failed: " + error
                                        ));
                                    })
                    )
                    .bodyToMono(OauthTokenResponse.class)
                    .block();

            if (tokenResponse == null) {
                throw new JiraOauthException("No response from token endpoint");
            }

            log.info("Access token refreshed successfully");
            return tokenResponse;

        } catch (JiraOauthException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during token refresh", e);
            throw new JiraOauthException("Token refresh failed", e);
        }
    }
}
