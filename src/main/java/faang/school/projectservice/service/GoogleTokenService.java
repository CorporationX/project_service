package faang.school.projectservice.service;

import faang.school.projectservice.model.entity.GoogleToken;

import java.util.Optional;

public interface GoogleTokenService {

    Optional<GoogleToken> getTokenByUserId(Long userId);

    void saveOrUpdateToken(GoogleToken googleToken);

    void deleteTokenByUserId(Long userId);

    void deleteAllTokens();

    long countTokens();

    Optional<GoogleToken> findByToken(String accessToken);
}
