package faang.school.projectservice.service.meet.observer;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.repository.MeetRepository;
import faang.school.projectservice.service.meet.event.MeetCreateEvent;
import faang.school.projectservice.service.meet.event.MeetDeleteEvent;
import faang.school.projectservice.service.meet.event.MeetUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
@Lazy
public class GoogleCalendarObserver {

    private final Calendar googleCalendar;
    private final ZoneId zoneId = ZoneId.of("Europe/Moscow");
    private final String calendarId = "fcc4660e6d7a24a8ef8ee0f841f3e0acf298d0b1e7c6b92a8bc0c5c3bc5f2a97"
            + "@group.calendar.google.com";
    private final MeetRepository meetRepository;

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleMeetCreated(MeetCreateEvent meetCreateEvent) {
        Meet meet = meetCreateEvent.meet();
        Event newEvent = createEvent(meet);

        try {
            newEvent = googleCalendar.events().insert(calendarId, newEvent).execute();
        } catch (Exception e) {
            log.error("Unable to create meet {}", e.getMessage());
            throw new RuntimeException("Unable to create meet");
        }

        meet.setGoogleEventId(newEvent.getId());
        meetRepository.save(meet);
        log.info("New Meet created: {}", newEvent);
    }

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleMeetUpdated(MeetUpdateEvent meetUpdateEvent) {
        Meet meet = meetUpdateEvent.meet();
        if (meet.getGoogleEventId() == null) {
            log.error("Unable to update google event {}", meetUpdateEvent.meet());
            throw new RuntimeException("Google Event not updated. EventID is null");
        }

        Event event = createEvent(meet);

        try {
            event = googleCalendar.events().update(calendarId, meet.getGoogleEventId(), event).execute();
        } catch (IOException e) {
            log.error("Unable to update meet {}", e.getMessage());
            throw new RuntimeException("Unable to update meet");
        }

        log.info("Updated Google Event {}", event);
    }

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleMeetDeleted(MeetDeleteEvent meetDeleteEvent) {
        Meet meet = meetDeleteEvent.meet();
        try {
            googleCalendar.events().delete(calendarId, meet.getGoogleEventId()).execute();
        } catch (IOException e) {
            log.error("Unable to delete google meet {}", e.getMessage());
            throw new RuntimeException("Unable to delete google event");
        }

        meet.setGoogleEventId(null);
        meetRepository.save(meet);
        log.info("Deleted Google Event from meet: {}", meet);
    }

    private DateTime convertToEventDateTime(LocalDateTime dateTime) {
        ZonedDateTime zonedDateTime = dateTime.atZone(zoneId);
        return new DateTime(zonedDateTime.toInstant().toEpochMilli());
    }

    private Event createEvent(Meet meet) {
        EventDateTime start = new EventDateTime()
                .setDateTime(convertToEventDateTime(meet.getStartsAt()))
                .setTimeZone(zoneId.getId());

        EventDateTime end = new EventDateTime()
                .setDateTime(convertToEventDateTime(meet.getStartsAt().plusHours(1)))
                .setTimeZone(zoneId.getId());

        return new Event()
                .setSummary(meet.getTitle())
                .setDescription(meet.getDescription())
                .setStart(start)
                .setEnd(end);
    }
}
