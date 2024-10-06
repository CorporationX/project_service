package faang.school.projectservice.controller.calendar;

import faang.school.projectservice.dto.google.calendar.CalendarEventDto;
import faang.school.projectservice.google.calendar.GoogleCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/calendar")
public class GoogleCalendarController {

    private final GoogleCalendarService googleCalendarService;

    @PostMapping("/event")
    public ResponseEntity<Long> createEvent(@RequestBody CalendarEventDto dto) {
        long eventId = googleCalendarService.createEvent(dto);
        return new ResponseEntity<>(eventId, HttpStatus.CREATED);
    }

    @PutMapping("/event")
    public ResponseEntity<Void> updateEvent(@RequestBody CalendarEventDto dto) {
        googleCalendarService.update(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/event/{googleEventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable long googleEventId) {
        googleCalendarService.delete(googleEventId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
