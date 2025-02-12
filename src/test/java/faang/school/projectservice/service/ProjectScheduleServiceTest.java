package faang.school.projectservice.service;

import com.google.api.services.calendar.model.Event;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.service.google.GoogleCalendarService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectScheduleServiceTest {

    @Mock
    private GoogleCalendarService googleCalendarService;

    @InjectMocks
    private ProjectScheduleService projectScheduleService;

    @Test
    void testCreateScheduleEvent() {
        String calendarId = "testCalendarId";
        Schedule schedule = new Schedule();
        schedule.setName("Test Schedule");
        schedule.setDescription("Test Description");
        schedule.setCreatedAt(LocalDateTime.now());

        Event createdEvent = new Event();
        createdEvent.setId("testEventId");
        when(googleCalendarService.createEvent(anyString(), any(Event.class))).thenReturn(createdEvent);

        projectScheduleService.createScheduleEvent(calendarId, schedule);

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(googleCalendarService).createEvent(anyString(), eventCaptor.capture());
        Event event = eventCaptor.getValue();

        assertEquals("Test Schedule", event.getSummary());
        assertEquals("Test Description", event.getDescription());
    }

    @Test
    void testGetScheduleEvent() {
        String calendarId = "testCalendarId";
        String eventId = "testEventId";
        Event event = new Event();
        when(googleCalendarService.getEvent(calendarId, eventId)).thenReturn(event);

        Event result = projectScheduleService.getScheduleEvent(calendarId, eventId);

        assertEquals(event, result);
        verify(googleCalendarService).getEvent(calendarId, eventId);
    }

    @Test
    void testDeleteScheduleEvent() {
        String calendarId = "testCalendarId";
        String eventId = "testEventId";

        projectScheduleService.deleteScheduleEvent(calendarId, eventId);

        verify(googleCalendarService).deleteEvent(calendarId, eventId);
    }
}
