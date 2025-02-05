package faang.school.projectservice.controller.calendar;

import com.google.api.services.calendar.model.Event;
import faang.school.projectservice.dto.calendar.CalendarEventDto;
import faang.school.projectservice.service.calendar.AuthService;
import faang.school.projectservice.service.calendar.GoogleCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RequiredArgsConstructor
@RequestMapping("/api/calendar")
@RestController
public class GoogleCalendarController {
    private final GoogleCalendarService googleCalendarService;
    private final AuthService oAuthService;

    @PostMapping("/event")
    public ResponseEntity<Event> createEvent(@RequestBody CalendarEventDto dto) throws Exception {
        try {
            Event event = googleCalendarService.createEvent(dto);
            return ResponseEntity.ok(event);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("{creatorId}/{calendarId}/event/{eventId}")
    public ResponseEntity<Event> getCalendarEvent(
            @PathVariable("calendarId") String calendarId,
            @PathVariable("eventId") String eventId,
            @PathVariable("creatorId") long creatorId) {
        try {
            Event event = googleCalendarService.getEvent(calendarId, eventId, creatorId);
            return ResponseEntity.ok(event);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("{creatorId}/{calendarId}/event/{eventId}")
    public ResponseEntity<Void> deleteCalendarEvent(
            @PathVariable("calendarId") String calendarId,
            @PathVariable("eventId") String eventId,
            @PathVariable("creatorId") long creatorId) {
        try {
            googleCalendarService.deleteEvent(calendarId, eventId, creatorId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

        @GetMapping("/test")
        public ResponseEntity<String> testConnection () {
            try {
                oAuthService.getCredentials("user@mail.net");
                return ResponseEntity.ok("Connecting to Google Calendar API successfully done");
            } catch (IOException | GeneralSecurityException e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body("Connection failed " + e.getMessage());
            }
        }
    }

