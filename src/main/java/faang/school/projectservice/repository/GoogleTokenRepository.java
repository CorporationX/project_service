package faang.school.projectservice.repository;

import faang.school.projectservice.model.google.GoogleToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoogleTokenRepository extends JpaRepository<GoogleToken, Integer> {
    Boolean existsGoogleTokenByUserId(long userId);
    Optional<GoogleToken> findByOauthClientId(String oauthClientId);
}