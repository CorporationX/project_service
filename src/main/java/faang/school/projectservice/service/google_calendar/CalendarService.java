package faang.school.projectservice.service.google_calendar;

import com.google.api.services.calendar.Calendar;
import faang.school.projectservice.exceptions.google_calendar.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
@Service
public class CalendarService {
    @Lazy
    @Autowired
    private Calendar calendarClient;

    public String createCalendar(String summary, String timeZone) throws IOException {
        com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();
        calendar.setSummary(summary);
        calendar.setTimeZone(timeZone);

        com.google.api.services.calendar.model.Calendar createdCalendar = calendarClient.calendars().
                insert(calendar).
                execute();
        String calendarId = createdCalendar.getId();

        log.info("Create calendar with ID: '{}'", calendarId);
        return calendarId;
    }

    public com.google.api.services.calendar.model.Calendar getCalendar(String calendarId) throws IOException {
        com.google.api.services.calendar.model.Calendar calendar = calendarClient.calendars()
                .get(calendarId).
                execute();

        if (calendar != null) {
            return calendar;
        } else {
            throw new NotFoundException("Calendar with ID '" + calendarId + "' not found");
        }
    }

    public void deleteCalendar(String calendarId) throws IOException {
        calendarClient.calendars().delete(calendarId).execute();
        log.info("Calendar with ID '{}' was deleted", calendarId);
    }
}