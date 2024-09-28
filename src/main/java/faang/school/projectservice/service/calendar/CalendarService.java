package faang.school.projectservice.service.calendar;

import com.google.api.services.calendar.model.Event;
import faang.school.projectservice.dto.EventDto;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface CalendarService {
    void addEventToCalendar(long eventId, String calendarId) throws GeneralSecurityException, IOException;

    Event update(EventDto eventDto, String calendarId) throws GeneralSecurityException, IOException;

    void update(long eventId, String calendarId) throws GeneralSecurityException, IOException;

    List<Event> getEvents(String calendarId) throws GeneralSecurityException, IOException;
}
