package faang.school.projectservice.service.calendar;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.Events;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.EventDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.google.AuthorizationServiceImpl;
import faang.school.projectservice.util.DateGoogleConverter;
import faang.school.projectservice.validator.CalendarServiceImplValidator;
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
    private final AuthorizationServiceImpl authorizationService;
    private final UserServiceClient userServiceClient;
    private final CalendarServiceImplValidator validator;

    @Override
    public void addEventToCalendar(long eventId, String calendarId) throws GeneralSecurityException, IOException {
        EventDto eventDto = userServiceClient.getEvent(eventId);
        if (eventDto.getCalendarEventId() == null) {
            Event event = add(eventDto, calendarId);
            userServiceClient.setCalendarEventId(eventId, event.getId());
            log.info("Event created: {}", event.getHtmlLink());
        } else {
            log.info("event has already added to the calendar");
        }
    }

    @Override
    public Event updateEvent(EventDto eventDto, String calendarId) throws GeneralSecurityException, IOException {
        validator.validateUpdate(eventDto);
        Event event = add(eventDto, calendarId);
        log.info("Event updated: {}", event.getHtmlLink());
        return event;
    }

    @Override
    public void updateEvent(long eventId, String calendarId) throws GeneralSecurityException, IOException {
        EventDto eventDto = userServiceClient.getEvent(eventId);
        validator.validateUpdate(eventDto);
        Event event = updateEvent(eventDto, calendarId);
        log.info("Event updated: {}", event.getHtmlLink());
    }

    @Override
    public List<Event> getEvents(String calendarId) throws GeneralSecurityException, IOException {
        Calendar service = authorizationService.authorizeAndGetCalendar();
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list(calendarId)
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        return events.getItems();
    }

    private Event initEvent(EventDto eventDto) {
        return new Event()
                .setStart(DateGoogleConverter.toEventDateTime(eventDto.getStartDate()))
                .setEnd(DateGoogleConverter.toEventDateTime(eventDto.getEndDate()))
                .setDescription(eventDto.getDescription())
                .setAttendees(eventDto.getAttendeeEmails().stream()
                        .map(email -> new EventAttendee().setEmail(email))
                        .toList()
                )
                .setSummary(eventDto.getTitle())
                .setReminders(new Event.Reminders().setUseDefault(true));
    }

    private Event add(EventDto eventDto, String calendarId) throws GeneralSecurityException, IOException {
        Calendar service = authorizationService.authorizeAndGetCalendar();
        checkIsCalendarAvailable(calendarId, service);
        Event event = initEvent(eventDto);
        if (eventDto.getCalendarEventId() != null) {
            return service.events()
                    .patch(calendarId, eventDto.getCalendarEventId(), event)
                    .execute();
        }
        return service.events()
                .insert(calendarId, event)
                .execute();
    }

    private void checkIsCalendarAvailable(String calendarId, Calendar service) throws IOException {
        if (service.calendars().get(calendarId) == null) {
            throw new DataValidationException("calendar id = " + calendarId + " is not available");
        }
    }
}
