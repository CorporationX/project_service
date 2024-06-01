package faang.school.projectservice.controller.calendar;

import faang.school.projectservice.service.calendar.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;


@RestController
@RequestMapping("events")
@RequiredArgsConstructor
public class CalendarController {
    private final CalendarService calendarService;


    @GetMapping("/auth")
    public URL getAuthorizationUrl() {
        return calendarService.getAuthorizationUrl();
    }

    @PostMapping("/auth/projects/{projectId}")
    public void setCredentials(@PathVariable long projectId, @RequestParam String code) {
        calendarService.auth(projectId, code);
    }


    @GetMapping("/projects/{projectId}")
    public void getUpcomingEvents(@PathVariable long projectId) {
        calendarService.getUpcomingEvents(projectId);
    }


}
