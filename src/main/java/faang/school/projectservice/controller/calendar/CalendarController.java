package faang.school.projectservice.controller.calendar;

import com.google.api.client.auth.oauth2.Credential;
import faang.school.projectservice.dto.calendar.ACLDto;
import faang.school.projectservice.dto.calendar.CalendarDto;
import faang.school.projectservice.dto.calendar.CalendarEventDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.CalendarService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@Validated
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class CalendarController {
    private final CalendarService calendarService;


    @GetMapping("/calendars/auth")
    @ResponseStatus(HttpStatus.FOUND)
    public void getAccessCode(HttpServletResponse response) throws IOException {
        URL url = calendarService.getAccessCode();
        response.sendRedirect(url.toString());
    }

    @PostMapping("/{projectId}/calendars/auth")
    public String setProjectCredentials(@PathVariable long projectId, @NotBlank @RequestParam String code) {
        Credential credential = calendarService.auth(projectId, code);
        return credential.getAccessToken();
    }

    @PostMapping("/{projectId}/calendars/events")
    public String createEvent(@PathVariable long projectId,
                              @RequestParam String calendarId,
                              @Valid @RequestBody CalendarEventDto eventDto) {
        validateEventDates(eventDto);
        CalendarEventDto createdEvent = calendarService.createEvent(projectId, calendarId, eventDto);
        return createdEvent.getId();
    }

    @GetMapping("/{projectId}/calendars/events")
    public List<CalendarEventDto> getEvents(@PathVariable long projectId, @RequestParam String calendarId) {
        return calendarService.getEvents(projectId, calendarId);
    }

    @DeleteMapping("/{projectId}/calendars/events/{eventId}")
    public void deleteEvent(@PathVariable long projectId,
                            @RequestParam String calendarId,
                            @PathVariable String eventId) {
        calendarService.deleteEvent(projectId, calendarId, eventId);
    }

    @PostMapping("/{projectId}/calendars")
    public CalendarDto createCalendar(@PathVariable long projectId, @Valid @RequestBody CalendarDto calendarDto) {
        return calendarService.createCalendar(projectId, calendarDto);
    }

    @PutMapping("/{projectId}/calendars/events/{eventId}")
    public String updateEvent(@PathVariable long projectId,
                              @RequestParam String calendarId,
                              @PathVariable String eventId,
                              @Valid @RequestBody CalendarEventDto eventDto) {
        calendarService.updateEvent(projectId, calendarId, eventId, eventDto);
        return "Event updated successfully.";
    }


    @GetMapping("/{projectId}/calendars")
    public CalendarDto getCalendar(@PathVariable long projectId, @RequestParam String calendarId) {
        return calendarService.getCalendar(projectId, calendarId);
    }

    @PostMapping("/{projectId}/calendars/acl")
    public ACLDto createAclRule(@PathVariable long projectId,
                                @RequestParam String calendarId,
                                @Valid @RequestBody ACLDto aclDto) {
        return calendarService.createAcl(projectId, calendarId, aclDto);
    }

    @GetMapping("/{projectId}/calendars/acl")
    public List<ACLDto> listAclRules(@PathVariable long projectId,
                                     @RequestParam String calendarId) {
        return calendarService.listAcl(projectId, calendarId);
    }

    @DeleteMapping("/{projectId}/calendars/acl")
    public void deleteAclRule(@PathVariable long projectId,
                              @RequestParam String calendarId,
                              @RequestParam long aclId) {
        calendarService.deleteAcl(projectId, calendarId, aclId);
    }

    private static void validateEventDates(CalendarEventDto eventDto) {
        if (eventDto.getEndTime().isBefore(eventDto.getStartTime())) {
            throw new DataValidationException("End time must be after start time.");
        }
    }
}
