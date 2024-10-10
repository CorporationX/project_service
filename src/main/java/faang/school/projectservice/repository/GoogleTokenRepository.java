package faang.school.projectservice.repository;

import faang.school.projectservice.model.entity.GoogleToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoogleTokenRepository extends JpaRepository<GoogleToken, Long> {
    Optional<GoogleToken> findByUserId(Long userId);
    Optional<GoogleToken> findByAccessToken(String accessToken);
}
