package faang.school.projectservice.integration.jira.oauth;

import faang.school.projectservice.integration.jira.dto.response.OAuthTokenResponse;
import faang.school.projectservice.integration.jira.exception.JiraOAuthException;
import faang.school.projectservice.integration.jira.oauth.model.UserJiraOAuthToken;
import faang.school.projectservice.integration.jira.service.JiraOAuthService;
import faang.school.projectservice.integration.jira.service.JiraTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class JiraOAuthTokenManager {

    private final JiraTokenService jiraTokenService;
    private final JiraOAuthService jiraOAuthService;

    public String getValidToken(Long userId) {
        log.debug("Getting valid token for user: {}", userId);

        UserJiraOAuthToken token = jiraTokenService.getToken(userId)
                .orElseThrow(() -> new JiraOAuthException(
                        "User " + userId + " is not connected to Jira. " +
                                "Please authorize first."
                ));

        if (token.isExpired()) {
            log.info("Access token expired for user: {}, refreshing...", userId);
            return refreshAndGetToken(userId, token);
        }

        log.debug("Token is valid for user: {}", userId);
        return token.getAccessToken();
    }

    private String refreshAndGetToken(Long userId, UserJiraOAuthToken oldToken) {
        try {
            log.info("Refreshing token for user: {}", userId);

            OAuthTokenResponse newTokens = jiraOAuthService.refreshAccessToken(
                    oldToken.getRefreshToken()
            );

            LocalDateTime newExpiresAt = LocalDateTime.now()
                    .plusSeconds(newTokens.getExpiresIn());

            UserJiraOAuthToken updatedToken = jiraTokenService.updateTokenAfterRefresh(
                    userId,
                    newTokens.getAccessToken(),
                    newTokens.getRefreshToken(), // может быть null
                    newExpiresAt
            );

            log.info("Token refreshed successfully for user: {}", userId);

            return updatedToken.getAccessToken();

        } catch (JiraOAuthException e) {
            if ("invalid_grant".equals(e.getErrorCode())) {
                log.error("Refresh token invalid for user: {}. Re-authorization required.", userId);

                jiraTokenService.deleteToken(userId);

                throw new JiraOAuthException(
                        "OAuth tokens expired. Please re-authorize with Jira."
                );
            }
            throw e;
        }
    }

    public boolean isTokenExpired(Long userId) {
        return jiraTokenService.getToken(userId)
                .map(UserJiraOAuthToken::isExpired)
                .orElse(true);
    }


    public String getAuthorizationHeader(Long userId) {
        String accessToken = getValidToken(userId);
        return "Bearer " + accessToken;
    }
}

