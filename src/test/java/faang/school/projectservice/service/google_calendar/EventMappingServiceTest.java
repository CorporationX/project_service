package faang.school.projectservice.service.google_calendar;

import faang.school.projectservice.exceptions.google_calendar.exceptions.NotFoundException;
import faang.school.projectservice.model.EventMapping;
import faang.school.projectservice.repository.EventMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventMappingServiceTest {
    private static final Long EVENT_ID = 1L;
    private static final String GOOGLE_EVENT_ID = "google-event-id";
    private static final String NOT_FOUND_MESSAGE_EVENT = "Маппинг для eventId '" + EVENT_ID + "' не найден";
    private static final String NOT_FOUND_MESSAGE_GOOGLE_EVENT = "Маппинг для googleEventId '" + GOOGLE_EVENT_ID + "' не найден";

    @Mock
    private EventMappingRepository eventMappingRepository;
    @InjectMocks
    private EventMappingService eventMappingService;

    private EventMapping eventMapping;

    @BeforeEach
    public void setUp() {
        eventMapping = new EventMapping();
        eventMapping.setEventId(EVENT_ID);
        eventMapping.setGoogleEventId(GOOGLE_EVENT_ID);
    }

    @Test
    public void testSaveMapping() {
        eventMappingService.saveMapping(EVENT_ID, GOOGLE_EVENT_ID);
        verify(eventMappingRepository).save(any(EventMapping.class));
    }

    @Test
    public void testGetGoogleEventIdByEventId_Success() {
        when(eventMappingRepository.findByEventId(EVENT_ID)).thenReturn(Optional.of(eventMapping));

        String result = eventMappingService.getGoogleEventIdByEventId(EVENT_ID);
        assertEquals(GOOGLE_EVENT_ID, result);

        verify(eventMappingRepository).findByEventId(EVENT_ID);
    }

    @Test
    public void testGetGoogleEventIdByEventId_NotFound() {
        when(eventMappingRepository.findByEventId(EVENT_ID)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                eventMappingService.getGoogleEventIdByEventId(EVENT_ID));
        assertEquals(NOT_FOUND_MESSAGE_EVENT, exception.getMessage());

        verify(eventMappingRepository).findByEventId(EVENT_ID);
    }

    @Test
    public void testGetEventIdByGoogleEventId_Success() {
        when(eventMappingRepository.findByGoogleEventId(GOOGLE_EVENT_ID)).thenReturn(Optional.of(eventMapping));

        Long result = eventMappingService.getEventIdByGoogleEventId(GOOGLE_EVENT_ID);
        assertEquals(EVENT_ID, result);

        verify(eventMappingRepository).findByGoogleEventId(GOOGLE_EVENT_ID);
    }

    @Test
    public void testGetEventIdByGoogleEventId_NotFound() {
        when(eventMappingRepository.findByGoogleEventId(GOOGLE_EVENT_ID)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                eventMappingService.getEventIdByGoogleEventId(GOOGLE_EVENT_ID));
        assertEquals(NOT_FOUND_MESSAGE_GOOGLE_EVENT, exception.getMessage());

        verify(eventMappingRepository).findByGoogleEventId(GOOGLE_EVENT_ID);
    }

    @Test
    public void testDeleteMapping_Success() {
        when(eventMappingRepository.existsById(EVENT_ID)).thenReturn(true);
        doNothing().when(eventMappingRepository).deleteById(EVENT_ID);

        eventMappingService.deleteMapping(EVENT_ID);

        verify(eventMappingRepository).existsById(EVENT_ID);
        verify(eventMappingRepository).deleteById(EVENT_ID);
    }

    @Test
    public void testDeleteMapping_NotFound() {
        when(eventMappingRepository.existsById(EVENT_ID)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                eventMappingService.deleteMapping(EVENT_ID));
        assertEquals(NOT_FOUND_MESSAGE_EVENT, exception.getMessage());

        verify(eventMappingRepository).existsById(EVENT_ID);
        verify(eventMappingRepository, never()).deleteById(EVENT_ID);
    }
}