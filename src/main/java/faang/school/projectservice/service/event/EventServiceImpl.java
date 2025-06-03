package faang.school.projectservice.service.event;

import faang.school.projectservice.client.GoogleCalendarClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.event.CalendarEventResponse;
import faang.school.projectservice.dto.event.CreateEventDto;
import faang.school.projectservice.dto.event.EventOutputDto;
import faang.school.projectservice.exception.FailedRequestException;
import faang.school.projectservice.mapper.event.EventMapper;
import faang.school.projectservice.model.Event;
import faang.school.projectservice.model.EventStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.EventRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.EventService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final GoogleCalendarClient googleCalendarClient;
    private final EventMapper eventMapper;
    private final UserContext userContext;
    private final ProjectRepository projectRepository;

    @Transactional
    @Override
    public EventOutputDto createEvent(CreateEventDto createEventDto, long projectId) {
        Project project = findProjectById(projectId);
        CalendarEventResponse eventCreationResponse = googleCalendarClient.createEvent(createEventDto);
        if (!eventCreationResponse.getStatus().equals("confirmed")) {
            throw new FailedRequestException("Creating event for project with id %d failed".formatted(projectId));
        }
        Event event = Event.builder()
                .title(eventCreationResponse.getSummary())
                .description(eventCreationResponse.getDescription())
                .status(EventStatus.PENDING)
                .creatorId(userContext.getUserId())
                .startsAt(eventCreationResponse.getStart().toLocalDateTime())
                .endsAt(eventCreationResponse.getEnd().toLocalDateTime())
                .project(project)
                .calendarEventId(eventCreationResponse.getId())
                .build();
        Event createdEvent = eventRepository.save(event);
        return eventMapper.toEventOutputDto(createdEvent);
    }

    @Override
    public EventOutputDto getEvent(long projectId, long eventId) {
        Project project = findProjectById(projectId);
        if (project.getEvents().stream()
                .noneMatch(event -> event.getId() == eventId)) {
            throw new IllegalArgumentException("Project with id %d doesn't contains event with id %d".formatted(projectId, eventId));
        }
        Event existingEvent = findEventById(eventId);
        CalendarEventResponse getEventResponse = null;
        try {
            getEventResponse = googleCalendarClient.getEvent(existingEvent.getCalendarEventId());
        } catch (Exception e) {
            System.out.println(1);
        }
        if (!getEventResponse.getStatus().equals("confirmed")) {
            throw new FailedRequestException("Creating event for project with id %d failed".formatted(projectId));
        }
        return eventMapper.toEventOutputDto(existingEvent);
    }

    @Transactional
    @Override
    public EventOutputDto deleteEvent(long projectId, long eventId) {
        Project project = findProjectById(projectId);
        if (project.getEvents().stream()
                .noneMatch(event -> event.getId() == eventId)) {
            throw new IllegalArgumentException("Project with id %d doesn't contains event with id %d".formatted(projectId, eventId));
        }
        Event existingEvent = findEventById(eventId);
        googleCalendarClient.deleteEvent(existingEvent.getCalendarEventId());
        eventRepository.delete(existingEvent);
        return eventMapper.toEventOutputDto(existingEvent);
    }

    public Project findProjectById(long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project with id %d doesn't exist".formatted(projectId)));
    }

    public Event findEventById(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id %d doesn't exist".formatted(eventId)));
    }
}