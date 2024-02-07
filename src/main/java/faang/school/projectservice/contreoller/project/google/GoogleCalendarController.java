package faang.school.projectservice.contreoller.project.google;

import faang.school.projectservice.dto.google.EventCalendarDto;
import faang.school.projectservice.service.project.google.GoogleCalendarService;
import jdk.jfr.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class GoogleCalendarController {
    private final GoogleCalendarService googleCalendarService;

    @PostMapping("/{disc}")
    public EventCalendarDto createEvent(@PathVariable long disc) throws GeneralSecurityException, IOException {
        return googleCalendarService.createEvent(disc);
    }
}
