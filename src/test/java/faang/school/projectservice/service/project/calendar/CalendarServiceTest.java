package faang.school.projectservice.service.project.calendar;


import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Acl;
import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import faang.school.projectservice.dto.project.calendar.AclDto;
import faang.school.projectservice.dto.project.calendar.CalendarDto;
import faang.school.projectservice.dto.project.calendar.EventDto;
import faang.school.projectservice.mapper.AclMapper;
import faang.school.projectservice.mapper.CalendarMapper;
import faang.school.projectservice.mapper.EventMapper;
import faang.school.projectservice.model.CalendarToken;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.testData.project.CalendarTestData;
import faang.school.projectservice.testData.project.ProjectTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static faang.school.projectservice.service.project.calendar.CalendarService.EVENTS_COUNT_TO_BE_RETURNED;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalendarServiceTest {
    @Spy
    @InjectMocks
    private CalendarService calendarService;
    @Mock
    private GoogleAuthorizationService oAuthService;
    @Mock
    private ProjectService projectService;
    @Mock
    private EventMapper eventMapper;
    @Mock
    private CalendarMapper calendarMapper;
    @Mock
    private AclMapper aclMapper;

    @Mock
    private Calendar calendarClient;
    @Mock
    private Calendar.Events events;
    @Mock
    private Calendar.Calendars calendars;
    @Mock
    private Calendar.Acl acls;
    private EventDto eventDto;
    private Event event;
    private CalendarDto calendarDto;
    private com.google.api.services.calendar.model.Calendar calendar;
    private AclDto aclDto;
    private AclRule acl;
    private Project project;


    @BeforeEach
    void setUp() {
        CalendarTestData calendarTestData = new CalendarTestData();
        ProjectTestData projectTestData = new ProjectTestData();

        eventDto = calendarTestData.getEventDto();
        event = calendarTestData.getEvent();
        calendarDto = calendarTestData.getCalendarDto();
        calendar = calendarTestData.getCalendar();
        aclDto = calendarTestData.getAclDto();
        acl = calendarTestData.getAcl();
        project = projectTestData.getProject();
    }

    @Test
    void getAuthUrlTest() {
        calendarService.getAuthUrl();

        verify(oAuthService).getAuthUrl();
    }

    @Test
    void authTest() {
        when(projectService.getProjectModel(anyLong())).thenReturn(project);
        when(oAuthService.authorizeProject(any(Project.class), anyString())).thenReturn(new CalendarToken());

        calendarService.auth(1L, "code");

        verify(oAuthService).generateCredential(any(CalendarToken.class));
    }

    @Nested
    class TestsUsingCalendar {
        @BeforeEach
        void setUp() {
            doReturn(calendarClient).when(calendarService).buildCalendar(anyLong());
        }

        @Nested
        class EventsTests {
            @BeforeEach
            void setUp() {
                when(calendarClient.events()).thenReturn(events);
            }

            @Test
            void createEventTest() throws IOException {
                when(eventMapper.toModel(any(EventDto.class))).thenReturn(event);
                when(events.insert(anyString(), any(Event.class))).thenReturn(mock(Calendar.Events.Insert.class));

                assertDoesNotThrow(() -> calendarService.createEvent(1L, "id", eventDto));

                verify(eventMapper).toDto(any());
            }

            @Test
            void getEvents() throws IOException {
                Calendar.Events.List eventsList = mock(Calendar.Events.List.class);
                Events modelEvents = mock(Events.class);

                when(events.list(anyString())).thenReturn(eventsList);
                when(eventsList.setMaxResults(anyInt())).thenReturn(eventsList);
                doReturn(modelEvents).when(eventsList).execute();

                assertDoesNotThrow(() -> calendarService.getEvents(1L, "id"));

                verify(eventsList).setMaxResults(EVENTS_COUNT_TO_BE_RETURNED);
                verify(modelEvents).getItems();
            }

            @Test
            void deleteEventTest() throws IOException {
                when(events.delete(anyString(), anyString())).thenReturn(mock(Calendar.Events.Delete.class));

                assertDoesNotThrow(() -> calendarService.deleteEvent(1L, "id", "id"));
            }
        }

        @Nested
        class CalendarsTests {
            @BeforeEach
            void setUp() {
                when(calendarClient.calendars()).thenReturn(calendars);
            }

            @Test
            void createCalendarTests() throws IOException {
                when(calendarMapper.toModel(any(CalendarDto.class))).thenReturn(calendar);
                when(calendars.insert(any(com.google.api.services.calendar.model.Calendar.class)))
                        .thenReturn(mock(Calendar.Calendars.Insert.class));

                assertDoesNotThrow(() -> calendarService.createCalendar(1L, calendarDto));

                verify(calendarMapper).toDto(any());
            }

            @Test
            void getCalendarTests() throws IOException {
                when(calendars.get(anyString())).thenReturn(mock(Calendar.Calendars.Get.class));

                assertDoesNotThrow(() -> calendarService.getCalendar(1L, "id"));

                verify(calendarMapper).toDto(any());
            }
        }

        @Nested
        class AclTests {
            @BeforeEach
            void setUp() {
                when(calendarClient.acl()).thenReturn(acls);
            }

            @Test
            void createAclTest() throws IOException {
                when(aclMapper.toModel(any(AclDto.class))).thenReturn(acl);
                when(acls.insert(anyString(), any(AclRule.class)))
                        .thenReturn(mock(Calendar.Acl.Insert.class));

                assertDoesNotThrow(() -> calendarService.createAcl(1L, "id", aclDto));

                verify(aclMapper).toDto(any());
            }

            @Test
            void listAclTest() throws IOException {
                Calendar.Acl.List aclList = mock(Calendar.Acl.List.class);
                when(acls.list(anyString())).thenReturn(aclList);
                doReturn(mock(Acl.class)).when(aclList).execute();

                assertDoesNotThrow(() -> calendarService.listAcl(1L, "id"));

                verify(aclMapper).toDtos(anyList());
            }

            @Test
            void deleteAclTest() throws IOException {
                when(acls.delete(anyString(), anyString()))
                        .thenReturn(mock(Calendar.Acl.Delete.class));

                assertDoesNotThrow(() -> calendarService.deleteAcl(1L, "id", "id"));
            }
        }
    }
}