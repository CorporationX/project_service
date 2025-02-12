package faang.school.projectservice.service;

import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.AclRule.Scope;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.repository.MeetRepository;
import faang.school.projectservice.service.google.GoogleCalendarService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectMeetService {
    private final GoogleCalendarService googleCalendarService;
    private final MeetRepository meetRepository;

    private static final Logger logger = LoggerFactory.getLogger(ProjectMeetService.class);

    public void createMeetEvent(String calendarId, Meet meet) {
        Event event = new Event();
        event.setSummary(meet.getTitle());
        event.setDescription(meet.getDescription());

        LocalDateTime startDateTime = meet.getStartsAt();
        EventDateTime start = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(startDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))
                .setTimeZone(ZoneId.systemDefault().toString());
        event.setStart(start);

        String creatorEmail = "";//TODO from users
        List<String> attendeesEmail = List.of("");//TODO from users

        event.setCreator(new Event.Creator().setEmail(creatorEmail));
        List<EventAttendee> attendees = attendeesEmail.stream()
                .map(email -> new EventAttendee().setEmail(email))
                .collect(Collectors.toList());
        event.setAttendees(attendees);

        Event createdEvent = googleCalendarService.createEvent(calendarId, event);
        meet.setGoogleEventId(createdEvent.getId());

        createAclForEvent(calendarId, creatorEmail, attendeesEmail);
    }

    public Event getMeetEvent(String calendarId, String eventId) {
        return googleCalendarService.getEvent(calendarId, eventId);
    }

    public void deleteMeetEvent(String calendarId, String eventId) {
        googleCalendarService.deleteEvent(calendarId, eventId);
    }

    private void createAclForEvent(String calendarId, String creatorEmail, List<String> attendeesEmail) {
        AclRule creatorAcl = new AclRule()
                .setRole("owner")
                .setScope(new Scope().setType("user").setValue(creatorEmail));
        googleCalendarService.createAcl(calendarId, creatorAcl);

        attendeesEmail.forEach(participantEmail -> {
            AclRule attendeeAcl = new AclRule()
                    .setRole("reader")
                    .setScope(new Scope().setType("user").setValue(participantEmail));
            googleCalendarService.createAcl(calendarId, attendeeAcl);
        });
    }
}
