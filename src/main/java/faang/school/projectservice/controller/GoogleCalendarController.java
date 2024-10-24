package faang.school.projectservice.controller;

import faang.school.projectservice.model.dto.CalendarEventDto;
import faang.school.projectservice.service.GoogleCalendarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/calendar")
public class GoogleCalendarController {

    private final GoogleCalendarService googleCalendarService;

    @PostMapping("/event")
    public CalendarEventDto createEvent(@Valid @RequestBody CalendarEventDto dto) throws GeneralSecurityException, IOException {
        return googleCalendarService.createEvent(dto);
    }

    @PutMapping("/event")
    public CalendarEventDto updateEvent(@Valid @RequestBody CalendarEventDto dto) throws GeneralSecurityException, IOException {
        return googleCalendarService.update(dto);
    }

    @DeleteMapping("/event/{calendarEventId}")
    public void deleteEvent(@PathVariable String calendarEventId) throws GeneralSecurityException, IOException {
        googleCalendarService.delete(calendarEventId);
    }
}
