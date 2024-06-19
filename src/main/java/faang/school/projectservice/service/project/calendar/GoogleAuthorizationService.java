package faang.school.projectservice.service.project.calendar;

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
import faang.school.projectservice.model.CalendarToken;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.CalendarTokenRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
class GoogleAuthorizationService {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = List.of(CalendarScopes.CALENDAR_EVENTS, CalendarScopes.CALENDAR);
    @Value("${spring.OAuth2.accessTokenExpiresInSeconds}")
    private int accessTokenExpiresInSeconds;
    private final CalendarTokenRepository calendarTokenRepository;
    @Value("${spring.OAuth2.accessType}")
    public String accessType;
    private GoogleAuthorizationCodeFlow flow;
    private NetHttpTransport http_transport;
    @Value("${spring.OAuth2.redirectUri}")
    private String redirectUri;
    @Value("${spring.OAuth2.clientSecret}")
    private String clientSecret;
    @Value("${spring.OAuth2.clientId}")
    private String clientId;

    @PostConstruct
    public void setUp() {
        try {
            http_transport = GoogleNetHttpTransport.newTrustedTransport();
            flow = new GoogleAuthorizationCodeFlow.Builder(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance(), clientId, clientSecret, SCOPES)
                    .setAccessType(accessType).build();
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
        if (credential.getExpiresInSeconds() < accessTokenExpiresInSeconds) {
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
                    CalendarToken calendarToken = CalendarToken.builder()
                            .project(project)
                            .accessToken(tokenResponse.getAccessToken())
                            .refreshToken(tokenResponse.getRefreshToken())
                            .build();

                    return (calendarTokenRepository.save(calendarToken));
                });
    }

    @Retryable(retryFor = TokenResponseException.class)
    public TokenResponse requestToken(String code) {
        AuthorizationCodeTokenRequest tokenRequest = flow.newTokenRequest(code);
        tokenRequest.setRedirectUri(redirectUri);

        try {
            return tokenRequest.execute();
        } catch (IOException tokenRequestFailed) {
            throw new RuntimeException("The token parsing failed.");
        }
    }

    public URL getAuthUrl() {
        return flow.newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .toURL();
    }

    public NetHttpTransport getHttp_transport() {
        return http_transport;
    }

    public JsonFactory getJsonFactory() {
        return JSON_FACTORY;
    }
}
