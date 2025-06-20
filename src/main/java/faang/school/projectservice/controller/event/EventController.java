package faang.school.projectservice.controller.event;

import faang.school.projectservice.dto.event.CreateEventDto;
import faang.school.projectservice.dto.event.EventOutputDto;
import faang.school.projectservice.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "EventController", description = "Provides several operations, related to user events")
public class EventController {
    private final EventService eventService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Creating event", description = "Provides ability to create new event")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Project doesn't exist"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public EventOutputDto createEvent(@PathVariable @Parameter(description = "Event project id", required = true) long projectId,
                                      @Valid @RequestBody @Parameter(description = "New event", required = true) CreateEventDto createEventDto) {
        log.debug("Creating event for project with id {} - Started", projectId);
        EventOutputDto createdEvent = eventService.createEvent(createEventDto, projectId);
        log.info("Creating event for project with id {} - Finished", projectId);
        return createdEvent;
    }

    @GetMapping("/{eventId}")
    @Operation(summary = "Getting event", description = "Provides ability to get event by id")
    @ApiResponse(responseCode = "404", description = "Project doesn't exist")
    public EventOutputDto getEvent(@PathVariable @Parameter(description = "Event project id", required = true) long projectId,
                                   @PathVariable @Parameter(description = "Event id", required = true) long eventId) {
        log.debug("Getting event with id {} for project with id {} - Started", eventId, projectId);
        EventOutputDto foundEvent = eventService.getEvent(projectId, eventId);
        log.info("Getting event with id {} for project with id {} - Finished", eventId, projectId);
        return foundEvent;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{eventId}")
    @Operation(summary = "Deleting event", description = "Enables ability to delete event by id")
    @ApiResponse(responseCode = "404", description = "Project doesn't exist")
    public EventOutputDto deleteEvent(@PathVariable @Parameter(description = "Event project id", required = true) long projectId,
                                      @PathVariable @Parameter(description = "Event id", required = true) long eventId) {
        log.debug("Deleting event with id {} for project with id {} - Started", eventId, projectId);
        EventOutputDto deletedEvent = eventService.deleteEvent(projectId, eventId);
        log.info("Deleting event with id {} for project with id {} - Finished", eventId, projectId);
        return deletedEvent;
    }
}