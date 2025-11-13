package faang.school.projectservice.integration.jira.oauth.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JiraOAuthTokenRepository extends JpaRepository<UserJiraOAuthToken, Long> {

    Optional<UserJiraOAuthToken> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    void deleteByUserId(Long userId);

    @Query("SELECT t FROM UserJiraOAuthToken t WHERE t.expiresAt < :now")
    List<UserJiraOAuthToken> findExpiredTokens(@Param("now") LocalDateTime now);

    @Query("SELECT t FROM UserJiraOAuthToken t WHERE t.expiresAt BETWEEN :now AND :threshold")
    List<UserJiraOAuthToken> findTokensExpiringBefore(
            @Param("now") LocalDateTime now,
            @Param("threshold") LocalDateTime threshold
    );

    @Query("SELECT COUNT(t) FROM UserJiraOAuthToken t WHERE t.expiresAt > :now")
    long countActiveTokens(@Param("now") LocalDateTime now);
}
