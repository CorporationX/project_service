package faang.school.projectservice.service.google_calendar;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.EventDtoForGoogleCalendar;
import faang.school.projectservice.exceptions.google_calendar.exceptions.GoogleCalendarException;
import faang.school.projectservice.mapper.EventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
@Service
public class EventService {
    @Lazy
    @Autowired
    private Calendar calendarClient;
    private final UserServiceClient userServiceClient;
    private final EventMappingService eventMappingService;
    private final EventMapper eventMapper;
    private static final String DEFAULT_CALENDAR = "primary";

    public String createEventInGoogleCalendar(Long eventId, String calendarId) throws IOException {
        EventDtoForGoogleCalendar eventDto = userServiceClient.getEventForGoogleCalendar(eventId);

        Event googleEvent = mapToGoogleEvent(eventDto);
        String resolvedCalendarId = resolveCalendarId(calendarId);

        Event createdEvent = calendarClient.events()
                .insert(resolvedCalendarId, googleEvent)
                .execute();
        log.info("Create event in Google Calendar with ID: '{}'", createdEvent.getId());

        eventMappingService.saveMapping(eventId, createdEvent.getId());
        return createdEvent.getId();
    }

    public EventDtoForGoogleCalendar getEventFromGoogleCalendar(Long eventId) throws IOException {
        String googleEventId = eventMappingService.getGoogleEventIdByEventId(eventId);
        Event googleEvent = calendarClient.events()
                .get(DEFAULT_CALENDAR, googleEventId)
                .execute();

        return mapToEventDto(googleEvent, googleEventId);
    }

    public void deleteEventFromGoogleCalendar(Long eventId) throws IOException {
        String googleEventId = eventMappingService.getGoogleEventIdByEventId(eventId);
        calendarClient.events()
                .delete(DEFAULT_CALENDAR, googleEventId)
                .execute();
        log.info("Event with googleEventId '{}' delete from Google Calendar", googleEventId);

        eventMappingService.deleteMapping(eventId);
    }

    private Event mapToGoogleEvent(EventDtoForGoogleCalendar eventDto) {
        Event googleEvent = eventMapper.mapToGoogleEvent(eventDto);
        googleEvent.setId(null);
        return googleEvent;
    }

    private String resolveCalendarId(String calendarId) {
        if (calendarId == null || calendarId.isEmpty() || !isCalendarExists(calendarId)) {
            return DEFAULT_CALENDAR;
        }
        return calendarId;
    }

    private boolean isCalendarExists(String calendarId) {
        try {
            calendarClient.calendars().get(calendarId).execute();
            return true;
        } catch (IOException e) {
            log.warn("Calendar with ID '{}' not found", calendarId);
            return false;
        }
    }

    private EventDtoForGoogleCalendar mapToEventDto(Event googleEvent, String googleEventId) {
        Long eventIdForDto = eventMappingService.getEventIdByGoogleEventId(googleEventId);
        EventDtoForGoogleCalendar eventDto = eventMapper.mapToEventDto(googleEvent);
        eventDto.setId(eventIdForDto);
        return eventDto;
    }
}