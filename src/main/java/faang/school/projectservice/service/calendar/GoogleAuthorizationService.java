package faang.school.projectservice.service.calendar;

import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.CalendarScopes;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.calendar.CalendarToken;
import faang.school.projectservice.repository.calendar.CalendarTokenRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static java.time.LocalDateTime.now;

@Slf4j
@Service
@RequiredArgsConstructor
class GoogleAuthorizationService {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = List.of(CalendarScopes.CALENDAR_EVENTS, CalendarScopes.CALENDAR);
    private final CalendarTokenRepository calendarTokenRepository;
    private final Environment env;
    private GoogleAuthorizationCodeFlow flow;
    @Getter
    private NetHttpTransport http_transport;

    @PostConstruct
    public void setUp() {
        try {
            http_transport = GoogleNetHttpTransport.newTrustedTransport();
            flow = new GoogleAuthorizationCodeFlow.Builder(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance(),
                    Objects.requireNonNull(env.getProperty("google.clientId")),
                    env.getProperty("google.clientSecret"), SCOPES)
                    .setAccessType(env.getProperty("google.accessType"))
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Credential generateCredential(CalendarToken calendarToken) {
        var expiresInSeconds = ChronoUnit.SECONDS.between(calendarToken.getUpdatedAt(), now());

        Credential credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                .setJsonFactory(JSON_FACTORY)
                .setTransport(http_transport)
                .setClientAuthentication(flow.getClientAuthentication())
                .setTokenServerEncodedUrl(flow.getTokenServerEncodedUrl())
                .build()
                .setAccessToken(calendarToken.getAccessToken())
                .setExpiresInSeconds(expiresInSeconds)
                .setRefreshToken(calendarToken.getRefreshToken());

        refreshToken(calendarToken, credential);
        return credential;
    }

    public void refreshToken(CalendarToken calendarToken, Credential credential) {
        if (credential.getExpiresInSeconds() < Long.parseLong(Objects.requireNonNull(env.getProperty("google.accessTokenExpiresInSeconds")))) {
            try {
                credential.refreshToken();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }

            calendarToken.setAccessToken(credential.getAccessToken());
            calendarToken.setRefreshToken(credential.getRefreshToken());

            calendarTokenRepository.save(calendarToken);
        }
    }

    public CalendarToken authorizeProject(Project project, String code) {
        return calendarTokenRepository.findByProjectId(project.getId())
                .orElseGet(() -> {
                    TokenResponse tokenResponse = requestToken(code);
                    log.info("TokenResponse requested token");
                    CalendarToken calendarToken = CalendarToken.builder()
                            .project(project)
                            .accessToken(tokenResponse.getAccessToken())
                            .refreshToken(tokenResponse.getRefreshToken())
                            .build();
                    log.info("Prepare to save calendar token");
                    return (calendarTokenRepository.save(calendarToken));
                });
    }

    @Retryable(retryFor = TokenResponseException.class)
    public TokenResponse requestToken(String code) {
        AuthorizationCodeTokenRequest tokenRequest = flow.newTokenRequest(code);
        log.info("Flow new token request");
        tokenRequest.setRedirectUri(env.getProperty("google.redirectUri"));
        log.info("Set Redirect Uri");
        try {
            return tokenRequest.execute();
        } catch (IOException tokenRequestFailed) {
            throw new RuntimeException("The token parsing failed.");
        }
    }

    public URL getAuthUrl() {
        return flow.newAuthorizationUrl()
                .setRedirectUri(env.getProperty("google.redirectUri"))
                .toURL();
    }

    public JsonFactory getJsonFactory() {
        return JSON_FACTORY;
    }
}
