package faang.school.projectservice.controller.event;

import faang.school.projectservice.dto.event.CreateEventDto;
import faang.school.projectservice.dto.event.EventOutputDto;
import faang.school.projectservice.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects/{projectId}/events")
public class EventController {
    private final EventService eventService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public EventOutputDto createEvent(@PathVariable long projectId, @Valid @RequestBody CreateEventDto createEventDto) {
        log.debug("Creating event for project with id {} - Started", projectId);
        EventOutputDto createdEvent = eventService.createEvent(createEventDto, projectId);
        log.info("Creating event for project with id {} - Finished", projectId);
        return createdEvent;
    }

    @GetMapping("/{eventId}")
    public EventOutputDto getEvent(@PathVariable long projectId, @PathVariable long eventId) {
        log.debug("Getting event with id {} for project with id {} - Started", eventId, projectId);
        EventOutputDto foundEvent = eventService.getEvent(projectId, eventId);
        log.info("Getting event with id {} for project with id {} - Finished", eventId, projectId);
        return foundEvent;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{eventId}")
    public EventOutputDto deleteEvent(@PathVariable long projectId, @PathVariable long eventId) {
        log.debug("Deleting event with id {} for project with id {} - Started", eventId, projectId);
        EventOutputDto deletedEvent = eventService.deleteEvent(projectId, eventId);
        log.info("Deleting event with id {} for project with id {} - Finished", eventId, projectId);
        return deletedEvent;
    }
}