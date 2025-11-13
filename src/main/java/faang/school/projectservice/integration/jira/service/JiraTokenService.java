package faang.school.projectservice.integration.jira.service;

import faang.school.projectservice.integration.jira.oauth.model.UserJiraOAuthToken;
import faang.school.projectservice.integration.jira.oauth.model.JiraOAuthTokenRepository;
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

    private final JiraOAuthTokenRepository tokenRepository;

    @Transactional
    public UserJiraOAuthToken saveToken(
            Long userId,
            String accessToken,
            String refreshToken,
            String tokenType,
            LocalDateTime expiresAt,
            String scope
    ) {
        log.info("Saving OAuth token for user: {}", userId);

        Optional<UserJiraOAuthToken> existing = tokenRepository.findByUserId(userId);

        UserJiraOAuthToken token;

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
            token = UserJiraOAuthToken.builder()
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

    public Optional<UserJiraOAuthToken> getToken(Long userId){
        log.debug("Getting tokens for user: {}", userId);
        return tokenRepository.findByUserId(userId);
    }

    @Transactional
    public void deleteToken(Long userId) {
        log.info("Deleting token for user: {}", userId);
        tokenRepository.deleteByUserId(userId);
        log.info("Tokens deleted for user: {}", userId);
    }

    public boolean hasValidToken(Long userId){
        Optional<UserJiraOAuthToken> token = tokenRepository.findByUserId(userId);

        if (token.isEmpty()) {
            log.debug("No token found for user: {}", userId);
            return false;
        }

        boolean isExpired = token.get().isExpired();
        log.debug("User {} has token, expired: {}", userId, isExpired);

        return true;
    }

    @Transactional
    public UserJiraOAuthToken updateTokenAfterRefresh(
            Long userId,
            String newAccessToken,
            String newRefreshToken,
            LocalDateTime newExpiresAt
    ) {
        log.info("Updating token after refresh for user: {}", userId);

        UserJiraOAuthToken token = tokenRepository.findByUserId(userId)
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
