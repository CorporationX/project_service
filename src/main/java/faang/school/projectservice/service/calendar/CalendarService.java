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
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
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
public class CalendarService {
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
    @Value("${spring.OAuth2.applicationName}")
    private String applicationName;

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

    public Credential auth(long projectId, String code) {
        CalendarToken calendarToken = authorizeProject(projectId, code);

        var expiresInSeconds = ChronoUnit.SECONDS.between(calendarToken.getUpdatedAt(), now());

        Credential credential = getCredential(calendarToken.getAccessToken(),
                expiresInSeconds,
                calendarToken.getRefreshToken());

        refreshToken(calendarToken, credential);

        calendarTokenRepository.save(calendarToken);

        return credential;
    }

    private void refreshToken(CalendarToken calendarToken, Credential credential) {
        if (credential.getExpiresInSeconds() < ACCESS_TOKEN_EXPIRES_IN_SECONDS) {
            try {
                credential.refreshToken();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }

            calendarToken.setAccessToken(credential.getAccessToken());
            calendarToken.setRefreshToken(credential.getRefreshToken());
        }
    }

    private CalendarToken authorizeProject(long projectId, String code) {
        return calendarTokenRepository.findByProjectId(projectId)
                .orElseGet(() -> {
                    AuthorizationCodeTokenRequest tokenRequest = flow.newTokenRequest(code);
                    tokenRequest.setRedirectUri(redirectUri);

                    TokenResponse tokenResponse;

                    try {
                        tokenResponse = tokenRequest.execute();
                    } catch (IOException tokenRequestFailed) {
                        throw new RuntimeException("Token request failed");
                    }

                    CalendarToken calendarToken = CalendarToken.builder()
                            .project(projectRepository.getProjectById(projectId))
                            .accessToken(tokenResponse.getAccessToken())
                            .refreshToken(tokenResponse.getRefreshToken())
                            .build();

                    return (calendarTokenRepository.save(calendarToken));
                });
    }

    private Credential getCredential(String accessToken, Long expiresInSeconds, String refreshToken) {
        Credential credentials = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                .setJsonFactory(JSON_FACTORY)
                .setTransport(http_transport)
                .setClientAuthentication(flow.getClientAuthentication())
                .setTokenServerEncodedUrl(flow.getTokenServerEncodedUrl())
                .build();
        return credentials
                .setAccessToken(accessToken)
                .setExpiresInSeconds(expiresInSeconds)
                .setRefreshToken(refreshToken);
    }

    public URL getAuthorizationUrl() {
        return flow.newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .toURL();
    }

    public void getUpcomingEvents(long projectId) {
        CalendarToken calendarToken = calendarTokenRepository.findByProjectId(projectId)
                .orElseThrow(() -> new RuntimeException("There is no registered access tokens for this project."));

        var expiresInSeconds = ChronoUnit.SECONDS.between(calendarToken.getUpdatedAt(), now());

        Credential credential = getCredential(calendarToken.getAccessToken(),
                expiresInSeconds,
                calendarToken.getRefreshToken());

        refreshToken(calendarToken, credential);
        calendarTokenRepository.save(calendarToken);


        Calendar service =
                new Calendar.Builder(http_transport, JSON_FACTORY, credential)
                        .setApplicationName(applicationName)
                        .build();

        DateTime now = new DateTime(System.currentTimeMillis());
        Events events;
        try {
            events = service.events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        List<Event> items = events.getItems();

        if (items.isEmpty()) {
            System.out.println("No upcoming events found.");
        } else {
            System.out.println("Upcoming events");
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                System.out.printf("%s (%s)\n", event.getSummary(), start);
            }
        }
    }
}
