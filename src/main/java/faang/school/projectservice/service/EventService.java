package faang.school.projectservice.service;

import faang.school.projectservice.dto.event.CreateEventDto;
import faang.school.projectservice.dto.event.EventOutputDto;

public interface EventService {
    EventOutputDto createEvent(CreateEventDto createEventDto, long projectId);

    EventOutputDto getEvent(long projectId, long eventId);

    EventOutputDto deleteEvent(long projectId, long eventId);
}
