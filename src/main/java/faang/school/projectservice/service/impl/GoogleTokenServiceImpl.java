package faang.school.projectservice.service.impl;

import faang.school.projectservice.model.entity.GoogleToken;
import faang.school.projectservice.repository.GoogleTokenRepository;
import faang.school.projectservice.service.GoogleTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GoogleTokenServiceImpl implements GoogleTokenService {

    private final GoogleTokenRepository googleTokenRepository;

    @Override
    public Optional<GoogleToken> getTokenByUserId(Long userId) {
        return googleTokenRepository.findByUserId(userId);
    }

    @Override
    public void saveOrUpdateToken(GoogleToken googleToken) {
        googleTokenRepository.save(googleToken);
    }

    @Override
    public void deleteTokenByUserId(Long userId) {
        googleTokenRepository.deleteById(userId);
    }

    @Override
    public void deleteAllTokens() {
        googleTokenRepository.deleteAll();
    }

    @Override
    public long countTokens() {
        return googleTokenRepository.count();
    }

    @Override
    public Optional<GoogleToken> findByToken(String accessToken) {
        return googleTokenRepository.findByAccessToken(accessToken);
    }

}
