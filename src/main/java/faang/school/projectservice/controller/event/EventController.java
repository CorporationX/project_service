package faang.school.projectservice.controller.event;

import faang.school.projectservice.dto.event.CreateEventDto;
import faang.school.projectservice.dto.event.EventOutputDto;
import faang.school.projectservice.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects/{projectId}/events")
public class EventController {
    private final EventService eventService;

    @PostMapping
    public EventOutputDto createEvent(@PathVariable long projectId, @Valid @RequestBody CreateEventDto createEventDto) {
        log.debug("Creating event for project with id {} - Started", projectId);
        EventOutputDto createdEvent = eventService.createEvent(createEventDto, projectId);
        log.info("Creating event for project with id {} - Finished", projectId);
        return createdEvent;
    }
}