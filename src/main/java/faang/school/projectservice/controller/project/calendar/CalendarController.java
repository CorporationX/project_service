package faang.school.projectservice.controller.project.calendar;

import faang.school.projectservice.dto.project.calendar.CalendarDto;
import faang.school.projectservice.dto.project.calendar.EventDto;
import faang.school.projectservice.service.project.calendar.CalendarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.util.List;

@Validated
@RestController
@RequestMapping("projects")
@RequiredArgsConstructor
public class CalendarController {
    private final CalendarService calendarService;


    @GetMapping("calendars/auth")
    public URL getAuthorizationUrl() {
        return calendarService.getAuthUrl();
    }

    @PostMapping("{projectId}/calendars/auth")
    public void setCredentials(@PathVariable long projectId, @RequestParam String code) {
        calendarService.auth(projectId, code);
    }

    @PostMapping("{projectId}/calendars/events")
    public EventDto createEvent(@PathVariable long projectId,
                                @RequestParam String calendarId,
                                @Valid @RequestBody EventDto eventDto) {
        return calendarService.createEvent(projectId, calendarId, eventDto);
    }

    @GetMapping("{projectId}/calendars/events")
    public List<EventDto> getEvents(@PathVariable long projectId, @RequestParam String calendarId) {
        return calendarService.getEvents(projectId, calendarId);
    }

    @DeleteMapping("{projectId}/calendars/events/{eventId}")
    public void deleteEvent(@PathVariable long projectId,
                            @RequestParam String calendarId,
                            @PathVariable String eventId) {
        calendarService.deleteEvent(projectId, calendarId, eventId);
    }

    @PostMapping("{projectId}/calendars")
    public CalendarDto createCalendar(@PathVariable long projectId, @Valid @RequestBody CalendarDto calendarDto) {
        return calendarService.createCalendar(projectId, calendarDto);
    }

    @GetMapping("{projectId}/calendars")
    public CalendarDto getCalendar(@PathVariable long projectId, @RequestParam String calendarId) {
        return calendarService.getCalendar(projectId, calendarId);
    }
}
