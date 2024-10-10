package faang.school.projectservice.service;

import com.google.api.services.calendar.model.EventDateTime;
import faang.school.projectservice.model.dto.CalendarEventDto;
import faang.school.projectservice.model.dto.ZonedDateTimeDto;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.ZoneId;

public interface GoogleCalendarService {
    CalendarEventDto createEvent(CalendarEventDto calendarEventDto) throws GeneralSecurityException, IOException;

    CalendarEventDto update(CalendarEventDto dto) throws GeneralSecurityException, IOException;

    void delete(String calendarEventId) throws GeneralSecurityException, IOException;

    default EventDateTime convertToEventDateTime(ZonedDateTimeDto zonedDateTimeDto) {
        if (zonedDateTimeDto.getLocalDateTime() == null || zonedDateTimeDto.getTimeZone() == null) {
            return null;
        }
        return new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(java.util.Date.from(
                        zonedDateTimeDto.getLocalDateTime().atZone(
                                ZoneId.of(zonedDateTimeDto.getTimeZone())).toInstant())))
                .setTimeZone(zonedDateTimeDto.getTimeZone());
    }
}
