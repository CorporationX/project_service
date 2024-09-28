package faang.school.projectservice.controller.google_calendar;

import faang.school.projectservice.service.google_calendar.CalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Google Calendars", description = "Операции для управления календарями Google Calendar")
@RestController
@RequestMapping("/google-calendar/calendars")
@Slf4j
public class CalendarController {

    private final CalendarService calendarService;

    @Autowired
    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @Operation(summary = "Создать календарь", description = "Создаёт новый календарь в Google Calendar.")
    @PostMapping
    public ResponseEntity<String> createCalendar(@RequestParam String summary, @RequestParam String timeZone) {
        log.info("Запрос на создание календаря с summary '{}' и timeZone '{}'", summary, timeZone);
        String calendarId = calendarService.createCalendar(summary, timeZone);
        return ResponseEntity.status(HttpStatus.CREATED).body("Календарь создан с ID: " + calendarId);
    }

    @Operation(summary = "Получить календарь", description = "Получает календарь Google Calendar по его идентификатору.")
    @GetMapping("/{calendarId}")
    public ResponseEntity<com.google.api.services.calendar.model.Calendar> getCalendar(@PathVariable String calendarId) {
        log.info("Запрос на получение календаря с ID '{}'", calendarId);
        com.google.api.services.calendar.model.Calendar calendar = calendarService.getCalendar(calendarId);
        return ResponseEntity.ok(calendar);
    }

    @Operation(summary = "Удалить календарь", description = "Удаляет календарь из Google Calendar по его идентификатору.")
    @DeleteMapping("/{calendarId}")
    public ResponseEntity<String> deleteCalendar(@PathVariable String calendarId) {
        log.info("Запрос на удаление календаря с ID '{}'", calendarId);
        calendarService.deleteCalendar(calendarId);
        return ResponseEntity.ok("Календарь удален");
    }
}
