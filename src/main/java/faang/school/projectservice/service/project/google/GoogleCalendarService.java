package faang.school.projectservice.service.project.google;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
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
        Calendar calendar = googleCalendar.getCalendar();
        Event event = new Event();
        event.setSummary("aa");
        event.setDescription(String.valueOf(disc));

        return mapper.toDto(event);
    }
}
