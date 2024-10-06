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
        Project project = projectRepository.getProjectById(projectId);
        GoogleCalendarToken calendarToken = oAuthService.authorizeProject(project, code);
        return oAuthService.generateCredential(calendarToken);
    }

    @Transactional
    public CalendarEventDto createEvent(long projectId, String calendarId, CalendarEventDto eventDto) {
        Calendar service = buildCalendar(projectId);

        Event event = eventMapper.toModel(eventDto);
        try {
            Event savedEvent = service.events()
                    .insert(String.valueOf(calendarId), event)
                    .execute();

            log.info("Created new event");
            return eventMapper.toDto(savedEvent);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public List<CalendarEventDto> getEvents(long projectId, String calendarId) {
        Calendar service = buildCalendar(projectId);

        try {
            List<Event> eventsOfProject = service.events()
                    .list(calendarId)
                    .setMaxResults(eventsCountToBeReturned)
                    .execute()
                    .getItems();

            return eventMapper.toDtos(eventsOfProject);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public void deleteEvent(long projectId, String calendarId, String eventId) {
        Calendar service = buildCalendar(projectId);

        try {
            service.events()
                    .delete(calendarId, String.valueOf(eventId))
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public CalendarDto createCalendar(long projectId, CalendarDto calendarDto) {
        Calendar service = buildCalendar(projectId);

        var newCalendar = calendarMapper.toModel(calendarDto);
        try {
            var createdCalendar = service.calendars()
                    .insert(newCalendar)
                    .execute();

            return calendarMapper.toDto(createdCalendar);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public CalendarDto getCalendar(long projectId, String calendarId) {
        Calendar service = buildCalendar(projectId);

        try {
            var calendar = service.calendars()
                    .get(calendarId)
                    .execute();

            return calendarMapper.toDto(calendar);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public ACLDto createAcl(long projectId, String calendarId, ACLDto aclDto) {
        Calendar service = buildCalendar(projectId);

        AclRule newAcl = aclMapper.toModel(aclDto);
        try {
            AclRule createdAcl = service.acl()
                    .insert(calendarId, newAcl)
                    .execute();

            return aclMapper.toDto(createdAcl);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public List<ACLDto> listAcl(long projectId, String calendarId) {
        Calendar service = buildCalendar(projectId);

        try {
            List<AclRule> aclRuleList = service.acl()
                    .list(calendarId)
                    .execute()
                    .getItems();

            return aclMapper.toDtos(aclRuleList);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public void deleteAcl(long projectId, String calendarId, long aclId) {
        Calendar service = buildCalendar(projectId);

        try {
            service.acl()
                    .delete(calendarId, String.valueOf(aclId))
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public Calendar buildCalendar(long projectId) {
        Credential credential;
        try {
            credential = auth(projectId, null);
        } catch (Exception e) {
            throw new CredentialsNotFoundException(
                    String.format("Project: %d dont have credentials, set credentials for this", projectId));
        }

        return new Calendar.Builder(oAuthService.getHttp_transport(), oAuthService.getJsonFactory(), credential)
                .setApplicationName(env.getProperty("google.applicationName"))
                .build();
    }
}

