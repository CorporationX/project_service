package faang.school.projectservice.controller.google_calendar;

import faang.school.projectservice.service.google_calendar.CalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Tag(name = "Google Calendars", description = "Operations for managing Google Calendars")
@RequiredArgsConstructor
@RequestMapping("/google-calendar/calendars")
@Slf4j
@RestController
public class CalendarController {
    private final CalendarService calendarService;

    @Operation(summary = "Create calendar", description = "Creates a new calendar in Google Calendar.")
    @PostMapping
    public ResponseEntity<String> createCalendar(@RequestParam String summary, @RequestParam String timeZone) throws IOException {
        String calendarId = calendarService.createCalendar(summary, timeZone);
        return ResponseEntity.status(HttpStatus.CREATED).body("Calendar created with ID: " + calendarId);
    }

    @Operation(summary = "Get calendar", description = "Retrieves a Google Calendar by its ID.")
    @GetMapping("/{calendarId}")
    public ResponseEntity<com.google.api.services.calendar.model.Calendar> getCalendar(@PathVariable String calendarId) throws IOException {
        com.google.api.services.calendar.model.Calendar calendar = calendarService.getCalendar(calendarId);
        return ResponseEntity.ok(calendar);
    }

    @Operation(summary = "Delete calendar", description = "Deletes a Google Calendar by its ID.")
    @DeleteMapping("/{calendarId}")
    public ResponseEntity<String> deleteCalendar(@PathVariable String calendarId) throws IOException {
        calendarService.deleteCalendar(calendarId);
        return ResponseEntity.ok("Calendar deleted");
    }
}