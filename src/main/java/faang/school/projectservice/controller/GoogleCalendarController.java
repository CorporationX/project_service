package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.service.google.GoogleCalendarService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/calendar")
@AllArgsConstructor
public class GoogleCalendarController {
    private final GoogleCalendarService googleCalendarService;
    private final UserContext userContext;

    @GetMapping("/authorization")
    public String authorize(@RequestParam(name = "code") String authorizationCode, @RequestParam String state) throws IOException {
        Long userId = Long.parseLong(state.split("-")[0]);
        return googleCalendarService.authorizeUser(authorizationCode, userId);
    }

    @PostMapping("/{id}")
    public String createEvent(@PathVariable(name = "id") Long eventId) throws IOException {
        Long userId = userContext.getUserId();
        return googleCalendarService.createEvent(userId, eventId);
    }
}