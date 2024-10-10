package faang.school.projectservice.google.calendar;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import faang.school.projectservice.config.app.AppConfig;
import faang.school.projectservice.config.google.calendar.CalendarProvider;
import faang.school.projectservice.model.dto.CalendarEventDto;
import faang.school.projectservice.mapper.calendar.CalendarEventMapper;
import faang.school.projectservice.service.impl.GoogleCalendarServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GoogleCalendarServiceTest {

    @Mock
    private CalendarProvider calendarProvider;

    @Mock
    private CalendarEventMapper calendarEventMapper;

    @Mock
    private AppConfig appConfig;

    @Mock
    private Calendar calendar;

    @Mock
    private Calendar.Events events;

    @Mock
    private Calendar.Events.Insert insert;

    @Mock
    private Calendar.Events.Update update;

    @Mock
    private Calendar.Events.Delete delete;

    @Mock
    private Calendar.Events.Get get;

    @InjectMocks
    private GoogleCalendarServiceImpl googleCalendarService;

    @BeforeEach
    public void setUp() throws GeneralSecurityException, IOException {
        MockitoAnnotations.openMocks(this);
        when(calendarProvider.getCalendar()).thenReturn(calendar);
        when(calendar.events()).thenReturn(events);
        when(appConfig.getCalendarId()).thenReturn("calendarId");
    }

    @Test
    public void testCreateEvent() throws GeneralSecurityException, IOException {
        CalendarEventDto calendarEventDto = new CalendarEventDto();
        calendarEventDto.setSummary("Test Event");

        Event event = new Event();
        when(calendarEventMapper.toEvent(calendarEventDto)).thenReturn(event);
        when(events.insert(anyString(), any(Event.class))).thenReturn(insert);
        when(insert.setSendUpdates(anyString())).thenReturn(insert);
        when(insert.execute()).thenReturn(event);
        when(appConfig.getCalendarId()).thenReturn("testCalendarId");
        when(appConfig.getTypeOfSendUpdates()).thenReturn("all");
        when(calendarEventMapper.toDto(event)).thenReturn(calendarEventDto);

        CalendarEventDto result = googleCalendarService.createEvent(calendarEventDto);

        assertNotNull(result);
        assertEquals("Test Event", result.getSummary());
        verify(events, times(1)).insert(anyString(), any(Event.class));
    }
}
