package faang.school.projectservice.service.google_calendar;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.EventDtoForGoogleCalendar;
import faang.school.projectservice.exceptions.google_calendar.exceptions.GoogleCalendarException;
import faang.school.projectservice.mapper.EventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RequiredArgsConstructor
@Service
@Slf4j
public class EventService {
    @Lazy
    @Autowired
    private Calendar calendarClient;
    private final UserServiceClient userServiceClient;
    private final EventMappingService eventMappingService;
    private final EventMapper eventMapper;
    private static final String DEFAULT_CALENDAR = "primary";

    public String createEventInGoogleCalendar(Long eventId, String calendarId) {
        log.info("Создание мероприятия в Google Календаре для eventId '{}'", eventId);
        try {
            EventDtoForGoogleCalendar eventDto = userServiceClient.getEventForGoogleCalendar(eventId);
            log.debug("Получены данные мероприятия: {}", eventDto);

            Event googleEvent = mapToGoogleEvent(eventDto);
            String resolvedCalendarId = resolveCalendarId(calendarId);

            Event createdEvent = calendarClient.events().insert(resolvedCalendarId, googleEvent).execute();
            log.info("Мероприятие создано в Google Календаре с ID: '{}'", createdEvent.getId());

            eventMappingService.saveMapping(eventId, createdEvent.getId());
            return createdEvent.getId();
        } catch (IOException e) {
            log.error("Ошибка при создании мероприятия в Google Календаре", e);
            throw new GoogleCalendarException("Ошибка при создании мероприятия в Google Календаре", e);
        }
    }

    public EventDtoForGoogleCalendar getEventFromGoogleCalendar(Long eventId) {
        log.info("Получение мероприятия из Google Календаря для eventId '{}'", eventId);
        try {
            String googleEventId = eventMappingService.getGoogleEventIdByEventId(eventId);
            Event googleEvent = calendarClient.events().get(DEFAULT_CALENDAR, googleEventId).execute();
            log.debug("Получено мероприятие из Google Календаря: {}", googleEvent);

            return mapToEventDto(googleEvent, googleEventId);
        } catch (IOException e) {
            log.error("Ошибка при получении мероприятия из Google Календаря", e);
            throw new GoogleCalendarException("Ошибка при создании мероприятия в Google Календаре", e);
        }
    }

    public void deleteEventFromGoogleCalendar(Long eventId) {
        log.info("Удаление мероприятия из Google Календаря для eventId '{}'", eventId);
        try {
            String googleEventId = eventMappingService.getGoogleEventIdByEventId(eventId);
            calendarClient.events().delete(DEFAULT_CALENDAR, googleEventId).execute();
            log.info("Мероприятие с googleEventId '{}' удалено из Google Календаря", googleEventId);

            eventMappingService.deleteMapping(eventId);
            log.info("Маппинг для eventId '{}' удален", eventId);
        } catch (IOException e) {
            log.error("Ошибка при удалении мероприятия из Google Календаря", e);
            throw new GoogleCalendarException("Ошибка при создании мероприятия в Google Календаре", e);
        }
    }

    private Event mapToGoogleEvent(EventDtoForGoogleCalendar eventDto) {
        Event googleEvent = eventMapper.mapToGoogleEvent(eventDto);
        googleEvent.setId(null);
        log.debug("Маппинг EventDto на Google Event выполнен");
        return googleEvent;
    }

    private String resolveCalendarId(String calendarId) {
        if (calendarId == null || calendarId.isEmpty() || !isCalendarExists(calendarId)) {
            return DEFAULT_CALENDAR;
        }
        return calendarId;
    }

    private boolean isCalendarExists(String calendarId) {
        try {
            calendarClient.calendars().get(calendarId).execute();
            return true;
        } catch (IOException e) {
            log.warn("Календарь с ID '{}' не найден", calendarId);
            return false;
        }
    }

    private EventDtoForGoogleCalendar mapToEventDto(Event googleEvent, String googleEventId) {
        Long eventIdForDto = eventMappingService.getEventIdByGoogleEventId(googleEventId);
        EventDtoForGoogleCalendar eventDto = eventMapper.mapToEventDto(googleEvent);
        eventDto.setId(eventIdForDto);
        log.debug("Маппинг Google Event на EventDto выполнен");
        return eventDto;
    }
}