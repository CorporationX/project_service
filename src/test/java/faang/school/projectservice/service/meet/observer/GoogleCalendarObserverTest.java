package faang.school.projectservice.service.meet.observer;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.repository.MeetRepository;
import faang.school.projectservice.service.meet.event.MeetCreateEvent;
import faang.school.projectservice.service.meet.event.MeetDeleteEvent;
import faang.school.projectservice.service.meet.event.MeetUpdateEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleCalendarObserverTest {

    private final String testCalendarId = "testCalendarId";
    private final String testZoneId = "Europe/Moscow";
    @Mock
    private Calendar googleCalendar;
    @Mock
    private MeetRepository meetRepository;
    @InjectMocks
    private GoogleCalendarObserver googleCalendarObserver;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(googleCalendarObserver, "calendarId", testCalendarId);
        ReflectionTestUtils.setField(googleCalendarObserver, "zoneId", testZoneId);
    }

    @Test
    public void handleMeetCreated_Success() throws Exception {
        Meet meet = new Meet();
        meet.setStartsAt(LocalDateTime.of(2025, Month.FEBRUARY, 9, 10, 0));
        meet.setTitle("Test Meeting");
        meet.setDescription("Test Description");

        MeetCreateEvent createEvent = new MeetCreateEvent(meet);
        Calendar.Events eventsMock = mock(Calendar.Events.class);
        Calendar.Events.Insert insertMock = mock(Calendar.Events.Insert.class);

        when(googleCalendar.events()).thenReturn(eventsMock);
        when(eventsMock.insert(eq(testCalendarId), any(Event.class))).thenReturn(insertMock);

        Event returnedEvent = new Event();
        returnedEvent.setId("generatedId");
        when(insertMock.execute()).thenReturn(returnedEvent);

        googleCalendarObserver.handleMeetCreated(createEvent);

        assertEquals("generatedId", meet.getGoogleEventId());
        verify(meetRepository).save(meet);
    }

    @Test
    public void testHandleMeetCreated_failure() throws Exception {
        Meet meet = new Meet();
        meet.setStartsAt(LocalDateTime.of(2025, Month.FEBRUARY, 9, 10, 0));
        meet.setTitle("Test Meeting");
        meet.setDescription("Test Description");
        MeetCreateEvent createEvent = new MeetCreateEvent(meet);

        Calendar.Events eventsMock = mock(Calendar.Events.class);
        Calendar.Events.Insert insertMock = mock(Calendar.Events.Insert.class);

        when(googleCalendar.events()).thenReturn(eventsMock);
        when(eventsMock.insert(eq(testCalendarId), any(Event.class))).thenReturn(insertMock);
        when(insertMock.execute()).thenThrow(new RuntimeException("Insert failed"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> googleCalendarObserver.handleMeetCreated(createEvent));
        assertEquals("Unable to create meet", thrown.getMessage());
        verify(meetRepository, never()).save(any());
    }

    @Test
    public void testHandleMeetUpdated_success() throws Exception {
        // Arrange
        Meet meet = new Meet();
        meet.setStartsAt(LocalDateTime.of(2025, Month.FEBRUARY, 9, 11, 0));
        meet.setTitle("Updated Meeting");
        meet.setDescription("Updated Description");
        // Для обновления необходимо, чтобы googleEventId уже был установлен
        meet.setGoogleEventId("existingId");
        MeetUpdateEvent updateEvent = new MeetUpdateEvent(meet);

        Calendar.Events eventsMock = mock(Calendar.Events.class);
        Calendar.Events.Update updateMock = mock(Calendar.Events.Update.class);

        when(googleCalendar.events()).thenReturn(eventsMock);
        when(eventsMock.update(eq(testCalendarId), eq("existingId"), any(Event.class))).thenReturn(updateMock);

        Event returnedEvent = new Event();
        returnedEvent.setId("existingId");
        when(updateMock.execute()).thenReturn(returnedEvent);

        // Act
        googleCalendarObserver.handleMeetUpdated(updateEvent);

        // Assert: проверяем, что метод update вызван с нужными параметрами
        verify(googleCalendar.events()).update(eq(testCalendarId), eq("existingId"), any(Event.class));
    }

    @Test
    public void testHandleMeetUpdated_failure_nullGoogleEventId() {
        // Arrange
        Meet meet = new Meet();
        meet.setStartsAt(LocalDateTime.of(2025, Month.FEBRUARY, 9, 11, 0));
        meet.setTitle("Updated Meeting");
        meet.setDescription("Updated Description");
        // googleEventId не задан
        MeetUpdateEvent updateEvent = new MeetUpdateEvent(meet);

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> googleCalendarObserver.handleMeetUpdated(updateEvent));
        assertEquals("Google Event not updated. EventID is null", thrown.getMessage());
    }

    @Test
    public void testHandleMeetUpdated_failure_exceptionOnUpdate() throws Exception {
        // Arrange
        Meet meet = new Meet();
        meet.setStartsAt(LocalDateTime.of(2025, Month.FEBRUARY, 9, 11, 0));
        meet.setTitle("Updated Meeting");
        meet.setDescription("Updated Description");
        meet.setGoogleEventId("existingId");
        MeetUpdateEvent updateEvent = new MeetUpdateEvent(meet);

        Calendar.Events eventsMock = mock(Calendar.Events.class);
        Calendar.Events.Update updateMock = mock(Calendar.Events.Update.class);

        when(googleCalendar.events()).thenReturn(eventsMock);
        when(eventsMock.update(eq(testCalendarId), eq("existingId"), any(Event.class))).thenReturn(updateMock);
        when(updateMock.execute()).thenThrow(new IOException("Update failed"));

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> googleCalendarObserver.handleMeetUpdated(updateEvent));
        assertEquals("Unable to update meet", thrown.getMessage());
    }

    @Test
    public void testHandleMeetDeleted_success() throws Exception {
        // Arrange
        Meet meet = new Meet();
        meet.setStartsAt(LocalDateTime.of(2025, Month.FEBRUARY, 9, 12, 0));
        meet.setTitle("Meeting to Delete");
        meet.setDescription("To be deleted");
        meet.setGoogleEventId("existingId");
        MeetDeleteEvent deleteEvent = new MeetDeleteEvent(meet);

        Calendar.Events eventsMock = mock(Calendar.Events.class);
        Calendar.Events.Delete deleteMock = mock(Calendar.Events.Delete.class);

        when(googleCalendar.events()).thenReturn(eventsMock);
        when(eventsMock.delete(eq(testCalendarId), eq("existingId"))).thenReturn(deleteMock);
        // В случае успешного удаления execute() просто выполняется без выбрасывания исключения
        doNothing().when(deleteMock).execute();

        // Act
        googleCalendarObserver.handleMeetDeleted(deleteEvent);

        // Assert: googleEventId должен стать null и должно быть сохранение meet
        assertNull(meet.getGoogleEventId());
        verify(meetRepository).save(meet);
    }

    @Test
    public void testHandleMeetDeleted_failure_exceptionOnDelete() throws Exception {
        // Arrange
        Meet meet = new Meet();
        meet.setStartsAt(LocalDateTime.of(2025, Month.FEBRUARY, 9, 12, 0));
        meet.setTitle("Meeting to Delete");
        meet.setDescription("To be deleted");
        meet.setGoogleEventId("existingId");
        MeetDeleteEvent deleteEvent = new MeetDeleteEvent(meet);

        Calendar.Events eventsMock = mock(Calendar.Events.class);
        Calendar.Events.Delete deleteMock = mock(Calendar.Events.Delete.class);

        when(googleCalendar.events()).thenReturn(eventsMock);
        when(eventsMock.delete(eq(testCalendarId), eq("existingId"))).thenReturn(deleteMock);
        when(deleteMock.execute()).thenThrow(new IOException("Delete failed"));

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> googleCalendarObserver.handleMeetDeleted(deleteEvent));
        assertEquals("Unable to delete google event", thrown.getMessage());
        verify(meetRepository, never()).save(any());
    }
}