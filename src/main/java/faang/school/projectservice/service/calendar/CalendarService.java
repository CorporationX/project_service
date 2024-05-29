package faang.school.projectservice.service.calendar;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import faang.school.projectservice.model.CalendarToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final GoogleAuthorizationService OAuthService;
    @Value("${spring.OAuth2.applicationName}")
    private String applicationName;


    public Credential auth(long projectId, String code) {
        CalendarToken calendarToken = OAuthService.authorizeProject(projectId, code);
        return OAuthService.generateCredential(calendarToken);
    }

    public URL getAuthUrl() {
        return OAuthService.getAuthUrl();
    }

    public void getUpcomingEvents(long projectId) {
        Credential credential = auth(projectId, null);

        Calendar service =
                new Calendar.Builder(OAuthService.getHttp_transport(), OAuthService.getJsonFactory(), credential)
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
