package faang.school.projectservice.service.project.google;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import faang.school.projectservice.config.context.google.GoogleCalendarConfig;
import faang.school.projectservice.dto.google.EventCalendarDto;
import faang.school.projectservice.mapper.project.google.EventCalendarMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Service
@RequiredArgsConstructor
public class GoogleCalendarService {

    private final GoogleCalendarConfig googleCalendar;
    private final EventCalendarMapper mapper;

    public EventCalendarDto createEvent(long disc) throws GeneralSecurityException, IOException {

        DateTime startDateTime = new DateTime("2015-05-28T09:00:00-07:00");
        DateTime endDateTime = new DateTime("2015-05-28T17:00:00-07:00");
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Russia/Moscow");
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Russian/Moscow");

        Event event =  new Event()
                .setSummary("New Event")
                .setStart(start)
                .setEnd(end);

        return mapper.toDto(event);
    }
}
