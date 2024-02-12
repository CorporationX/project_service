package faang.school.projectservice.service.project.google;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.google.GoogleCalendarConfig;
import faang.school.projectservice.dto.client.EventDto;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.google.EventCalendarDto;
import faang.school.projectservice.mapper.project.google.EventCalendarMapper;
import faang.school.projectservice.validator.project.GoogleCalendarValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class GoogleCalendarService {

    private final GoogleCalendarConfig googleCalendar;
    private final UserServiceClient userServiceClient;
    private final GoogleCalendarValidator validator;
    private final EventCalendarMapper mapper;

    public EventCalendarDto createEvent(long disc) throws GeneralSecurityException, IOException {

        DateTime startDateTime = new DateTime("2015-05-28T09:00:00-07:00");
        DateTime endDateTime = new DateTime("2015-05-28T17:00:00-07:00");
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("America/Los_Angeles");
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("America/Los_Angeles");

        Event event =  new Event()
                .setSummary("New Event")
                .setDescription(disc + " new disc")
                .setStart(start)
                .setEnd(end);


        String calendarId = "primary";

        event = googleCalendar.getCalendar().events().insert(calendarId, event).execute();
        System.out.println("Event created " + event.getHtmlLink());

        return mapper.toDto(event);
    }
}
