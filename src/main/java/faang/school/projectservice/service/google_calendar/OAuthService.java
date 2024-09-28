package faang.school.projectservice.service.google_calendar;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import faang.school.projectservice.exceptions.google_calendar.exceptions.BadRequestException;
import faang.school.projectservice.exceptions.google_calendar.exceptions.GoogleCalendarException;
import faang.school.projectservice.model.GoogleAuthToken;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuthService {
    private final TokenService tokenService;
    private GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow;

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    @PostConstruct
    private void initializeGoogleAuthorizationCodeFlow() {
        try {
            GoogleClientSecrets clientSecrets = new GoogleClientSecrets()
                    .setWeb(new GoogleClientSecrets.Details()
                            .setClientId(clientId)
                            .setClientSecret(clientSecret));

            this.googleAuthorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    clientSecrets,
                    Arrays.asList("https://www.googleapis.com/auth/calendar", "https://www.googleapis.com/auth/calendar.events"))
                    .setAccessType("offline")
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            log.error("Ошибка инициализации GoogleAuthorizationCodeFlow", e);
            throw new GoogleCalendarException("Ошибка инициализации Google OAuth Flow", e);
        }
    }

    public String buildAuthorizationUrl() {
        String state = UUID.randomUUID().toString();

        String authorizationUrl = googleAuthorizationCodeFlow.newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .setState(state)
                .setAccessType("offline")
                .setApprovalPrompt("force")
                .build();

        log.info("Сформирован URL для авторизации: {}", authorizationUrl);
        return authorizationUrl;
    }

    public String processOAuthCallback(String code, String error) {
        if (error != null && !error.isEmpty()) {
            log.error("Ошибка авторизации: {}", error);
            throw new BadRequestException("Ошибка авторизации: " + error);
        }

        return handleOAuthCallback(code);
    }

    public String handleOAuthCallback(String code) {
        log.info("Обработка OAuth callback с кодом: {}", code);
        try {
            TokenResponse tokenResponse = googleAuthorizationCodeFlow.newTokenRequest(code)
                    .setRedirectUri(redirectUri)
                    .execute();

            Credential credential = googleAuthorizationCodeFlow.createAndStoreCredential(tokenResponse, "user");

            GoogleAuthToken token = new GoogleAuthToken();
            token.setAccessToken(credential.getAccessToken());
            token.setRefreshToken(credential.getRefreshToken());
            token.setExpiresIn(credential.getExpiresInSeconds());
            token.setScope(tokenResponse.getScope());
            token.setTokenType(tokenResponse.getTokenType());
            token.setCreatedAt(LocalDateTime.now());

            tokenService.saveToken(token);

            log.info("OAuth токены успешно получены и сохранены");

            return "Авторизация успешна. Токены сохранены.";
        } catch (IOException e) {
            log.error("Ошибка при обработке OAuth callback", e);
            throw new GoogleCalendarException("Ошибка обработки OAuth callback", e);
        }
    }
}