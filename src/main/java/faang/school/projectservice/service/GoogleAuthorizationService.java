package faang.school.projectservice.service;

import com.google.api.client.auth.oauth2.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.CalendarScopes;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ExpiredTokenException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.GoogleCalendarToken;
import faang.school.projectservice.repository.GoogleCalendarTokenRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;

import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static java.time.LocalTime.now;

@Service
@RequiredArgsConstructor
public class GoogleAuthorizationService {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = List.of(CalendarScopes.CALENDAR_EVENTS, CalendarScopes.CALENDAR);
    private final GoogleCalendarTokenRepository calendarTokenRepository;
    private final Environment env;

    @Getter
    private NetHttpTransport http_transport;

    private GoogleAuthorizationCodeFlow flow;

    @PostConstruct
    public void setUp() {
        try {
            http_transport = GoogleNetHttpTransport.newTrustedTransport();
            flow = new GoogleAuthorizationCodeFlow.Builder(
                    http_transport, JSON_FACTORY,
                    Objects.requireNonNull(env.getProperty("google.clientId")),
                    Objects.requireNonNull(env.getProperty("google.clientSecret")), SCOPES)
                    .setAccessType(env.getProperty("google.accessType"))
                    .setAccessType("offline")
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Failed to set up Google Authorization Service: " + e.getMessage(), e);
        }
    }

    public Credential generateCredential(GoogleCalendarToken calendarToken) {
        var expiresInSeconds = ChronoUnit.SECONDS.between(calendarToken.getUpdatedAt(), LocalDateTime.now());

        Credential credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                .setJsonFactory(JSON_FACTORY)
                .setTransport(http_transport)
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
        if (credential.getExpiresInSeconds() < Long.parseLong(Objects.requireNonNull(env.getProperty("google.accessTokenExpiresInSeconds")))) {
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
                .orElseGet(() -> {
                    TokenResponse tokenResponse = requestToken(code);
                    if (tokenResponse.getRefreshToken() == null || tokenResponse.getRefreshToken().isEmpty()) {
                        throw new DataValidationException("No refresh token. Refresh tokens are only generated when you grant access to the application.");
                    }

                    GoogleCalendarToken calendarToken = GoogleCalendarToken.builder()
                            .project(project)
                            .accessToken(tokenResponse.getAccessToken())
                            .refreshToken(tokenResponse.getRefreshToken())
                            .build();
                    return calendarTokenRepository.save(calendarToken);
                });
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


    public JsonFactory getJsonFactory() {
        return JSON_FACTORY;
    }
}
