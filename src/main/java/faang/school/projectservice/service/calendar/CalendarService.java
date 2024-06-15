package faang.school.projectservice.service.calendar;

import com.google.api.client.auth.oauth2.Credential;
import faang.school.projectservice.dto.calendar.AclDto;
import faang.school.projectservice.dto.calendar.CalendarDto;
import faang.school.projectservice.dto.calendar.CalendarEventDto;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;

@Component
public interface CalendarService {

    URL getAuthUrl();

    Credential auth(long projectId, String code);

    CalendarEventDto createEvent(long projectId, String calendarId, CalendarEventDto eventDto);

    CalendarDto getCalendar(long projectId, String calendarId);

    List<CalendarEventDto> getEvents(long projectId, String calendarId);

    void deleteEvent(long projectId, String calendarId, String eventId);

    CalendarDto createCalendar(long projectId, CalendarDto calendarDto);

    AclDto createAcl(long projectId, String calendarId, AclDto aclDto);

    List<AclDto> listAcl(long projectId, String calendarId);

    void deleteAcl(long projectId, String calendarId, String aclId);
}
