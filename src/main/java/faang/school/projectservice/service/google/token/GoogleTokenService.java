package faang.school.projectservice.service.google.token;

import faang.school.projectservice.model.GoogleToken;
import faang.school.projectservice.repository.GoogleTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GoogleTokenService {

    private final GoogleTokenRepository googleTokenRepository;

    public Optional<GoogleToken> getTokenByUserId(Long userId) {
        return googleTokenRepository.findByUserId(userId);
    }

    public void saveOrUpdateToken(GoogleToken googleToken) {
        googleTokenRepository.save(googleToken);
    }

    public void deleteTokenByUserId(Long userId) {
        googleTokenRepository.deleteById(userId);
    }

    public void deleteAllTokens() {
        googleTokenRepository.deleteAll();
    }

    public long countTokens() {
        return googleTokenRepository.count();
    }

    public Optional<GoogleToken> findByToken(String accessToken) {
        return googleTokenRepository.findByAccessToken(accessToken);
    }

}
