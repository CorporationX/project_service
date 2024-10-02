package faang.school.projectservice.controller.google_calendar;

import faang.school.projectservice.dto.client.EventDtoForGoogleCalendar;
import faang.school.projectservice.service.google_calendar.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@Tag(name = "Google Calendar Events", description = "Operations for managing Google Calendar events")
@RestController
@RequiredArgsConstructor
@RequestMapping("/google-calendar/events")
public class EventController {
    private final EventService eventService;

    @Operation(summary = "Create event", description = "Creates an event in Google Calendar based on an existing event in the system.")
    @PostMapping("/{eventId}")
    public ResponseEntity<String> createEvent(
            @PathVariable Long eventId,
            @RequestParam(required = false) String calendarId) throws IOException {
        String googleEventId = eventService.createEventInGoogleCalendar(eventId, calendarId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Event created with ID: " + googleEventId);
    }

    @Operation(summary = "Get event", description = "Retrieves an event from Google Calendar by its identifier.")
    @GetMapping("/{eventId}")
    public ResponseEntity<EventDtoForGoogleCalendar> getEvent(@PathVariable Long eventId) throws IOException {
        EventDtoForGoogleCalendar event = eventService.getEventFromGoogleCalendar(eventId);
        return ResponseEntity.ok(event);
    }

    @Operation(summary = "Delete event", description = "Deletes an event from Google Calendar by its identifier.")
    @DeleteMapping("/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long eventId) throws IOException {
        eventService.deleteEventFromGoogleCalendar(eventId);
        return ResponseEntity.ok("Event deleted");
    }
}