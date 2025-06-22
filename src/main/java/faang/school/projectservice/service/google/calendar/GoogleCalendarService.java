package faang.school.projectservice.service.google.calendar;

import faang.school.projectservice.dto.google.calendar.GoogleCalendarEventDto;
import faang.school.projectservice.mapper.GoogleCalendarEventMapper;
import faang.school.projectservice.model.google.calendar.GoogleCalendarEvent;
import faang.school.projectservice.repository.GoogleCalendarEventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import com.google.api.services.calendar.Calendar;

import java.io.IOException;

@Log4j2
@Service
@AllArgsConstructor
public class GoogleCalendarService {

    private final GoogleCalendarEventMapper eventMapper;
    private final GoogleCalendarEventRepository eventRepository;
    private final GoogleOAuth googleOAuth;

    public GoogleCalendarEventDto createEvent(GoogleCalendarEventDto googleCalendarEventDto) throws IOException {

        Calendar calendarService = googleOAuth.init();

        GoogleCalendarEvent eventEntity = GoogleCalendarEvent.builder()
                .title(googleCalendarEventDto.getTitle())
                .description(googleCalendarEventDto.getDescription())
                .startTime(googleCalendarEventDto.getStartTime())
                .endTime(googleCalendarEventDto.getEndTime())
                .build();

        return eventMapper.toDto(eventRepository.save(eventEntity));
    }

    public void updateEvent(GoogleCalendarEventDto googleCalendarEventDto) {
        log.info("Updating Google Calendar event: {}", googleCalendarEventDto);
        eventRepository.save(eventMapper.toEntity(googleCalendarEventDto));
    }

    public void deleteEvent(Long eventId) {
        log.info("Deleting Google Calendar event with ID: {}", eventId);
        eventRepository.deleteById(eventId);
    }

    public GoogleCalendarEventDto getEvent(Long eventId) {
        log.info("Retrieving Google Calendar event with ID: {}", eventId);
        return eventMapper.toDto(eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId)));
    }
}
