package faang.school.projectservice.service.calendar;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RequiredArgsConstructor
@Service
public class GoogleCalendarService {
    private final AuthService oAuthService;

    public void createEvent(String summary, String description, String startDateTime, String endDateTime)
            throws Exception {
        Credential credential = oAuthService.getCredentials("user@mail.net");
        Calendar newCalendar = getCalendar(credential);

        Event event = new Event()
                .setSummary(summary)
                .setDescription(description)
                .setStart(new EventDateTime()
                        .setDateTime(new DateTime(startDateTime))
                        .setTimeZone("UTC"))
                .setEnd(new EventDateTime()
                        .setDateTime(new DateTime(endDateTime))
                        .setTimeZone("UTC"));

        String calendarId = "primary";
        newCalendar.events().insert(calendarId, event).execute();
//        calendarService.events().delete()
    }

    private Calendar getCalendar(Credential credential) throws GeneralSecurityException, IOException {
        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                credential)
                .setApplicationName("ProjectService")
                .build();
    }
}

