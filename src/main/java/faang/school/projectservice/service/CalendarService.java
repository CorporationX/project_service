package faang.school.projectservice.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.Event;
import faang.school.projectservice.dto.calendar.ACLDto;
import faang.school.projectservice.dto.calendar.CalendarDto;
import faang.school.projectservice.dto.calendar.CalendarEventDto;
import faang.school.projectservice.exception.CredentialsNotFoundException;
import faang.school.projectservice.mapper.ACLMapper;
import faang.school.projectservice.mapper.CalendarMapper;
import faang.school.projectservice.mapper.CalendarEventMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.GoogleCalendarToken;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarService {

    @Value("${calendar.event.count_to_return}")
    private int eventsCountToBeReturned;
    private final GoogleAuthorizationService oAuthService;
    private final ProjectRepository projectRepository;
    private final CalendarEventMapper eventMapper;
    private final CalendarMapper calendarMapper;
    private final ACLMapper aclMapper;
    private final Environment env;

    public URL getAccessCode() {
        return oAuthService.getAuthUrl();
    }

    @Transactional
    public Credential auth(long projectId, @NotNull String code) {
        Project project = projectRepository.getByIdOrThrow(projectId);
        GoogleCalendarToken calendarToken = oAuthService.authorizeProject(project, code);
        return oAuthService.generateCredential(calendarToken);
    }

    public CalendarEventDto createEvent(long projectId, String calendarId, CalendarEventDto eventDto) {
        return executeWithCalendar(projectId, service -> {
            Event event = eventMapper.toModel(eventDto);
            Event savedEvent = service.events()
                    .insert(calendarId, event)
                    .execute();
            log.info("Created new event");
            return eventMapper.toDto(savedEvent);
        });
    }

    public CalendarEventDto updateEvent(long projectId, String calendarId, String eventId, CalendarEventDto eventDto) {
        return executeWithCalendar(projectId, service -> {
            Event event = eventMapper.toModel(eventDto);
            Event updatedEvent = service.events()
                    .update(calendarId, eventId, event)
                    .execute();
            log.info("Updated event with ID: " + eventId);
            return eventMapper.toDto(updatedEvent);
        });
    }

    public List<CalendarEventDto> getEvents(long projectId, String calendarId) {
        return executeWithCalendar(projectId, service -> {
            List<Event> events = service.events()
                    .list(calendarId)
                    .setMaxResults(eventsCountToBeReturned)
                    .execute()
                    .getItems();
            return eventMapper.toDtos(events);
        });
    }

    public void deleteEvent(long projectId, String calendarId, String eventId) {
        executeWithCalendar(projectId, service -> {
            service.events().delete(calendarId, eventId).execute();
            return null;
        });
    }


    public CalendarDto createCalendar(long projectId, CalendarDto calendarDto) {
        return executeWithCalendar(projectId, service -> {
            var newCalendar = calendarMapper.toModel(calendarDto);
            var createdCalendar = service.calendars().insert(newCalendar).execute();
            return calendarMapper.toDto(createdCalendar);
        });
    }

    public CalendarDto getCalendar(long projectId, String calendarId) {
        return executeWithCalendar(projectId, service -> {
            var calendar = service.calendars().get(calendarId).execute();
            return calendarMapper.toDto(calendar);
        });
    }


    public ACLDto createAcl(long projectId, String calendarId, ACLDto aclDto) {
        return executeWithCalendar(projectId, service -> {
            AclRule newAcl = aclMapper.toModel(aclDto);
            AclRule createdAcl = service.acl().insert(calendarId, newAcl).execute();
            return aclMapper.toDto(createdAcl);
        });
    }

    public List<ACLDto> listAcl(long projectId, String calendarId) {
        return executeWithCalendar(projectId, service -> {
            List<AclRule> aclList = service.acl().list(calendarId).execute().getItems();
            return aclMapper.toDtos(aclList);
        });
    }


    public void deleteAcl(long projectId, String calendarId, long aclId) {
        executeWithCalendar(projectId, service -> {
            service.acl().delete(calendarId, String.valueOf(aclId)).execute();
            return null;
        });
    }


    public Calendar buildCalendar(long projectId) {
        Credential credential;
        try {
            credential = auth(projectId, null);
        } catch (Exception e) {
            throw new CredentialsNotFoundException(
                    String.format("Project: %d dont have credentials, set credentials for this", projectId));
        }

        return new Calendar.Builder(oAuthService.getHttpTransport(), oAuthService.getJsonFactory(), credential)
                .setApplicationName(env.getProperty("google.applicationName"))
                .build();
    }


    private <T> T executeWithCalendar(long projectId, CalendarServiceFunction<T> function) {
        Calendar service = buildCalendar(projectId);
        try {
            return function.execute(service);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}

