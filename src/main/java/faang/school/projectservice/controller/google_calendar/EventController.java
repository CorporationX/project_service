package faang.school.projectservice.controller.google_calendar;

import faang.school.projectservice.dto.client.EventDtoForGoogleCalendar;
import faang.school.projectservice.service.google_calendar.EventService;
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

import java.io.IOException;

@Tag(name = "Google Calendar Events", description = "Операции для управления мероприятиями Google Calendar")
@RestController
@RequestMapping("/google-calendar/events")
@Slf4j
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @Operation(summary = "Создать мероприятие", description = "Создаёт мероприятие в Google Calendar на основе существующего мероприятия в системе.")
    @PostMapping("/{eventId}")
    public ResponseEntity<String> createEvent(
            @PathVariable Long eventId,
            @RequestParam(required = false) String calendarId) throws IOException {
        log.info("Запрос на создание мероприятия с eventId '{}'", eventId);
        String googleEventId = eventService.createEventInGoogleCalendar(eventId, calendarId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Мероприятие создано с ID: " + googleEventId);
    }

    @Operation(summary = "Получить мероприятие", description = "Получает мероприятие из Google Calendar по его идентификатору.")
    @GetMapping("/{eventId}")
    public ResponseEntity<EventDtoForGoogleCalendar> getEvent(@PathVariable Long eventId) throws IOException {
        log.info("Запрос на получение мероприятия с eventId '{}'", eventId);
        EventDtoForGoogleCalendar event = eventService.getEventFromGoogleCalendar(eventId);
        return ResponseEntity.ok(event);
    }

    @Operation(summary = "Удалить мероприятие", description = "Удаляет мероприятие из Google Calendar по его идентификатору.")
    @DeleteMapping("/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long eventId) throws IOException {
        log.info("Запрос на удаление мероприятия с eventId '{}'", eventId);
        eventService.deleteEventFromGoogleCalendar(eventId);
        return ResponseEntity.ok("Мероприятие удалено");
    }
}