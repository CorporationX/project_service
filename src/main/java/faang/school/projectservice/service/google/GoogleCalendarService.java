package faang.school.projectservice.service.google;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.CalendarListEntry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class GoogleCalendarService {
    private final Calendar calendarService;

    private static final Logger logger = LoggerFactory.getLogger(GoogleCalendarService.class);

    public Event createEvent(String calendarId, Event event) {
        try {
            return calendarService.events().insert(calendarId, event).execute();
        } catch (IOException e) {
            logger.error("Failed to create event in calendar: {}", calendarId, e);
            return null;
        }
    }

    public Event getEvent(String calendarId, String eventId) {
        try {
            return calendarService.events().get(calendarId, eventId).execute();
        } catch (IOException e) {
            logger.error("Failed to get event from calendar: {} with event ID: {}", calendarId, eventId, e);
            return null;
        }
    }

    public void deleteEvent(String calendarId, String eventId) {
        try {
            calendarService.events().delete(calendarId, eventId).execute();
        } catch (IOException e) {
            logger.error("Failed to delete event from calendar: {} with event ID: {}", calendarId, eventId, e);
        }
    }

    public com.google.api.services.calendar.model.Calendar createCalendar(com.google.api.services.calendar.model.Calendar calendar) {
        try {
            calendarService.calendars().insert(calendar).execute();
            return calendar;
        } catch (IOException e) {
            logger.error("Failed to create calendar", e);
            return null;
        }
    }

    public CalendarListEntry getCalendar(String calendarId) {
        try {
            return calendarService.calendarList().get(calendarId).execute();
        } catch (IOException e) {
            logger.error("Failed to create calendar", e);
            return null;
        }
    }

    public void createAcl(String calendarId, com.google.api.services.calendar.model.AclRule aclRule) {
        try {
            calendarService.acl().insert(calendarId, aclRule).execute();
        } catch (IOException e) {
            logger.error("Failed to create ACL for calendar: {}", calendarId, e);
        }
    }

    public com.google.api.services.calendar.model.AclRule getAcl(String calendarId, String ruleId) {
        try {
            return calendarService.acl().get(calendarId, ruleId).execute();
        } catch (IOException e) {
            logger.error("Failed to get ACL for calendar: {} with rule ID: {}", calendarId, ruleId, e);
            return null;
        }
    }

    public void deleteAcl(String calendarId, String ruleId) {
        try {
            calendarService.acl().delete(calendarId, ruleId).execute();
        } catch (IOException e) {
            logger.error("Failed to delete ACL for calendar: {} with rule ID: {}", calendarId, ruleId, e);
        }
    }
}
