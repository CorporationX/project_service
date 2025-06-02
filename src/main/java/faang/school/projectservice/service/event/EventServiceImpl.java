package faang.school.projectservice.service.event;

import faang.school.projectservice.client.GoogleCalendarClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.event.CalendarEventResponse;
import faang.school.projectservice.dto.event.CreateEventDto;
import faang.school.projectservice.dto.event.EventOutputDto;
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

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final GoogleCalendarClient googleCalendarClient;
    private final EventMapper eventMapper;
    private final UserContext userContext;
    private final ProjectRepository projectRepository;

    @Override
    public EventOutputDto createEvent(CreateEventDto createEventDto, long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project with id %d doesn't exist".formatted(projectId)));
        CalendarEventResponse eventCreationResponse = googleCalendarClient.createEvent(createEventDto);
        Event event = Event.builder()
                .title(createEventDto.getSummary())
                .description(createEventDto.getDescription())
                .status(EventStatus.PENDING)
                .creatorId(userContext.getUserId())
                .startsAt(LocalDateTime.now().plusMonths(2))
                .endsAt(LocalDateTime.now().plusMonths(3))
                .project(project)
                .build();
        event = eventRepository.save(event);
        return eventMapper.toEventOutputDto(event);
    }
}