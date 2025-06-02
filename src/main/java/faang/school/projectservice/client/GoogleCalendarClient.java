package faang.school.projectservice.client;

import faang.school.projectservice.client.config.GoogleFeignConfig;
import faang.school.projectservice.dto.event.CalendarEventResponse;
import faang.school.projectservice.dto.event.CreateEventDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "google-calendar",  url = "${google.api.url}", configuration = GoogleFeignConfig.class)
public interface GoogleCalendarClient {
    @PostMapping("/calendar/v3/calendars/${google.calendar.id}/events")
    CalendarEventResponse createEvent(@RequestBody CreateEventDto createEventDto);
}