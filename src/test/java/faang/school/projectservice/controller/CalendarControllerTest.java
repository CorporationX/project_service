package faang.school.projectservice.controller;

import faang.school.projectservice.dto.EventDto;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.service.calendar.CalendarService;
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
class CalendarControllerTest {
    @InjectMocks
    private CalendarController controller;

    @Mock
    private CalendarService service;
    private final String CALENDAR_ID = "primary";

    private EventDto initDto() {
        EventDto eventDto = new EventDto();

        eventDto.setId(1L);
        eventDto.setTitle("123");
        eventDto.setStartDate(LocalDateTime.now());
        eventDto.setEndDate(LocalDateTime.MAX);
        eventDto.setOwner(new UserDto());

        return eventDto;
    }

    @Test
    void addEventToCalendar_whenOk() throws GeneralSecurityException, IOException {
        controller.addEventToCalendar(CALENDAR_ID, 1L);
        Mockito.verify(service, Mockito.times(1))
                .addEventToCalendar(1L, CALENDAR_ID);
    }

    @Test
    void getEvents_whenOk() throws GeneralSecurityException, IOException {
        controller.getEvents(CALENDAR_ID);

        Mockito.verify(service, Mockito.times(1))
                .getEvents(CALENDAR_ID);
    }

    @Test
    void updateEventByDto_whenOk() throws GeneralSecurityException, IOException {
        EventDto eventDto = initDto();

        controller.updateEvent(CALENDAR_ID, eventDto);

        Mockito.verify(service, Mockito.times(1))
                .update(eventDto, CALENDAR_ID);
    }

    @Test
    void updateEventById_whenOk() throws GeneralSecurityException, IOException {
        controller.updateEvent(CALENDAR_ID, 1L);

        Mockito.verify(service, Mockito.times(1))
                .update(1L, CALENDAR_ID);
    }
}
