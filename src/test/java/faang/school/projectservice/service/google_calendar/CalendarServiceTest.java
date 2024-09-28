package faang.school.projectservice.service.google_calendar;

import com.google.api.services.calendar.Calendar;
import faang.school.projectservice.exceptions.google_calendar.exceptions.GoogleCalendarException;
import faang.school.projectservice.exceptions.google_calendar.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CalendarServiceTest {
    private static final String CALENDAR_ID = "test-calendar-id";
    private static final String SUMMARY = "Test Calendar";
    private static final String TIME_ZONE = "America/New_York";
    private static final String TEST_IO_EXCEPTION_MESSAGE = "Test IOException";

    @Mock
    private Calendar calendarClient;

    @InjectMocks
    private CalendarService calendarService;

    private Calendar.Calendars calendars;
    private Calendar.Calendars.Insert insertRequest;
    private Calendar.Calendars.Get getRequest;
    private Calendar.Calendars.Delete deleteRequest;

    @BeforeEach
    public void setUp() throws IOException {
        calendars = mock(Calendar.Calendars.class);
        when(calendarClient.calendars()).thenReturn(calendars);

        insertRequest = mock(Calendar.Calendars.Insert.class);
        lenient().when(calendars.insert(any(com.google.api.services.calendar.model.Calendar.class))).thenReturn(insertRequest);

        getRequest = mock(Calendar.Calendars.Get.class);
        lenient().when(calendars.get(eq(CALENDAR_ID))).thenReturn(getRequest);

        deleteRequest = mock(Calendar.Calendars.Delete.class);
        lenient().when(calendars.delete(eq(CALENDAR_ID))).thenReturn(deleteRequest);
    }

    @Test
    public void testCreateCalendar_Success() throws IOException {
        com.google.api.services.calendar.model.Calendar createdCalendar = new com.google.api.services.calendar.model.Calendar();
        createdCalendar.setId(CALENDAR_ID);

        when(insertRequest.execute()).thenReturn(createdCalendar);

        String result = calendarService.createCalendar(SUMMARY, TIME_ZONE);

        assertEquals(CALENDAR_ID, result);
        verify(calendarClient.calendars()).insert(any(com.google.api.services.calendar.model.Calendar.class));
        verify(insertRequest).execute();
    }

    @Test
    public void testCreateCalendar_IOException() throws IOException {
        when(insertRequest.execute()).thenThrow(new IOException(TEST_IO_EXCEPTION_MESSAGE));

        assertThrows(GoogleCalendarException.class, () -> calendarService.createCalendar(SUMMARY, TIME_ZONE));
        verify(calendarClient.calendars()).insert(any(com.google.api.services.calendar.model.Calendar.class));
        verify(insertRequest).execute();
    }

    @Test
    public void testGetCalendar_Success() throws IOException {
        com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();
        calendar.setId(CALENDAR_ID);
        calendar.setSummary(SUMMARY);

        when(getRequest.execute()).thenReturn(calendar);

        com.google.api.services.calendar.model.Calendar result = calendarService.getCalendar(CALENDAR_ID);

        assertNotNull(result);
        assertEquals(CALENDAR_ID, result.getId());
        verify(calendarClient.calendars()).get(CALENDAR_ID);
        verify(getRequest).execute();
    }

    @Test
    public void testGetCalendar_NotFound() throws IOException {
        when(getRequest.execute()).thenReturn(null);

        assertThrows(NotFoundException.class, () -> calendarService.getCalendar(CALENDAR_ID));
        verify(calendarClient.calendars()).get(CALENDAR_ID);
        verify(getRequest).execute();
    }

    @Test
    public void testGetCalendar_IOException() throws IOException {
        when(getRequest.execute()).thenThrow(new IOException(TEST_IO_EXCEPTION_MESSAGE));

        assertThrows(GoogleCalendarException.class, () -> calendarService.getCalendar(CALENDAR_ID));
        verify(calendarClient.calendars()).get(CALENDAR_ID);
        verify(getRequest).execute();
    }

    @Test
    public void testDeleteCalendar_Success() throws IOException {
        doNothing().when(deleteRequest).execute();

        calendarService.deleteCalendar(CALENDAR_ID);

        verify(calendarClient.calendars()).delete(CALENDAR_ID);
        verify(deleteRequest).execute();
    }

    @Test
    public void testDeleteCalendar_IOException() throws IOException {
        doThrow(new IOException(TEST_IO_EXCEPTION_MESSAGE)).when(deleteRequest).execute();

        assertThrows(GoogleCalendarException.class, () -> calendarService.deleteCalendar(CALENDAR_ID));
        verify(calendarClient.calendars()).delete(CALENDAR_ID);
        verify(deleteRequest).execute();
    }
}