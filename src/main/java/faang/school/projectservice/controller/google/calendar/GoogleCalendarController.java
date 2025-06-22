package faang.school.projectservice.controller.google.calendar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Validated
@Controller
@RequestMapping("/api/v1/google/calendars")
@RequiredArgsConstructor
public class GoogleCalendarController {

    public void getCalendarByProjectId() {
        log.info("Retrieving Google Calendar by Project ID");
        // Logic to retrieve a Google Calendar by project ID would go here
    }

    public void getCalendarsByUserId() {
        log.info("Retrieving Google Calendar by User ID");
        // Logic to retrieve a Google Calendar by user ID would go here
    }
}
