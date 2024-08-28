package faang.school.projectservice.service;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.Event;
import faang.school.projectservice.dto.calendar.CalendarDto;
import faang.school.projectservice.dto.calendar.EventDto;
import faang.school.projectservice.mapper.CalendarMapper;
import faang.school.projectservice.mapper.EventMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@Service
@Slf4j
@RequiredArgsConstructor
public class CalendarService {

    private final Calendar googleCalendar;
    private final EventMapper eventMapper;
    private final CalendarMapper calendarMapper;
    private final ProjectValidator projectValidator;
    private final ProjectRepository projectRepository;

    @Transactional
    public CalendarDto createCalendarForProject(Long projectId, CalendarDto calendarDto) {
        return handleRequest(() -> {
            Project project = projectRepository.getProjectById(projectId);
            projectValidator.verifyProjectDoesNotHaveCalendar(project);
            com.google.api.services.calendar.model.Calendar newCalendar = calendarMapper.toEntity(calendarDto);
            newCalendar = googleCalendar.calendars().insert(newCalendar).execute();
            project.setCalendarId(newCalendar.getId());
            projectRepository.save(project);
            return calendarMapper.toDto(newCalendar);
        });
    }

    public EventDto createEvent(String calendarId, EventDto eventDto) {
        return handleRequest(() -> {
            Event event = eventMapper.toEvent(eventDto);
            event = googleCalendar.events().insert(calendarId, event).execute();
            return eventMapper.toDto(event);
        });
    }

    public EventDto getEvent(String calendarId, String eventId) {
        return handleRequest(() -> {
            Event event = googleCalendar.events().get(calendarId, eventId).execute();
            return eventMapper.toDto(event);
        });
    }

    public void deleteEvent(String calendarId, String eventId) {
        handleRequest(() -> googleCalendar.events().delete(calendarId, eventId).execute());
    }

    public List<EventDto> createEvents(String calendarId, List<EventDto> eventDtoList) {
        List<EventDto> createdEventDtoList = new ArrayList<>();
        for (EventDto eventDto : eventDtoList) {
            EventDto createdEventDto = createEvent(calendarId, eventDto);
            createdEventDtoList.add(createdEventDto);
        }
        return createdEventDtoList;
    }

    public List<EventDto> getEvents(String calendarId) {
        return handleRequest(() -> {
            List<Event> calendarEvents = googleCalendar.events().list(calendarId).execute().getItems();
            return calendarEvents.stream().map(eventMapper::toDto).toList();
        });
    }

    public AclRule addAclRule(String calendarId, String email, String role, String scopeType) {
        return handleRequest(() -> {
            AclRule rule = new AclRule()
                    .setRole(role)
                    .setScope(new AclRule.Scope().setType(scopeType).setValue(email));
            return googleCalendar.acl().insert(calendarId, rule).execute();
        });
    }


    public List<AclRule> getAclRules(String calendarId) {
        return handleRequest(() -> googleCalendar.acl().list(calendarId).execute().getItems());
    }


    public void deleteAclRule(String calendarId, String ruleId) {
        handleRequest(() -> googleCalendar.acl().delete(calendarId, ruleId).execute());
    }

    private <T> T handleRequest(Callable<T> supplier) {
        try {
            return supplier.call();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }
}