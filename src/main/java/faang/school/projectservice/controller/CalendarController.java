package faang.school.projectservice.controller;

import com.google.api.services.calendar.model.AclRule;
import faang.school.projectservice.dto.calendar.CalendarDto;
import faang.school.projectservice.dto.calendar.EventDto;
import faang.school.projectservice.service.CalendarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CalendarDto createCalendarForProject(@RequestParam Long projectId,
                                                @RequestBody @Valid CalendarDto calendarDto) {
        return calendarService.createCalendarForProject(projectId, calendarDto);
    }


    @PostMapping("/{calendarId}/event")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto createEvent(@PathVariable String calendarId,
                                @RequestBody @Valid EventDto eventDto) {
        return calendarService.createEvent(calendarId, eventDto);
    }


    @GetMapping("/{calendarId}/event/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto getEvent(@PathVariable String calendarId,
                             @PathVariable String eventId) {
        return calendarService.getEvent(calendarId, eventId);
    }

    @DeleteMapping("/{calendarId}/event/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteEvent(@PathVariable String calendarId,
                            @PathVariable String eventId) {
        calendarService.deleteEvent(calendarId, eventId);
    }

    @PostMapping("/{calendarId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public List<EventDto> createEvents(@PathVariable String calendarId,
                                       @RequestBody List<EventDto> eventDtoList) {
        return calendarService.createEvents(calendarId, eventDtoList);
    }

    @GetMapping("/{calendarId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventDto> getEvents(@PathVariable String calendarId) {
        return calendarService.getEvents(calendarId);
    }


    @PostMapping("/{calendarId}/acl")
    @ResponseStatus(HttpStatus.CREATED)
    public AclRule addAclRule(@PathVariable String calendarId,
                              @RequestParam String email,
                              @RequestParam String role,
                              @RequestParam String scopeType) {
        return calendarService.addAclRule(calendarId, email, role, scopeType);
    }


    @GetMapping("/{calendarId}/acl")
    @ResponseStatus(HttpStatus.OK)
    public List<AclRule> getAclRules(@PathVariable String calendarId) {
        return calendarService.getAclRules(calendarId);
    }


    @DeleteMapping("/{calendarId}/acl/{ruleId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAclRule(@PathVariable String calendarId,
                              @PathVariable String ruleId) {
        calendarService.deleteAclRule(calendarId, ruleId);
    }
}