package faang.school.projectservice.service.calendar;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.Events;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.EventDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.google.DateGoogleConverter;
import faang.school.projectservice.google.GoggleAuthorizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {
    private final GoggleAuthorizationService authorizationService;
    private final UserServiceClient userServiceClient;

    @Override
    public void addEventToCalendar(long eventId, String calendarId) throws GeneralSecurityException, IOException {
        EventDto eventDto = userServiceClient.getEvent(eventId);
        if (eventDto.getCalendarEventId() == null) {
            add(eventDto, calendarId);
        } else {
            log.info("event has already added to the calendar");
        }
    }

    private void add(EventDto eventDto, String calendarId) throws GeneralSecurityException, IOException {
        Calendar service = authorizationService.authorizeAndGetCalendar();
        Event event = new Event()
                .setStart(DateGoogleConverter.toEventDateTime(eventDto.getStartDate()))
                .setEnd(DateGoogleConverter.toEventDateTime(eventDto.getEndDate()))
                .setDescription(eventDto.getDescription())
                .setAttendees(eventDto.getAttendeeEmails().stream()
                        .map((email) -> new EventAttendee().setEmail(email))
                        .toList()
                )
                .setSummary(eventDto.getTitle())
                .setReminders(new Event.Reminders().setUseDefault(true));

        event = service.events().insert(calendarId, event).execute();
        log.info("Event created: {}", event.getHtmlLink());
    }

    private void checkIfCalendarAvailable(String calendarId, Calendar service) throws IOException {
        if (service.calendars().get(calendarId) == null) {
            throw new DataValidationException("calendar id = " + calendarId + " is not available");
        }
    }

    @Override
    public void update() {

    }

    @Override
    public void view() throws GeneralSecurityException, IOException {
        Calendar service = authorizationService.authorizeAndGetCalendar();
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        if (items.isEmpty()) {
            log.info("No upcoming events found.");
        } else {
            log.info("Upcoming events");
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                log.info("{} {}\n", event.getSummary(), start);
            }
        }
    }
}
