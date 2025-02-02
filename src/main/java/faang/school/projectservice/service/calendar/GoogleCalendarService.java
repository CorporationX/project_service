package faang.school.projectservice.service.calendar;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import faang.school.projectservice.dto.calendar.CalendarEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class GoogleCalendarService {
    private final AuthService oAuthService;

    public String createEvent(CalendarEventDto eventDto) throws Exception {
        Credential credential = oAuthService.getCredentials(String.valueOf(eventDto.getCreatorId()));
        Calendar newCalendar = getCalendar(credential);
        Date startDateTime = convertLocalDateToDate(eventDto.getStartsAt());
        Date endDateTime = convertLocalDateToDate(eventDto.getEndsAt());

        Event event = new Event()
                .setSummary(eventDto.getTitle())
                .setDescription(eventDto.getDescription())
                .setStart(new EventDateTime()
                        .setDateTime(new DateTime(startDateTime)))
                .setEnd(new EventDateTime()
                        .setDateTime(new DateTime(endDateTime)));

        Event createdEvent = newCalendar.events().insert(eventDto.getCalendarId(), event).execute();
        log.info("New event with id {} was added to calendar", createdEvent.getId());
        return createdEvent.getId();
    }

    public Event getEvent(String calendarId, String eventId, long creatorId) throws Exception {
        Credential credential = oAuthService.getCredentials(String.valueOf(creatorId));
        Calendar calendar = getCalendar(credential);
        return calendar.events().get(calendarId, eventId).execute();
    }

    public void deleteEvent(String calendarId, String eventId, long creatorId) throws Exception {
        Credential credential = oAuthService.getCredentials(String.valueOf(creatorId));
        Calendar calendar = getCalendar(credential);
        calendar.events().delete(calendarId, eventId).execute();
    }

    private Calendar getCalendar(Credential credential) throws GeneralSecurityException, IOException {
        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                credential)
                .setApplicationName("ProjectService")
                .build();
    }

    private Date convertLocalDateToDate(LocalDateTime time) {
        return Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
    }
}

