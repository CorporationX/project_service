package faang.school.projectservice.service.calendar;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.calendar.Calendar;
import faang.school.projectservice.dto.calendar.AclDto;
import faang.school.projectservice.dto.calendar.CalendarDto;
import faang.school.projectservice.dto.calendar.CalendarEventDto;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;

@Component
public interface CalendarService {

    URL getAccessCode();

    Credential auth(long projectId, String code);

    CalendarEventDto createEvent(long projectId, String calendarId, CalendarEventDto eventDto);

    CalendarDto getCalendar(long projectId, String calendarId);

    List<CalendarEventDto> getEvents(long projectId, String calendarId);

    void deleteEvent(long projectId, String calendarId, long eventId);

    CalendarDto createCalendar(long projectId, CalendarDto calendarDto);

    AclDto createAcl(long projectId, String calendarId, AclDto aclDto);

    List<AclDto> listAcl(long projectId, String calendarId);

    void deleteAcl(long projectId, String calendarId, long aclId);

    Calendar buildCalendar(long projectId);
}
