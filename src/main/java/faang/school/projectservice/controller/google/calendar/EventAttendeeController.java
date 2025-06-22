package faang.school.projectservice.controller.google.calendar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/google/calendars/acl")
@RequiredArgsConstructor
public class EventAttendeeController {

    public void addCreator() {
        log.info("Adding creator to Google Calendar ACL");
        // Logic to add a creator to the Google Calendar ACL would go here
    }

    public void addAttendee() {
        log.info("Adding attendee to Google Calendar ACL");
        // Logic to add an attendee to the Google Calendar ACL would go here
    }

    public void removeAttendee() {
        log.info("Removing attendee from Google Calendar ACL");
        // Logic to remove an attendee from the Google Calendar ACL would go here
    }

    public void removeCreator() {
        log.info("Removing creator from Google Calendar ACL");
        // Logic to retrieve the Google Calendar ACL would go here
    }
}