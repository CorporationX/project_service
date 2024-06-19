package faang.school.projectservice.controller.project.calendar;

import faang.school.projectservice.dto.project.calendar.AclDto;
import faang.school.projectservice.dto.project.calendar.CalendarDto;
import faang.school.projectservice.dto.project.calendar.EventDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.project.calendar.CalendarService;
import faang.school.projectservice.testData.project.CalendarTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class CalendarControllerTest {
    @InjectMocks
    private CalendarController calendarController;
    @Mock
    private CalendarService calendarService;

    private EventDto eventDto;
    private CalendarDto calendarDto;
    private AclDto aclDto;

    @BeforeEach
    void setUp() {
        CalendarTestData testData = new CalendarTestData();

        eventDto = testData.getEventDto();
        calendarDto = testData.getCalendarDto();
        aclDto = testData.getAclDto();
    }

    @Nested
    class PositiveTests {
        @DisplayName("Should call getAuthUrl from service")
        @Test
        void getAuthorizationURLTest() {
            calendarController.getAuthorizationUrl();

            verify(calendarService).getAuthUrl();
        }

        @DisplayName("Should call auth from service")
        @Test
        void setCredentialsTest() {
            calendarController.setCredentials(1L, "code");

            verify(calendarService).auth(anyLong(), anyString());
        }

        @DisplayName("Should call eventService.createEvent when passed dto has valid dates")
        @Test
        void createEventWithValidDatesTest() {
            calendarController.createEvent(1L, "id", eventDto);

            verify(calendarService).createEvent(anyLong(), anyString(), any(EventDto.class));
        }

        @DisplayName("Should call eventService.getEvents")
        @Test
        void getEventsTest() {
            calendarController.getEvents(1L, "id");

            verify(calendarService).getEvents(anyLong(), anyString());
        }

        @DisplayName("Should call eventService.deleteEvent")
        @Test
        void deleteEventTest() {
            calendarController.deleteEvent(1L, "id", "id");

            verify(calendarService).deleteEvent(anyLong(), anyString(), anyString());
        }

        @DisplayName("Should call eventService.createCalendar")
        @Test
        void createCalendarTest() {
            calendarController.createCalendar(1L, calendarDto);

            verify(calendarService).createCalendar(anyLong(), any(CalendarDto.class));
        }

        @DisplayName("Should call eventService.deleteEvent")
        @Test
        void getCalendarTest() {
            calendarController.getCalendar(1L, "id");

            verify(calendarService).getCalendar(anyLong(), anyString());
        }

        @DisplayName("Should call eventService.createAcl")
        @Test
        void createAclRuleTest() {
            calendarController.createAclRule(1L, "id", aclDto);

            verify(calendarService).createAcl(anyLong(), anyString(), any(AclDto.class));
        }

        @DisplayName("Should call eventService.listAcl")
        @Test
        void listAclRulesTest() {
            calendarController.listAclRules(1L, "id");

            verify(calendarService).listAcl(anyLong(), anyString());
        }

        @DisplayName("Should call eventService.deleteAcl")
        @Test
        void deleteAclRuleTest() {
            calendarController.deleteAclRule(1L, "id", "id");

            verify(calendarService).deleteAcl(anyLong(), anyString(), anyString());
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("Should throw exception when passed dto has invalid dates")
        @Test
        void createEventWithValidDatesTest() {
            eventDto.setStartTime(LocalDateTime.now().plus(10, ChronoUnit.DAYS));

            assertThrows(DataValidationException.class,
                    () -> calendarController.createEvent(1L, "id", eventDto));

            verifyNoInteractions(calendarService);
        }
    }
}