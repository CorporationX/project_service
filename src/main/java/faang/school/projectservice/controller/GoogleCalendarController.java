package faang.school.projectservice.controller;

import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.Event;
import faang.school.projectservice.dto.calendar.CalendarDTO;
import faang.school.projectservice.dto.calendar.CalendarRole;
import faang.school.projectservice.dto.calendar.CreateEventDTO;
import faang.school.projectservice.service.GoogleCalendarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/google-calendar")
@RequiredArgsConstructor
public class GoogleCalendarController {

    private final GoogleCalendarService googleCalendarService;

    @GetMapping("/calendars")
    public ResponseEntity<List<CalendarDTO>> getCalendars() {
        return ResponseEntity.ok(googleCalendarService.getCalendars());
    }

    @PostMapping("/calendars")
    public ResponseEntity<String> createCalendar(@RequestParam String name) {
        return ResponseEntity.ok(googleCalendarService.createCalendar(name));
    }

    @GetMapping("/calendars/{calendarId}")
    public ResponseEntity<String> getCalendar(@PathVariable String calendarId) {
        return ResponseEntity.ok(googleCalendarService.getCalendar(calendarId));
    }

    @PostMapping("/calendars/{calendarId}/events")
    public ResponseEntity<String> createEvent(
            @PathVariable String calendarId,
            @RequestBody @Valid CreateEventDTO createEventDTO) {

        String eventId = googleCalendarService.createEvent(calendarId, createEventDTO);
        return ResponseEntity.ok("Event created with ID: " + eventId);
    }

    @GetMapping("/calendars/{calendarId}/events")
    public ResponseEntity<List<Event>> getEvents(@PathVariable String calendarId) {
        return ResponseEntity.ok(googleCalendarService.getEvents(calendarId));
    }

    @GetMapping("/calendars/{calendarId}/events/{eventId}")
    public ResponseEntity<Event> getEventById(@PathVariable String calendarId,
                                              @PathVariable String eventId) {
        return ResponseEntity.ok(googleCalendarService.getEventById(calendarId, eventId));
    }

    @PutMapping("/calendars/{calendarId}/events/{eventId}")
    public ResponseEntity<String> updateEvent(
            @PathVariable String calendarId,
            @PathVariable String eventId,
            @RequestBody CreateEventDTO dto) {

        googleCalendarService.updateEvent(calendarId, eventId, dto);
        return ResponseEntity.ok("Event updated successfully.");
    }

    @DeleteMapping("/calendars/{calendarId}/events/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable String calendarId, @PathVariable String eventId) {
        googleCalendarService.deleteEvent(calendarId, eventId);
        return ResponseEntity.ok("Event deleted successfully.");
    }

    @PostMapping("/calendars/{calendarId}/add-user")
    public ResponseEntity<String> addCalendarAccess(@PathVariable String calendarId,
                                                    @RequestParam String userEmail,
                                                    @RequestParam CalendarRole role) {
        googleCalendarService.addCalendarAccess(calendarId, userEmail, role);
        return ResponseEntity.ok("User " + userEmail + " now has access to calendar " + calendarId);
    }

    @DeleteMapping("/calendars/{calendarId}/acl")
    public ResponseEntity<String> removeCalendarAccess(@PathVariable String calendarId,
                                                       @RequestParam String userEmail) {
        googleCalendarService.removeCalendarAccess(calendarId, userEmail);
        return ResponseEntity.ok("User " + userEmail + " has been removed from calendar "
                + calendarId);
    }

    @GetMapping("/calendars/{calendarId}/acl")
    public ResponseEntity<List<AclRule>> getCalendarAccessList(@PathVariable String calendarId) {
        return ResponseEntity.ok(googleCalendarService.getCalendarAccessList(calendarId));
    }
}

