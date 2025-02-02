package faang.school.projectservice.service;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import faang.school.projectservice.dto.calendar.CalendarDTO;
import faang.school.projectservice.dto.calendar.CalendarRole;
import faang.school.projectservice.dto.calendar.CalendarScopeTypes;
import faang.school.projectservice.dto.calendar.CreateEventDTO;
import faang.school.projectservice.exception.GoogleCalendarException;
import faang.school.projectservice.util.ApiCall;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleCalendarService {

    private final Calendar googleCalendarClient;

    public List<CalendarDTO> getCalendars() {
        CalendarList calendarList = executeApiCall(
                () -> googleCalendarClient.calendarList().list().execute(),
                "Error fetching calendars"
        );
        return calendarList.getItems().stream()
                .map(c -> new CalendarDTO(c.getId(), c.getSummary()))
                .collect(Collectors.toList());
    }

    public String getCalendar(String calendarId) {
        return executeApiCall(
                () -> googleCalendarClient.calendars().get(calendarId).execute().getId(),
                "Error fetching calendar with ID: " + calendarId
        );
    }

    public String createCalendar(String summary) {
        com.google.api.services.calendar.model.Calendar newCalendar =
                new com.google.api.services.calendar.model.Calendar();
        newCalendar.setSummary(summary);
        return executeApiCall(
                () -> googleCalendarClient.calendars().insert(newCalendar).execute().getId(),
                "Error creating calendar"
        );
    }

    public String createEvent(String calendarId, CreateEventDTO dto) {
        Event event = new Event()
                .setSummary(dto.getSummary())
                .setDescription(dto.getDescription())
                .setStart(new EventDateTime().setDateTime(new DateTime(dto.getStartDateTime()))
                        .setTimeZone(TimeZone.getDefault().getID()))
                .setEnd(new EventDateTime().setDateTime(new DateTime(dto.getEndDateTime()))
                        .setTimeZone(TimeZone.getDefault().getID()))
                .setReminders(new Event.Reminders().setUseDefault(false));
        return executeApiCall(
                () -> googleCalendarClient.events().insert(calendarId, event).execute().getId(),
                "Error creating event in calendar: " + calendarId
        );
    }

    public List<Event> getEvents(String calendarId) {
        return executeApiCall(
                () -> googleCalendarClient.events().list(calendarId).execute().getItems(),
                "Error fetching events for calendar: " + calendarId
        );
    }

    public Event getEventById(String calendarId, String eventId) {
        return executeApiCall(
                () -> googleCalendarClient.events().get(calendarId, eventId).execute(),
                "Error fetching event with ID: " + eventId
        );
    }

    public void updateEvent(String calendarId, String eventId, CreateEventDTO dto) {
        executeApiCall(() -> {
            Event event = googleCalendarClient.events().get(calendarId, eventId).execute();
            event.setSummary(dto.getSummary())
                    .setDescription(dto.getDescription())
                    .setStart(new EventDateTime().setDateTime(new DateTime(dto.getStartDateTime())))
                    .setEnd(new EventDateTime().setDateTime(new DateTime(dto.getEndDateTime())));
            return googleCalendarClient.events().update(calendarId, eventId, event).execute();
        }, "Error updating event with ID: " + eventId);
    }

    public void deleteEvent(String calendarId, String eventId) {
        executeApiCall(() -> googleCalendarClient.events().delete(calendarId, eventId).execute(),
                "Error deleting event with ID: " + eventId);
    }

    public void addCalendarAccess(String calendarId, String userEmail, CalendarRole role) {
        AclRule rule = new AclRule()
                .setRole(role.name().toLowerCase())
                .setScope(new AclRule.Scope()
                        .setType(CalendarScopeTypes.USER.name().toLowerCase())
                        .setValue(userEmail));
        executeApiCall(() -> googleCalendarClient.acl().insert(calendarId, rule).execute(),
                "Error granting role in calendar: " + calendarId);
    }

    public void removeCalendarAccess(String calendarId, String userEmail) {
        executeApiCall(() -> googleCalendarClient.acl().delete(calendarId,
                        CalendarScopeTypes.USER.name().toLowerCase() + ":" + userEmail).execute(),
                "Error removing role in calendar: " + calendarId);
    }

    public List<AclRule> getCalendarAccessList(String calendarId) {
        return executeApiCall(
                () -> googleCalendarClient.acl().list(calendarId).execute().getItems(),
                "Error fetching ACL roles for calendar: " + calendarId
        );
    }

    private <T> T executeApiCall(ApiCall<T> apiCall, String errorMessage) {
        try {
            return apiCall.execute();
        } catch (IOException e) {
            throw new GoogleCalendarException(errorMessage);
        }
    }
}
