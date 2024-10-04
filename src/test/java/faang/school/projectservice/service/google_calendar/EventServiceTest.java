package faang.school.projectservice.service.google_calendar;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import faang.school.projectservice.dto.client.EventDtoForGoogleCalendar;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.mapper.EventMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

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
public class EventServiceTest {
    private static final Long EVENT_ID = 1L;
    private static final String CALENDAR_ID = "primary";
    private static final String GOOGLE_EVENT_ID = "google-event-id";

    @Mock
    private Calendar calendarClient;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private EventMappingService eventMappingService;
    @Mock
    private EventMapper eventMapper;
    @InjectMocks
    private EventService eventService;

    private Calendar.Events events;
    private Calendar.Events.Insert insertRequest;
    private Calendar.Events.Get getRequest;
    private Calendar.Events.Delete deleteRequest;

    @BeforeEach
    public void setUp() throws IOException {
        ReflectionTestUtils.setField(eventService, "calendarClient", calendarClient);

        events = mock(Calendar.Events.class);
        Calendar.Calendars calendars = mock(Calendar.Calendars.class);
        Calendar.Calendars.Get calendarGetRequest = mock(Calendar.Calendars.Get.class);

        lenient().when(calendarClient.events()).thenReturn(events);
        lenient().when(calendarClient.calendars()).thenReturn(calendars);
        lenient().when(calendars.get(CALENDAR_ID)).thenReturn(calendarGetRequest);

        insertRequest = mock(Calendar.Events.Insert.class);
        lenient().when(events.insert(eq(CALENDAR_ID), any(Event.class))).thenReturn(insertRequest);

        getRequest = mock(Calendar.Events.Get.class);
        lenient().when(events.get(eq(CALENDAR_ID), eq(GOOGLE_EVENT_ID))).thenReturn(getRequest);

        deleteRequest = mock(Calendar.Events.Delete.class);
        lenient().when(events.delete(eq(CALENDAR_ID), eq(GOOGLE_EVENT_ID))).thenReturn(deleteRequest);
    }

    @Test
    public void testCreateEventInGoogleCalendar_Success() throws IOException {
        EventDtoForGoogleCalendar eventDto = new EventDtoForGoogleCalendar();
        eventDto.setId(EVENT_ID);

        Event googleEvent = new Event();
        googleEvent.setId(GOOGLE_EVENT_ID);

        when(userServiceClient.getEventForGoogleCalendar(EVENT_ID)).thenReturn(eventDto);
        when(eventMapper.mapToGoogleEvent(eventDto)).thenReturn(new Event());
        when(events.insert(eq(CALENDAR_ID), any(Event.class))).thenReturn(insertRequest);

        when(insertRequest.execute()).thenReturn(googleEvent);

        String result = eventService.createEventInGoogleCalendar(EVENT_ID, CALENDAR_ID);

        assertEquals(GOOGLE_EVENT_ID, result);
        verify(events).insert(eq(CALENDAR_ID), any(Event.class));
        verify(insertRequest).execute();
        verify(eventMappingService).saveMapping(EVENT_ID, GOOGLE_EVENT_ID);
    }

    @Test
    public void testCreateEventInGoogleCalendar_IOException() throws IOException {
        EventDtoForGoogleCalendar eventDto = new EventDtoForGoogleCalendar();
        eventDto.setId(EVENT_ID);

        Event googleEvent = new Event();
        googleEvent.setId(GOOGLE_EVENT_ID);

        when(userServiceClient.getEventForGoogleCalendar(EVENT_ID)).thenReturn(eventDto);
        when(eventMapper.mapToGoogleEvent(eventDto)).thenReturn(googleEvent);
        when(insertRequest.execute()).thenThrow(new IOException("Test IOException"));

        assertThrows(IOException.class, () -> eventService.createEventInGoogleCalendar(EVENT_ID, CALENDAR_ID));

        verify(events).insert(eq(CALENDAR_ID), any(Event.class));
        verify(insertRequest).execute();
    }

    @Test
    public void testGetEventFromGoogleCalendar_Success() throws IOException {
        Event googleEvent = new Event();
        googleEvent.setId(GOOGLE_EVENT_ID);

        when(eventMappingService.getGoogleEventIdByEventId(EVENT_ID)).thenReturn(GOOGLE_EVENT_ID);
        when(getRequest.execute()).thenReturn(googleEvent);

        EventDtoForGoogleCalendar eventDto = new EventDtoForGoogleCalendar();
        eventDto.setId(EVENT_ID);

        when(eventMapper.mapToEventDto(googleEvent)).thenReturn(eventDto);
        when(eventMappingService.getEventIdByGoogleEventId(GOOGLE_EVENT_ID)).thenReturn(EVENT_ID);

        EventDtoForGoogleCalendar result = eventService.getEventFromGoogleCalendar(EVENT_ID);

        assertNotNull(result);
        assertEquals(EVENT_ID, result.getId());
        verify(events).get(CALENDAR_ID, GOOGLE_EVENT_ID);
        verify(getRequest).execute();
    }

    @Test
    public void testGetEventFromGoogleCalendar_IOException() throws IOException {
        when(eventMappingService.getGoogleEventIdByEventId(EVENT_ID)).thenReturn(GOOGLE_EVENT_ID);
        when(getRequest.execute()).thenThrow(new IOException("Test IOException"));

        assertThrows(IOException.class, () -> eventService.getEventFromGoogleCalendar(EVENT_ID));

        verify(events).get(CALENDAR_ID, GOOGLE_EVENT_ID);
        verify(getRequest).execute();
    }

    @Test
    public void testDeleteEventFromGoogleCalendar_Success() throws IOException {
        doNothing().when(deleteRequest).execute();
        when(eventMappingService.getGoogleEventIdByEventId(EVENT_ID)).thenReturn(GOOGLE_EVENT_ID);

        eventService.deleteEventFromGoogleCalendar(EVENT_ID);

        verify(events).delete(CALENDAR_ID, GOOGLE_EVENT_ID);
        verify(deleteRequest).execute();
        verify(eventMappingService).deleteMapping(EVENT_ID);
    }

    @Test
    public void testDeleteEventFromGoogleCalendar_IOException() throws IOException {
        when(eventMappingService.getGoogleEventIdByEventId(EVENT_ID)).thenReturn(GOOGLE_EVENT_ID);
        doThrow(new IOException("Test IOException")).when(deleteRequest).execute();

        assertThrows(IOException.class, () -> eventService.deleteEventFromGoogleCalendar(EVENT_ID));

        verify(events).delete(CALENDAR_ID, GOOGLE_EVENT_ID);
        verify(deleteRequest).execute();
    }
}