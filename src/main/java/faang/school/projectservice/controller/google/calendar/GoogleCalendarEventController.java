package faang.school.projectservice.controller.google.calendar;

import faang.school.projectservice.dto.google.calendar.GoogleCalendarEventDto;
import faang.school.projectservice.service.google.calendar.GoogleCalendarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Log4j2
@Validated
@RestController
@RequestMapping("/api/v1/google/calendar/events")
@RequiredArgsConstructor
public class GoogleCalendarEventController {

    private final GoogleCalendarService calendarService;

    @PostMapping
    public GoogleCalendarEventDto createEvent(@RequestBody @Valid GoogleCalendarEventDto eventDto) throws IOException {
        log.info("Creating Google Calendar event: {}", eventDto);
        return calendarService.createEvent(eventDto);
    }

    @PutMapping
    public void updateEvent(GoogleCalendarEventDto eventDto) {
        log.info("Updating Google Calendar event: {}", eventDto);
        calendarService.updateEvent(eventDto);
    }

    @DeleteMapping
    public void deleteEvent(Long eventId) {
        log.info("Deleting Google Calendar event with ID: {}", eventId);
        calendarService.deleteEvent(eventId);
    }

    @GetMapping
    public void getEvent(Long eventId) {
        log.info("Retrieving Google Calendar event with ID: {}", eventId);
        calendarService.getEvent(eventId);
    }
}
