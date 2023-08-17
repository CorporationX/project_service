package faang.school.projectservice.service;

import faang.school.projectservice.config.calendar.GoogleCalendarConfig;
import faang.school.projectservice.dto.calendar.EventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class EventService {
    private final GoogleCalendarConfig calendarConfig;
    public EventDto createEvent(EventDto eventCalendarDto) {

    }
}
