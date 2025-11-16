package faang.school.projectservice.integration.jira.service;

import faang.school.projectservice.integration.jira.Oauth.model.UserJiraOauthToken;
import faang.school.projectservice.integration.jira.Oauth.model.JiraOauthTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JiraTokenService {

    private final JiraOauthTokenRepository tokenRepository;

    @Transactional
    public UserJiraOauthToken saveToken(
            Long userId,
            String accessToken,
            String refreshToken,
            String tokenType,
            LocalDateTime expiresAt,
            String scope
    ) {
        log.info("Saving Oauth token for user: {}", userId);

        Optional<UserJiraOauthToken> existing = tokenRepository.findByUserId(userId);

        UserJiraOauthToken token;

        if (existing.isPresent()) {
            log.debug("Updating existing tokens for user: {}", userId);
            token = existing.get();
            token.setAccessToken(accessToken);
            token.setRefreshToken(refreshToken);
            token.setTokenType(tokenType);
            token.setExpiresAt(expiresAt);
            token.setScope(scope);
        } else {
            log.debug("Creating new tokens for user: {}", userId);
            token = UserJiraOauthToken.builder()
                    .userId(userId)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .expiresAt(expiresAt)
                    .scope(scope)
                    .build();
        }

        token = tokenRepository.save(token);
        log.info("Token save for user: {}", userId);

        return token;
    }

    public Optional<UserJiraOauthToken> getToken(Long userId) {
        log.debug("Getting tokens for user: {}", userId);
        return tokenRepository.findByUserId(userId);
    }

    @Transactional
    public void deleteToken(Long userId) {
        log.info("Deleting token for user: {}", userId);
        tokenRepository.deleteByUserId(userId);
        log.info("Tokens deleted for user: {}", userId);
    }

    public boolean hasValidToken(Long userId) {
        Optional<UserJiraOauthToken> token = tokenRepository.findByUserId(userId);

        if (token.isEmpty()) {
            log.debug("No token found for user: {}", userId);
            return false;
        }

        boolean isExpired = token.get().isExpired();
        log.debug("User {} has token, expired: {}", userId, isExpired);

        return true;
    }

    @Transactional
    public UserJiraOauthToken updateTokenAfterRefresh(
            Long userId,
            String newAccessToken,
            String newRefreshToken,
            LocalDateTime newExpiresAt
    ) {
        log.info("Updating token after refresh for user: {}", userId);

        UserJiraOauthToken token = tokenRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException(
                        "No tokens found for user: " + userId
                ));

        token.setAccessToken(newAccessToken);

        if (newRefreshToken != null && !newRefreshToken.isBlank()) {
            token.setRefreshToken(newRefreshToken);
        }

        token.setExpiresAt(newExpiresAt);

        token = tokenRepository.save(token);
        log.info("Tokens updated for user: {}", userId);

        return token;
    }
}
