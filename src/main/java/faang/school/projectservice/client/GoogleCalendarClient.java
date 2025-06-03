package faang.school.projectservice.client;

import faang.school.projectservice.client.config.GoogleFeignConfig;
import faang.school.projectservice.dto.event.CalendarEventResponse;
import faang.school.projectservice.dto.event.CreateEventDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@FeignClient(name = "google-calendar",  url = "${google.api.url}", configuration = GoogleFeignConfig.class)
public interface GoogleCalendarClient {
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/calendar/v3/calendars/${google.calendar.id}/events")
    CalendarEventResponse createEvent(@RequestBody CreateEventDto createEventDto);

    @GetMapping("/calendar/v3/calendars/${google.calendar.id}/events/{eventId}")
    CalendarEventResponse getEvent(@PathVariable String eventId);

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/calendar/v3/calendars/${google.calendar.id}/events/{eventId}")
    void deleteEvent(@PathVariable String eventId);
}