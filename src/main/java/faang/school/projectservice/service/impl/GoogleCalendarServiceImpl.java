package faang.school.projectservice.service.impl;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import faang.school.projectservice.config.app.AppConfig;
import faang.school.projectservice.model.dto.CalendarEventDto;
import faang.school.projectservice.model.enums.CalendarEventStatus;
import faang.school.projectservice.config.google.calendar.CalendarProvider;
import faang.school.projectservice.mapper.calendar.CalendarEventMapper;
import faang.school.projectservice.service.GoogleCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RequiredArgsConstructor
@Service
public class GoogleCalendarServiceImpl implements GoogleCalendarService {

    private final CalendarProvider calendarProvider;
    private final CalendarEventMapper calendarEventMapper;
    private final AppConfig appConfig;

    @Override
    public CalendarEventDto createEvent(CalendarEventDto calendarEventDto) throws GeneralSecurityException, IOException {
        Calendar calendar = calendarProvider.getCalendar();
        Event event = calendarEventMapper.toEvent(calendarEventDto);
        String calendarId = appConfig.getCalendarId();
        String typeOfSendUpdate = appConfig.getTypeOfSendUpdates();
        event = calendar.events().insert(calendarId, event)
                .setSendUpdates(typeOfSendUpdate)
                .execute();
        return calendarEventMapper.toDto(event);
    }

    @Override
    public CalendarEventDto update(CalendarEventDto dto) throws GeneralSecurityException, IOException {
        Calendar calendar = calendarProvider.getCalendar();
        String calendarId = appConfig.getCalendarId();
        Event event = calendar.events().get(calendarId, dto.getId()).execute();

        event.setSummary(dto.getSummary());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());

        EventDateTime start = convertToEventDateTime(dto.getStartDate());
        event.setStart(start);

        EventDateTime end = convertToEventDateTime(dto.getEndDate());
        event.setEnd(end);

        if (event.getStatus() != null && !event.getStatus().equals(CalendarEventStatus.CANCELLED.toString())) {
            event.setAttendees(calendarEventMapper.toEventAttendees(dto.getAttendeeEmails()));
        }

        String typeOfSendUpdate = appConfig.getTypeOfSendUpdates();
        Event updatedEvent = calendar.events().update(calendarId, dto.getId(), event)
                .setSendUpdates(typeOfSendUpdate)
                .execute();

        return calendarEventMapper.toDto(updatedEvent);
    }

    @Override
    public void delete(String calendarEventId) throws GeneralSecurityException, IOException {
        String calendarId = appConfig.getCalendarId();
        Calendar calendar = calendarProvider.getCalendar();
        String typeOfSendUpdate = appConfig.getTypeOfSendUpdates();
        calendar.events().delete(calendarId, calendarEventId)
                .setSendUpdates(typeOfSendUpdate)
                .execute();
    }
}
