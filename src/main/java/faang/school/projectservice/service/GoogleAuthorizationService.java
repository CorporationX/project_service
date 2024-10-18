package faang.school.projectservice.service;

import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ExpiredTokenException;
import faang.school.projectservice.model.GoogleCalendarToken;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.GoogleCalendarTokenRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Service
@Getter
@RequiredArgsConstructor
public class GoogleAuthorizationService {

    private final GoogleCalendarTokenRepository calendarTokenRepository;
    private final Environment env;
    private final NetHttpTransport httpTransport;
    private final GoogleAuthorizationCodeFlow flow;
    private final JsonFactory jsonFactory;

    public Credential generateCredential(GoogleCalendarToken calendarToken) {
        var expiresInSeconds = ChronoUnit.SECONDS.between(calendarToken.getUpdatedAt(), LocalDateTime.now());

        Credential credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                .setJsonFactory(jsonFactory)
                .setTransport(httpTransport)
                .setClientAuthentication(flow.getClientAuthentication())
                .setTokenServerEncodedUrl(flow.getTokenServerEncodedUrl())
                .build()
                .setAccessToken(calendarToken.getAccessToken())
                .setRefreshToken(calendarToken.getRefreshToken())
                .setExpiresInSeconds(expiresInSeconds);

        refreshToken(calendarToken, credential);
        return credential;
    }

    public void refreshToken(GoogleCalendarToken calendarToken, Credential credential) {
        long expiresInSeconds = credential.getExpiresInSeconds();
        long configuredExpirationTime = Long.parseLong(
                Objects.requireNonNull(env.getProperty("google.accessTokenExpiresInSeconds"))
        );

        if (expiresInSeconds < configuredExpirationTime) {
            try {
                credential.refreshToken();
            } catch (IOException e) {
                throw new RuntimeException("Failed to refresh token: " + e.getMessage(), e);
            }

            calendarToken.setAccessToken(credential.getAccessToken());
            calendarToken.setRefreshToken(credential.getRefreshToken());
            calendarTokenRepository.save(calendarToken);
        } else {
            throw new ExpiredTokenException("Token time expired");
        }
    }

    public GoogleCalendarToken authorizeProject(Project project, String code) {
        return calendarTokenRepository.findByProjectId(project.getId())
                .orElseGet(() -> createAndSaveCalendarToken(project, code));
    }

    @Retryable(include = TokenResponseException.class)
    public TokenResponse requestToken(String code) {
        AuthorizationCodeTokenRequest tokenRequest = flow.newTokenRequest(code);
        tokenRequest.setRedirectUri(env.getProperty("google.redirectUri"));
        try {
            return tokenRequest.execute();
        } catch (IOException tokenRequestFailed) {
            throw new RuntimeException("Token request failed: " + tokenRequestFailed.getMessage(), tokenRequestFailed);
        }
    }

    public URL getAuthUrl() {
        return flow.newAuthorizationUrl()
                .setRedirectUri(env.getProperty("google.redirectUri"))
                .setAccessType("offline")
                .set("prompt", "consent")
                .toURL();
    }

    private GoogleCalendarToken createAndSaveCalendarToken(Project project, String code) {
        TokenResponse tokenResponse = requestToken(code);
        validateRefreshToken(tokenResponse);

        GoogleCalendarToken calendarToken = buildCalendarToken(project, tokenResponse);
        return calendarTokenRepository.save(calendarToken);
    }

    private void validateRefreshToken(TokenResponse tokenResponse) {
        if (tokenResponse.getRefreshToken() == null || tokenResponse.getRefreshToken().isEmpty()) {
            throw new DataValidationException(
                    "No refresh token. Refresh tokens are only generated when you grant access to the application."
            );
        }
    }

    private GoogleCalendarToken buildCalendarToken(Project project, TokenResponse tokenResponse) {
        return GoogleCalendarToken.builder()
                .project(project)
                .accessToken(tokenResponse.getAccessToken())
                .refreshToken(tokenResponse.getRefreshToken())
                .build();
    }
}
