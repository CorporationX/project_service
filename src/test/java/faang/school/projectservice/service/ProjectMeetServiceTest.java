package faang.school.projectservice.service;

import com.google.api.services.calendar.model.Event;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.repository.MeetRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectMeetServiceTest {

    @Mock
    private GoogleCalendarService googleCalendarService;

    @Mock
    private MeetRepository meetRepository;

    @InjectMocks
    private ProjectMeetService projectMeetService;

    @Test
    void testCreateMeetEvent() {
        String calendarId = "testCalendarId";
        Meet meet = new Meet();
        meet.setTitle("Test Meet");
        meet.setDescription("Test Description");
        meet.setStartsAt(LocalDateTime.now());

        Event createdEvent = new Event();
        createdEvent.setId("testEventId");
        when(googleCalendarService.createEvent(anyString(), any(Event.class))).thenReturn(createdEvent);

        projectMeetService.createMeetEvent(calendarId, meet);

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(googleCalendarService).createEvent(eq(calendarId), eventCaptor.capture());
        Event event = eventCaptor.getValue();

        assertEquals("Test Meet", event.getSummary());
        assertEquals("Test Description", event.getDescription());
        assertEquals("testEventId", meet.getGoogleEventId());
    }

    @Test
    void testGetMeetEvent() {
        String calendarId = "testCalendarId";
        String eventId = "testEventId";
        Event event = new Event();
        when(googleCalendarService.getEvent(calendarId, eventId)).thenReturn(event);

        Event result = projectMeetService.getMeetEvent(calendarId, eventId);

        assertEquals(event, result);
        verify(googleCalendarService).getEvent(calendarId, eventId);
    }

    @Test
    void testDeleteMeetEvent() {
        String calendarId = "testCalendarId";
        String eventId = "testEventId";

        projectMeetService.deleteMeetEvent(calendarId, eventId);

        verify(googleCalendarService).deleteEvent(calendarId, eventId);
    }
}
