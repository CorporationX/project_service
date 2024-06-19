package faang.school.projectservice.service.project.calendar;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.Event;
import faang.school.projectservice.dto.project.calendar.AclDto;
import faang.school.projectservice.dto.project.calendar.CalendarDto;
import faang.school.projectservice.dto.project.calendar.EventDto;
import faang.school.projectservice.mapper.AclMapper;
import faang.school.projectservice.mapper.CalendarMapper;
import faang.school.projectservice.mapper.EventMapper;
import faang.school.projectservice.model.CalendarToken;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarService {
    public static final int EVENTS_COUNT_TO_BE_RETURNED = 10;
    private final GoogleAuthorizationService oAuthService;
    private final ProjectService projectService;
    private final EventMapper eventMapper;
    private final CalendarMapper calendarMapper;
    private final AclMapper aclMapper;
    @Value("${spring.OAuth2.applicationName}")
    private String applicationName;


    /**
     * Возвращает ссылку аутентификации. Предоставив доступ по этой ссылке, вы получите код для авторизации
     *
     * @return URL
     */
    public URL getAuthUrl() {
        return oAuthService.getAuthUrl();
    }

    /**
     * Авторизация проекта по коду авторизации (Сохраняет токен авторизации в БД)
     *
     * @param projectId Id проекта, для которого необходимо выполнить авторизацию
     * @param code      код авторизации. Его можно получить, предоставив доступ, перейдя по ссылке аутентификации (projects/calendars/auth)
     * @return объект Credential для доступа к Calendar API
     */
    @Transactional
    public Credential auth(long projectId, @NotNull String code) {
        Project project = projectService.getProjectModel(projectId);
        CalendarToken calendarToken = oAuthService.authorizeProject(project, code);
        return oAuthService.generateCredential(calendarToken);
    }

    @Transactional
    public EventDto createEvent(Long projectId, String calendarId, EventDto eventDto) {
        Calendar service = buildCalendar(projectId);

        Event event = eventMapper.toModel(eventDto);
        try {
            Event savedEvent = service.events()
                    .insert(calendarId, event)
                    .execute();

            return eventMapper.toDto(savedEvent);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public List<EventDto> getEvents(long projectId, String calendarId) {
        Calendar service = buildCalendar(projectId);

        try {
            List<Event> eventsOfProject = service.events()
                    .list(calendarId)
                    .setMaxResults(EVENTS_COUNT_TO_BE_RETURNED)
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
                    .delete(calendarId, eventId)
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
    public AclDto createAcl(long projectId, String calendarId, AclDto aclDto) {
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
    public List<AclDto> listAcl(long projectId, String calendarId) {
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
    public void deleteAcl(long projectId, String calendarId, String aclId) {
        Calendar service = buildCalendar(projectId);

        try {
            service.acl()
                    .delete(calendarId, aclId)
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    Calendar buildCalendar(long projectId) {
        Credential credential = auth(projectId, null);

        return new Calendar.Builder(oAuthService.getHttp_transport(), oAuthService.getJsonFactory(), credential)
                .setApplicationName(applicationName)
                .build();
    }
}
