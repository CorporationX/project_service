package faang.school.projectservice.service;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.service.google.GoogleCalendarService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class ProjectScheduleService {
    private final GoogleCalendarService googleCalendarService;

    private static final Logger logger = LoggerFactory.getLogger(ProjectScheduleService.class);

    public void createScheduleEvent(String calendarId, Schedule schedule) {
        Event event = new Event();
        event.setSummary(schedule.getName());
        event.setDescription(schedule.getDescription());
        LocalDateTime startDateTime =schedule.getCreatedAt();
        EventDateTime start = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(startDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))
                .setTimeZone(ZoneId.systemDefault().toString());

        String creatorEmail = "";//TODO from users
        event.setCreator(new Event.Creator().setEmail(creatorEmail));

        event.setStart(start);

        googleCalendarService.createEvent(calendarId, event);
    }

    public Event getScheduleEvent(String calendarId, String eventId) {
        return googleCalendarService.getEvent(calendarId, eventId);
    }

    public void deleteScheduleEvent(String calendarId, String eventId) {
        googleCalendarService.deleteEvent(calendarId, eventId);
    }
}
