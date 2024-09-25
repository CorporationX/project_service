package faang.school.projectservice.controller;

import faang.school.projectservice.service.calendar.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/calendar")
public class CalendarController {
    private final CalendarService service;

    @PutMapping("/event")
    public void createEvent() {
    }

    @PutMapping("/events")
    public void viewEvents() throws GeneralSecurityException, IOException {
        service.view();
    }
}
