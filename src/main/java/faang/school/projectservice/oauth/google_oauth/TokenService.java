package faang.school.projectservice.oauth.google_oauth;

import faang.school.projectservice.model.GoogleAuthToken;
import faang.school.projectservice.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenService {
    private final TokenRepository tokenRepository;

    @Transactional
    public void saveToken(GoogleAuthToken token) {
        tokenRepository.save(token);
        log.info("Token saved");
    }

    @Transactional(readOnly = true)
    public GoogleAuthToken getLatestToken() {
        return tokenRepository.findFirstByOrderByCreatedAtDesc();
    }

    @Transactional
    public void deleteToken() {
        GoogleAuthToken token = getLatestToken();
        if (token != null) {
            tokenRepository.delete(token);
        }
    }

    @Transactional
    public void deleteAllTokens() {
        tokenRepository.deleteAll();
    }
}