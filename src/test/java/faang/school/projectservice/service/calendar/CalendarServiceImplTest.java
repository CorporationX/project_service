package faang.school.projectservice.service.calendar;

import com.google.api.services.calendar.Calendar;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.EventDto;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.google.AuthorizationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class CalendarServiceImplTest {
    @InjectMocks
    private CalendarServiceImpl calendarService;
    @Mock
    private AuthorizationService authorizationService;
    @Mock
    private UserServiceClient client;
    private EventDto eventDto;
    private final long eventId = 1L;
    private final String CALENDAR_ID = "primary";

    @BeforeEach
    public void init() throws GeneralSecurityException, IOException, InstantiationException, IllegalAccessException {
        eventDto = new EventDto();
        eventDto.setId(eventId);
        eventDto.setTitle("123");
        eventDto.setStartDate(LocalDateTime.now());
        eventDto.setEndDate(LocalDateTime.MAX);
        eventDto.setOwner(new UserDto());
        eventDto.setCalendarEventId(null);

        Calendar calendarMock = Mockito.mock(Calendar.class);
        Calendar.Calendars calendarsMock = Mockito.mock(Calendar.Calendars.class);

        Mockito.lenient().when(client.getEvent(eventId))
                .thenReturn(eventDto);
        Mockito.lenient().when(authorizationService.authorizeAndGetCalendar())
                .thenReturn(calendarMock);
        Mockito.lenient().when(calendarMock.calendars())
                .thenReturn(calendarsMock);
        Mockito.lenient().when(calendarsMock.get(CALENDAR_ID))
                .thenReturn(null);
    }

    @Test
    void addEventToCalendar_whenNothingInCalendar() throws GeneralSecurityException, IOException {
        Assertions.assertThrows(DataValidationException.class, () -> calendarService.addEventToCalendar(eventId, "1"));

        Mockito.verify(authorizationService, Mockito.times(1))
                .authorizeAndGetCalendar();
        Mockito.verify(client, Mockito.times(1))
                .getEvent(eventId);
    }

    @Test
    void addEventToCalendar_whenAlreadyInCalendar() throws GeneralSecurityException, IOException {
        eventDto.setCalendarEventId("first");

        calendarService.addEventToCalendar(eventId, "1");
        Mockito.verify(authorizationService, Mockito.never())
                .authorizeAndGetCalendar();
        Mockito.verify(client, Mockito.times(1))
                .getEvent(eventId);
    }

    @Test
    void updateByEventDtoTest_whenOk() throws GeneralSecurityException, IOException {
        Assertions.assertThrows(DataValidationException.class, () -> calendarService.update(eventDto, CALENDAR_ID));

        Mockito.verify(authorizationService, Mockito.times(1))
                .authorizeAndGetCalendar();
    }

    @Test
    void updateById_whenOk() throws GeneralSecurityException, IOException {
        Assertions.assertThrows(DataValidationException.class, () -> calendarService.update(eventId, CALENDAR_ID));

        Mockito.verify(client, Mockito.times(1))
                .getEvent(eventId);
        Mockito.verify(authorizationService, Mockito.times(1))
                .authorizeAndGetCalendar();
    }

    @Test
    void getEvents_whenOk() throws GeneralSecurityException, IOException {
        Assertions.assertThrows(NullPointerException.class, () -> calendarService.getEvents(CALENDAR_ID));

        Mockito.verify(authorizationService, Mockito.times(1))
                .authorizeAndGetCalendar();
    }
}
