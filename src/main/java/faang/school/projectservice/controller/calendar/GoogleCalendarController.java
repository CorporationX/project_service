package faang.school.projectservice.controller.calendar;

import faang.school.projectservice.service.calendar.GoogleCalendarService;
import faang.school.projectservice.service.calendar.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<String> createEvent(
            @RequestParam String summary,
            @RequestParam String description,
            @RequestParam String startDateTime,
            @RequestParam String endDateTime
    ) throws Exception {
        try {
            googleCalendarService.createEvent(summary, description, startDateTime, endDateTime);
            return ResponseEntity.ok("Event successfully created");
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Event creating interrupted with error " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> testConnection() {
        try {
            oAuthService.getCredentials("user@mail.net");
            return ResponseEntity.ok("Connecting to Google Calendar API successfully done");
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Connection failed " + e.getMessage());
        }
    }
}

