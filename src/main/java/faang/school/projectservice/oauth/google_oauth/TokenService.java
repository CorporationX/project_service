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
        log.info("Сохранение OAuth токена в базу данных");
        tokenRepository.save(token);
    }

    @Transactional(readOnly = true)
    public GoogleAuthToken getLatestToken() {
        log.info("Получение последнего OAuth токена из базы данных");
        return tokenRepository.findFirstByOrderByCreatedAtDesc();
    }

    @Transactional
    public void deleteToken() {
        log.info("Удаление последнего OAuth токена из базы данных");
        GoogleAuthToken token = getLatestToken();
        if (token != null) {
            tokenRepository.delete(token);
        }
    }

    @Transactional
    public void deleteAllTokens() {
        log.info("Удаление всех OAuth токенов из базы данных");
        tokenRepository.deleteAll();
    }
}