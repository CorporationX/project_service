package faang.school.projectservice.service.google.calendar;

import com.google.api.services.calendar.model.Event;
import faang.school.projectservice.config.google.calendar.GoogleCalendarConfig;
import faang.school.projectservice.dto.google.calendar.GoogleCalendarEventDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EventNotFoundException;
import faang.school.projectservice.exception.CalendarApiException;
import faang.school.projectservice.mapper.google.calendar.EventCreateMapper;
import faang.school.projectservice.mapper.google.calendar.EventUpdateMapper;
import faang.school.projectservice.mapper.google.calendar.GoogleEventMapper;
import faang.school.projectservice.model.google.calendar.GoogleCalendarEvent;
import faang.school.projectservice.repository.GoogleCalendarEventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import com.google.api.services.calendar.Calendar;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Service
@AllArgsConstructor
public class GoogleCalendarService {

    private static final String CALENDAR_ID = "primary";
    private final Calendar calendarService;

    private final EventCreateMapper eventCreateMapper;
    private final GoogleCalendarEventRepository eventRepository;
    private final GoogleCalendarEventRepository googleCalendarEventRepository;
    private final EventUpdateMapper eventUpdateMapper;
    private final GoogleEventMapper googleEventMapper;

    public GoogleCalendarEventDto createEvent(GoogleCalendarEventDto googleCalendarEventDto) {
        try {

            Event googleEvent = googleEventMapper.toGoogleEvent(googleCalendarEventDto);

            Event createEventInCalendar = calendarService.events()
                    .insert(CALENDAR_ID, googleEvent)
                    .execute();

            GoogleCalendarEvent eventEntity = googleEventMapper.toEntity(createEventInCalendar);

            return eventCreateMapper.toDto(eventRepository.save(eventEntity));
        } catch (IOException e) {
            log.error("Error creating Google Calendar event: {}", e.getMessage());
            throw new CalendarApiException("Failed to create Google Calendar event", e);
        }
    }

    public GoogleCalendarEventDto updateEvent(GoogleCalendarEventDto googleCalendarEventDto,
                                              Long eventId) {
        try {
            log.info("Updating Google Calendar event: {}", googleCalendarEventDto);

            GoogleCalendarEvent googleCalendarEvent = googleCalendarEventRepository.findById(eventId)
                    .orElseThrow(() -> new EventNotFoundException("Event not found"));

            validateUpdatedDateRange(googleCalendarEventDto, googleCalendarEvent);

            eventUpdateMapper.updateEntityFromDto(googleCalendarEventDto, googleCalendarEvent);

            Event updatedGoogleEvent = googleEventMapper.toGoogleEvent(googleCalendarEventDto);
            updatedGoogleEvent.setId(googleCalendarEvent.getGoogleCalendarId());
            log.info("Updating Google event with ID: {}", googleCalendarEvent.getGoogleCalendarId());
            log.info("Updated GoogleEvent object ID: {}", updatedGoogleEvent.getId());
            calendarService.events()
                    .update(CALENDAR_ID, googleCalendarEvent.getGoogleCalendarId(), updatedGoogleEvent)
                    .execute();

            GoogleCalendarEvent savedEvent = eventRepository.save(googleCalendarEvent);

            return eventCreateMapper.toDto(savedEvent);
        } catch (IOException e) {
            log.error("Error updating Google Calendar event: {}", e.getMessage());
            throw new CalendarApiException("Failed to update Google Calendar event", e);
        }
    }

    public void deleteEvent(Long eventId)  {
        try {
            log.info("Deleting Google Calendar event with ID: {}", eventId);

            GoogleCalendarEvent googleCalendarEvent = googleCalendarEventRepository.findById(eventId)
                    .orElseThrow(() -> new EventNotFoundException("Event not found"));

            calendarService.events().delete("primary", googleCalendarEvent.getGoogleCalendarId()).execute();

            googleCalendarEvent.setCanceled(true);
            eventRepository.save(googleCalendarEvent);
        } catch (IOException e) {
            log.error("Error deleting Google Calendar event: {}", e.getMessage());
            throw new CalendarApiException("Failed to delete Google Calendar event", e);
        }
    }

    public GoogleCalendarEventDto getEvent(Long eventId) {
        log.info("Retrieving Google Calendar event with ID: {}", eventId);
        return eventCreateMapper.toDto(eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId)));
    }

    public List<GoogleCalendarEventDto> getAllEvents() {
        log.info("Retrieving all Google Calendar events");

        List<GoogleCalendarEvent> events = eventRepository.findAllByIsCanceledFalse();

        return eventCreateMapper.toDtoList(events);
    }

    private void validateUpdatedDateRange(GoogleCalendarEventDto dto, GoogleCalendarEvent eventFromDb) {
        LocalDateTime newStart = dto.getStartTime() != null ? dto.getStartTime() : eventFromDb.getStartTime();
        LocalDateTime newEnd = dto.getEndTime() != null ? dto.getEndTime() : eventFromDb.getEndTime();

        if (newStart != null && newEnd != null && !newStart.isBefore(newEnd)) {
            throw new DataValidationException("Start time must be before end time");
        }
    }
}