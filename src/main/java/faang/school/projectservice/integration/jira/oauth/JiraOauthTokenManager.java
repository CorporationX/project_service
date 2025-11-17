package faang.school.projectservice.integration.jira.oauth;

import faang.school.projectservice.integration.jira.dto.response.OauthTokenResponse;
import faang.school.projectservice.integration.jira.exception.JiraOauthException;
import faang.school.projectservice.integration.jira.oauth.model.UserJiraOauthToken;
import faang.school.projectservice.integration.jira.service.JiraOauthService;
import faang.school.projectservice.integration.jira.service.JiraTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class JiraOauthTokenManager {

    private final JiraTokenService jiraTokenService;
    private final JiraOauthService jiraOauthService;

    public String getValidToken(Long userId) {
        log.debug("Getting valid token for user: {}", userId);

        UserJiraOauthToken token = jiraTokenService.getToken(userId)
                .orElseThrow(() -> new JiraOauthException(
                        "User "
                                + userId
                                + " is not connected to Jira. "
                                + "Please authorize first."
                ));

        if (token.isExpired()) {
            log.info("Access token expired for user: {}, refreshing...", userId);
            return refreshAndGetToken(userId, token);
        }

        log.debug("Token is valid for user: {}", userId);
        return token.getAccessToken();
    }

    private String refreshAndGetToken(Long userId, UserJiraOauthToken oldToken) {
        try {
            log.info("Refreshing token for user: {}", userId);

            OauthTokenResponse newTokens = jiraOauthService.refreshAccessToken(
                    oldToken.getRefreshToken()
            );

            LocalDateTime newExpiresAt = LocalDateTime.now()
                    .plusSeconds(newTokens.getExpiresIn());

            UserJiraOauthToken updatedToken = jiraTokenService.updateTokenAfterRefresh(
                    userId,
                    newTokens.getAccessToken(),
                    newTokens.getRefreshToken(), // может быть null
                    newExpiresAt
            );

            log.info("Token refreshed successfully for user: {}", userId);

            return updatedToken.getAccessToken();

        } catch (JiraOauthException e) {
            if ("invalid_grant".equals(e.getErrorCode())) {
                log.error("Refresh token invalid for user: {}. Re-authorization required.", userId);

                jiraTokenService.deleteToken(userId);

                throw new JiraOauthException(
                        "Oauth tokens expired. Please re-authorize with Jira."
                );
            }
            throw e;
        }
    }

    public boolean isTokenExpired(Long userId) {
        return jiraTokenService.getToken(userId)
                .map(UserJiraOauthToken::isExpired)
                .orElse(true);
    }


    public String getAuthorizationHeader(Long userId) {
        String accessToken = getValidToken(userId);
        return "Bearer " + accessToken;
    }
}

