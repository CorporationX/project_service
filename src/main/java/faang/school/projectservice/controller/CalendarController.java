package faang.school.projectservice.controller;

import com.google.api.services.calendar.model.Event;
import faang.school.projectservice.dto.EventDto;
import faang.school.projectservice.service.calendar.CalendarService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/calendar")
@Validated
public class CalendarController {
    private final CalendarService service;

    @PutMapping("/event/{calendarId}/{eventId}")
    public void addEventToCalendar(@PathVariable("calendarId") @NotBlank String calendarId,
                                   @PathVariable("eventId") @Positive long eventId) throws GeneralSecurityException, IOException {
        service.addEventToCalendar(eventId, calendarId);
    }

    @GetMapping("/events/{calendarId}")
    public List<Event> getEvents(@PathVariable("calendarId") @NotBlank String calendarId) throws GeneralSecurityException, IOException {
        return service.getEvents(calendarId);
    }

    @PutMapping("/event/update/{calendarId}")
    public void updateEvent(@PathVariable("calendarId") @NotBlank String calendarId,
                            @RequestBody EventDto eventDto) throws GeneralSecurityException, IOException {
        service.update(eventDto, calendarId);
    }

    @PutMapping("/event/update/{calendarId}/{eventId}")
    public void updateEvent(@PathVariable("calendarId") @NotBlank String calendarId,
                            @PathVariable("eventId") @Positive long eventId) throws GeneralSecurityException, IOException {
        service.update(eventId, calendarId);
    }
}
