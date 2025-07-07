package faang.school.projectservice.controller.google.calendar;

import faang.school.projectservice.dto.google.calendar.GoogleCalendarEventDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.google.calendar.GoogleCalendarService;
import faang.school.projectservice.validation.dto.DtoValidatorUtils;
import faang.school.projectservice.validation.google.calendar.ValidationGroups;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@Validated
@RestController
@RequestMapping("/api/v1/google/calendar/events")
@RequiredArgsConstructor
public class GoogleCalendarEventController {

    private final GoogleCalendarService calendarService;

    @PostMapping
    public GoogleCalendarEventDto createEvent(@RequestBody @Validated(ValidationGroups.OnCreate.class)
                                                  GoogleCalendarEventDto eventDto)  {
        log.info("Creating Google Calendar event: {}", eventDto);
        return calendarService.createEvent(eventDto);
    }

    @PutMapping("/{id}")
    public GoogleCalendarEventDto updateEvent(@RequestBody @Validated(ValidationGroups.OnUpdate.class)
                                                  GoogleCalendarEventDto eventDto,
                                                  @PathVariable @Positive Long id)  {
        if (DtoValidatorUtils.isAllFieldsNull(eventDto)) {
            throw new DataValidationException("Event must not be null");
        }

        log.info("Updating Google Calendar event with id {} : {}", id, eventDto);
        return calendarService.updateEvent(eventDto, id);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable @Positive Long eventId)  {
        log.info("Deleting Google Calendar event with ID: {}", eventId);
        calendarService.deleteEvent(eventId);
        return ResponseEntity.ok("Event deleted successfully");
    }

    @GetMapping("/{eventId}")
    public GoogleCalendarEventDto getEvent(@PathVariable @Positive Long eventId) {
        log.info("Retrieving Google Calendar event with ID: {}", eventId);
        return calendarService.getEvent(eventId);
    }

    @GetMapping("/all")
    public List<GoogleCalendarEventDto> getAllEvents() {
        log.info("Retrieving all Google Calendar events");
        return calendarService.getAllEvents();
    }
}
