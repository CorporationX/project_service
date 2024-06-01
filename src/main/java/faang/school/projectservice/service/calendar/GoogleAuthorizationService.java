package faang.school.projectservice.service.calendar;

import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.CalendarScopes;
import faang.school.projectservice.model.CalendarToken;
import faang.school.projectservice.repository.CalendarTokenRepository;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
class GoogleAuthorizationService {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);
    private static final int ACCESS_TOKEN_EXPIRES_IN_SECONDS = 3499;
    private final CalendarTokenRepository calendarTokenRepository;
    private final ProjectRepository projectRepository;
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
        if (credential.getExpiresInSeconds() < ACCESS_TOKEN_EXPIRES_IN_SECONDS) {
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

    public CalendarToken authorizeProject(long projectId, String code) {
        return calendarTokenRepository.findByProjectId(projectId)
                .orElseGet(() -> {
                    if (code == null) {
                        throw new RuntimeException("Authorization code is required to connect your project with CalendarAPI.");
                    }

                    TokenResponse tokenResponse = requestToken(code);

                    CalendarToken calendarToken = CalendarToken.builder()
                            .project(projectRepository.getProjectById(projectId))
                            .accessToken(tokenResponse.getAccessToken())
                            .refreshToken(tokenResponse.getRefreshToken())
                            .build();

                    return (calendarTokenRepository.save(calendarToken));
                });
    }

    public TokenResponse requestToken(String code) {
        AuthorizationCodeTokenRequest tokenRequest = flow.newTokenRequest(code);
        tokenRequest.setRedirectUri(redirectUri);

        TokenResponse tokenResponse;

        try {
            tokenResponse = tokenRequest.execute();
        } catch (IOException tokenRequestFailed) {
            throw new RuntimeException("Token request failed");
        }
        return tokenResponse;
    }

    public URL getAuthUrl() {
        return flow.newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .toURL();
    }

    public GoogleAuthorizationCodeFlow getFlow() {
        return flow;
    }

    public NetHttpTransport getHttp_transport() {
        return http_transport;
    }

    public JsonFactory getJsonFactory() {
        return JSON_FACTORY;
    }
}
