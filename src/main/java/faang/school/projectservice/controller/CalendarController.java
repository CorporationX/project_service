package faang.school.projectservice.controller;

import faang.school.projectservice.service.calendar.CalendarService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/calendar")
@Validated
public class CalendarController {
    private final CalendarService service;

    @PutMapping("/event/{calendarId}/{eventId}")
    public void addEventToCalendar(@PathVariable("calendarId") @Positive String calendarId,
            @PathVariable("eventId") @Positive long eventId) throws GeneralSecurityException, IOException {
        service.addEventToCalendar(eventId, calendarId);
    }

    @GetMapping("/events")
    public void viewEvents() throws GeneralSecurityException, IOException {
        service.view();
    }
}
