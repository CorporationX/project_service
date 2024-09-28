package faang.school.projectservice.service.google_calendar;

import com.google.api.services.calendar.Calendar;
import faang.school.projectservice.exceptions.google_calendar.exceptions.GoogleCalendarException;
import faang.school.projectservice.exceptions.google_calendar.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RequiredArgsConstructor
@Service
@Slf4j
public class CalendarService {
    @Lazy
    @Autowired
    private Calendar calendarClient;

    public String createCalendar(String summary, String timeZone) {
        log.info("Создание календаря с summary: '{}' и timeZone: '{}'", summary, timeZone);

        try {
            com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();
            calendar.setSummary(summary);
            calendar.setTimeZone(timeZone);

            com.google.api.services.calendar.model.Calendar createdCalendar = calendarClient.calendars().insert(calendar).execute();
            String calendarId = createdCalendar.getId();

            log.info("Календарь создан с ID: '{}'", calendarId);
            return calendarId;
        } catch (IOException e) {
            log.error("Ошибка создания календаря", e);
            throw new GoogleCalendarException("Ошибка создания календаря", e);
        }
    }

    public com.google.api.services.calendar.model.Calendar getCalendar(String calendarId) {
        log.info("Получение календаря с ID: '{}'", calendarId);

        try {
            com.google.api.services.calendar.model.Calendar calendar = calendarClient.calendars().get(calendarId).execute();

            if (calendar != null) {
                log.info("Календарь найден: '{}'", calendar.getSummary());
                return calendar;
            } else {
                log.warn("Календарь с ID '{}' не найден", calendarId);
                throw new NotFoundException("Календарь с ID '" + calendarId + "' не найден");
            }
        } catch (IOException e) {
            log.error("Ошибка получения календаря", e);
            throw new GoogleCalendarException("Ошибка получения календаря", e);
        }
    }

    public void deleteCalendar(String calendarId) {
        log.info("Удаление календаря с ID: '{}'", calendarId);

        try {
            calendarClient.calendars().delete(calendarId).execute();
            log.info("Календарь с ID '{}' удален", calendarId);
        } catch (IOException e) {
            log.error("Ошибка удаления календаря", e);
            throw new GoogleCalendarException("Ошибка удаления календаря", e);
        }
    }
}