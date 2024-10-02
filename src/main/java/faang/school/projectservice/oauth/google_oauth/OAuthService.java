package faang.school.projectservice.oauth.google_oauth;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import faang.school.projectservice.exceptions.google_calendar.exceptions.BadRequestException;
import faang.school.projectservice.exceptions.google_calendar.exceptions.GoogleCalendarException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuthService {
    private static final String ACCESS_TYPE_OFFLINE = "offline";
    private static final String APPROVAL_PROMPT_FORCE = "force";
    private static final String USER_KEY = "user";

    private final GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    public String buildAuthorizationUrl() {
        String state = UUID.randomUUID().toString();

        String authorizationUrl = googleAuthorizationCodeFlow.newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .setState(state)
                .setAccessType(ACCESS_TYPE_OFFLINE)
                .setApprovalPrompt(APPROVAL_PROMPT_FORCE)
                .build();

        log.info("Url authorization: {}", authorizationUrl);
        return authorizationUrl;
    }

    public String processOAuthCallback(String code, String error) {
        if (error != null && !error.isEmpty()) {
            throw new BadRequestException("Ошибка авторизации: " + error);
        }
        return handleOAuthCallback(code);
    }

    public String handleOAuthCallback(String code) {
        try {
            TokenResponse tokenResponse = googleAuthorizationCodeFlow.newTokenRequest(code)
                    .setRedirectUri(redirectUri)
                    .execute();

            googleAuthorizationCodeFlow.createAndStoreCredential(tokenResponse, USER_KEY);
            return "OAuth токены успешно получены и сохранены";
        } catch (IOException e) {
            throw new GoogleCalendarException("Ошибка обработки OAuth callback", e);
        }
    }
}