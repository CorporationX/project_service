package faang.school.projectservice.controller.calendar;

import faang.school.projectservice.service.calendar.GoogleCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

       /**
     * Эндпоинт для создания события в Google Calendar
     */
    @PostMapping("/event")
    public ResponseEntity<String> createEvent(
            @RequestParam String summary,
            @RequestParam String description,
            @RequestParam String startDateTime, // Формат: "2025-01-29T10:00:00Z"
            @RequestParam String endDateTime    // Формат: "2025-01-29T11:00:00Z"
    ) {
        try {
            googleCalendarService.createEvent(summary, description, startDateTime, endDateTime);
            return ResponseEntity.ok("Событие успешно создано!");
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Ошибка при создании события: " + e.getMessage());
        }
    }

    /**
     * Эндпоинт для проверки соединения с Google Calendar API
     */
    @GetMapping("/test")
    public ResponseEntity<String> testConnection() {
        try {
            googleCalendarService.getCredentials();
            return ResponseEntity.ok("Подключение к Google Calendar API успешно!");
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Ошибка подключения: " + e.getMessage());
        }
    }
}

