package faang.school.projectservice.controller.calendar;

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
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;


@RestController
public class CalendarController {
    public static final String ACCESS_TYPE = "offline";
    private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);
    private GoogleAuthorizationCodeFlow flow;
    private NetHttpTransport http_transport;
    @Value("${spring.OAuth2.clientSecret}")
    private String clientSecret;
    @Value("${spring.OAuth2.clientId}")
    private String clientId;
    @Value("${spring.OAuth2.applicationName}")
    private String applicationName;
    private Credential credential;
    private TokenResponse tokenResponse;


    @PostConstruct
    public void setUp() {
        try {
            http_transport = GoogleNetHttpTransport.newTrustedTransport();
            flow = new GoogleAuthorizationCodeFlow.Builder(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance(), clientId, clientSecret, SCOPES)
                    .setAccessType(ACCESS_TYPE).build();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/authorization")
    public URL getAuthorizationUrl() {
        return flow.newAuthorizationUrl()
                .setRedirectUri(REDIRECT_URI)
                .toURL();
    }

    @PostMapping("/authorization")
    public void setCredentials(@RequestParam String code) {
        credential = auth(code);
    }


    @GetMapping("/events")
    public void getUpcomingEvents() throws IOException {
        System.out.println("ExpiresInSeconds = " + credential.getExpiresInSeconds());
        if (credential.getExpiresInSeconds() <= 0) {
            credential.refreshToken();
        }

        Calendar service =
                new Calendar.Builder(http_transport, JSON_FACTORY, credential)
                        .setApplicationName(applicationName)
                        .build();

        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
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

    private Credential auth(String code) {
        AuthorizationCodeTokenRequest tokenRequest = flow.newTokenRequest(code);

        tokenRequest.setRedirectUri(REDIRECT_URI);
        try {
            tokenResponse = tokenRequest.execute();
            System.out.println("Token response:");
            System.out.println(tokenResponse);

            return new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                    .setJsonFactory(JSON_FACTORY)
                    .setTransport(http_transport)
                    .setClientAuthentication(tokenRequest.getClientAuthentication())
                    .setTokenServerUrl(tokenRequest.getTokenServerUrl())
                    .build()
                    .setFromTokenResponse(tokenResponse);
        } catch (IOException tokenRequestFailed) {
            throw new RuntimeException("Token request failed");
        }
    }
}
