package faang.school.projectservice.service.google;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.Event;
import faang.school.projectservice.dto.MeetDto;
import faang.school.projectservice.mapper.EventMeetMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleCalendarService {

    private static final String EVENT_NOT_FOUND = "Event not found in Google Calendar";

    @Value("${google.calendar_id}")
    private String calendarId;

    private final Calendar calendar;
    private final EventMeetMapper eventMeetMapper;

    public MeetDto getEventById(String eventId) {
        Event event;
        try {
            event = calendar.events().get(calendarId, eventId).execute();
        } catch (IOException e) {
            log.info(EVENT_NOT_FOUND);
            throw new EntityNotFoundException(e.getMessage());
        }
        return eventMeetMapper.eventToMeetDto(event);
    }

    public Event createEvent(MeetDto meetDto) {
        log.info("Create event in Google calendar");
        Event event = eventMeetMapper.meetDtoToEvent(meetDto);
        try {
            return calendar.events().insert(calendarId, event).execute();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public MeetDto updateEvent(MeetDto meetDto) {
        log.info("Update event in Google calendar");
        Event updatedEvent;
        try {
            Event event = calendar.events().get(calendarId, meetDto.getEventGoogleId()).execute();
            event = eventMeetMapper.meetDtoToEvent(meetDto);
            updatedEvent = calendar.events().update(calendarId, meetDto.getEventGoogleId(), event).execute();
        } catch (IOException e) {
            log.info(EVENT_NOT_FOUND);
            throw new EntityNotFoundException(e.getMessage());
        }
        return eventMeetMapper.eventToMeetDto(updatedEvent);
    }

    public void deleteEvent(String eventId) {
        System.out.println("Remove calendar event with ID: " + eventId);
        try {
            calendar.events().delete(calendarId, eventId).execute();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void createAcl(String email) {
        AclRule rule = new AclRule();
        AclRule.Scope scope = new AclRule.Scope();
        scope.setType("user").setValue(email);
        rule.setScope(scope).setRole("reader");

        AclRule createdRule;
        try {
            log.info("Create ACL for user {} in calendar: {}", email, calendarId);
            createdRule = calendar.acl().insert(calendarId, rule).execute();
            log.info("Acl ID: {}", createdRule.getId());
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void deleteAcl(String userEmail) {
        try {
            log.info("Remove ACL with ID: {}", userEmail);
            calendar.acl().delete(calendarId, "user:" + userEmail).execute();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String getAclById(String userEmail) {
        AclRule rule;
        try {
            log.info("Get ACL with ID: {}", userEmail);
            rule = calendar.acl().get(calendarId, "user:" + userEmail).execute();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return rule.getId() + ": " + rule.getRole();
    }
}
