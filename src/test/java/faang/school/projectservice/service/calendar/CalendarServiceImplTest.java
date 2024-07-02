package faang.school.projectservice.service.calendar;

import com.amazonaws.services.kms.model.NotFoundException;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.Event;
import faang.school.projectservice.dto.calendar.AclDto;
import faang.school.projectservice.dto.calendar.CalendarDto;
import faang.school.projectservice.dto.calendar.CalendarEventDto;
import faang.school.projectservice.dto.calendar.ScopeDto;
import faang.school.projectservice.exceptions.NoCredentialsException;
import faang.school.projectservice.mapper.calendar.AclMapper;
import faang.school.projectservice.mapper.calendar.CalendarMapper;
import faang.school.projectservice.mapper.calendar.CalenderEventMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.calendar.CalendarToken;
import faang.school.projectservice.model.enums.calendar.AclRole;
import faang.school.projectservice.model.enums.calendar.AclScopeType;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CalendarServiceImplTest {

    @Mock
    private GoogleAuthorizationServiceImpl oAuthService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private CalenderEventMapper eventMapper;

    @Mock
    private CalendarMapper calendarMapper;

    @Mock
    private AclMapper aclMapper;

    @Mock
    private Environment env;

    @InjectMocks
    private CalendarServiceImpl calendarService;

    private Project project;
    private CalendarToken calendarToken;
    private Credential expectedCredential;
    private Calendar googleCalendar;

    @Mock
    private Calendar.Events events;
    @Mock
    private Calendar.Events.Insert eventsInsert;
    @Mock
    private Calendar.Events.List eventsList;
    @Mock
    private Calendar.Events.Delete eventsDelete;
    @Mock
    private Calendar.Calendars calendars;
    @Mock
    private Calendar.Calendars.Insert calendarsInsert;
    @Mock
    private Calendar.Calendars.Get calendarsGet;
    @Mock
    private Calendar.Acl acl;
    @Mock
    private Calendar.Acl.Insert aclInsert;
    @Mock
    private Calendar.Acl.List aclList;
    @Mock
    private Calendar.Acl.Delete aclDelete;

    private AclDto aclDto;
    private CalendarEventDto eventDto;
    private CalendarDto calendarDto;
    private CalendarEventDto expectedDto;
    private Event event;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(calendarService, "eventsCountToBeReturned", 10);

        project = new Project();
        project.setId(1L);

        calendarToken = new CalendarToken();
        calendarToken.setAccessToken("accessToken");

        expectedCredential = mock(Credential.class);

        googleCalendar = mock(Calendar.class);

        when(googleCalendar.events()).thenReturn(events);
        when(events.insert(anyString(), any(Event.class))).thenReturn(eventsInsert);
        when(events.list(anyString())).thenReturn(eventsList);
        when(events.delete(anyString(), anyString())).thenReturn(eventsDelete);

        when(googleCalendar.calendars()).thenReturn(calendars);
        when(calendars.insert(any(com.google.api.services.calendar.model.Calendar.class))).thenReturn(calendarsInsert);
        when(calendars.get(anyString())).thenReturn(calendarsGet);

        when(googleCalendar.acl()).thenReturn(acl);
        when(acl.insert(anyString(), any(AclRule.class))).thenReturn(aclInsert);
        when(acl.list(anyString())).thenReturn(aclList);
        when(acl.delete(anyString(), anyString())).thenReturn(aclDelete);

        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(oAuthService.authorizeProject(any(Project.class), anyString())).thenReturn(calendarToken);
        when(oAuthService.generateCredential(any(CalendarToken.class))).thenReturn(expectedCredential);

        when(env.getProperty("google.applicationName")).thenReturn("TestApp");

        aclDto = AclDto.builder()
                .id("1")
                .role(AclRole.READER)
                .scope(ScopeDto.builder().type(AclScopeType.USER).value("user@example.com").build())
                .build();

        eventDto = CalendarEventDto.builder()
                .id("1")
                .summary("Summary")
                .description("Description")
                .location("Location")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(2))
                .build();

        calendarDto = CalendarDto.builder()
                .id("1")
                .summary("Summary")
                .description("Description")
                .location("Location")
                .build();

        expectedDto = CalendarEventDto.builder()
                .id("1")
                .summary("Summary")
                .description("Description")
                .location("Location")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(2))
                .build();

        event = new Event();
    }

    @Test
    void testAuthSuccess() {
        Credential actualCredential = calendarService.auth(1L, "code");
        assertEquals(expectedCredential, actualCredential);
        verify(projectRepository).findById(1L);
        verify(oAuthService).authorizeProject(project, "code");
        verify(oAuthService).generateCredential(calendarToken);
    }

    @Test
    void testAuthProjectNotFound() {
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> calendarService.auth(1L, "code"));
        verify(projectRepository).findById(1L);
        verify(oAuthService, never()).authorizeProject(any(), anyString());
        verify(oAuthService, never()).generateCredential(any());
    }

    @Test
    void testCreateEventSuccess() throws IOException {
        Event savedEvent = new Event();

        when(eventMapper.toModel(eventDto)).thenReturn(event);
        when(eventsInsert.execute()).thenReturn(savedEvent);
        when(eventMapper.toDto(savedEvent)).thenReturn(expectedDto);

        CalendarServiceImpl spyService = spy(calendarService);
        doReturn(googleCalendar).when(spyService).buildCalendar(anyLong());

        CalendarEventDto actualDto = spyService.createEvent(1L, "calendarId", eventDto);

        assertEquals(expectedDto, actualDto);
        verify(eventMapper).toModel(eventDto);
        verify(eventMapper).toDto(savedEvent);
    }

    @Test
    void testCreateEventThrowsRuntimeException() throws IOException {
        when(eventMapper.toModel(eventDto)).thenReturn(event);
        when(eventsInsert.execute()).thenThrow(new IOException("IO Exception"));

        CalendarServiceImpl spyService = spy(calendarService);
        doReturn(googleCalendar).when(spyService).buildCalendar(anyLong());

        assertThrows(RuntimeException.class, () -> spyService.createEvent(1L, "calendarId", eventDto));

        verify(eventMapper).toModel(eventDto);
    }

    @Test
    void testGetEventsSuccess() throws IOException {
        List<Event> eventList = Collections.singletonList(event);
        List<CalendarEventDto> expectedDtos = Collections.singletonList(eventDto);

        com.google.api.services.calendar.model.Events eventsResponse = new com.google.api.services.calendar.model.Events();
        eventsResponse.setItems(eventList);

        when(eventsList.setMaxResults(anyInt())).thenReturn(eventsList);
        when(eventsList.execute()).thenReturn(eventsResponse);
        when(eventMapper.toDtos(eventList)).thenReturn(expectedDtos);

        CalendarServiceImpl spyService = spy(calendarService);
        doReturn(googleCalendar).when(spyService).buildCalendar(anyLong());

        List<CalendarEventDto> actualDtos = spyService.getEvents(1L, "calendarId");

        assertEquals(expectedDtos, actualDtos);
        verify(eventMapper).toDtos(eventList);
    }



    @Test
    void testGetEventsThrowsRuntimeException() throws IOException {
        when(eventsList.setMaxResults(anyInt())).thenReturn(eventsList);
        when(eventsList.execute()).thenThrow(new IOException("IO Exception"));

        CalendarServiceImpl spyService = spy(calendarService);
        doReturn(googleCalendar).when(spyService).buildCalendar(anyLong());

        assertThrows(RuntimeException.class, () -> spyService.getEvents(1L, "calendarId"));
    }

    @Test
    void testDeleteEventSuccess() throws IOException {
        long projectId = 1L;
        String calendarId = "calendarId";
        long eventId = 1L;

        when(events.delete(calendarId, String.valueOf(eventId))).thenReturn(eventsDelete);

        CalendarServiceImpl spyService = spy(calendarService);
        doReturn(googleCalendar).when(spyService).buildCalendar(projectId);

        spyService.deleteEvent(projectId, calendarId, eventId);

        verify(events).delete(calendarId, String.valueOf(eventId));
        verify(eventsDelete).execute();
    }

    @Test
    void testDeleteEventThrowsRuntimeException() throws IOException {
        long projectId = 1L;
        String calendarId = "calendarId";
        long eventId = 1L;

        when(events.delete(calendarId, String.valueOf(eventId))).thenReturn(eventsDelete);
        when(eventsDelete.execute()).thenThrow(new IOException("IO Exception"));

        CalendarServiceImpl spyService = spy(calendarService);
        doReturn(googleCalendar).when(spyService).buildCalendar(projectId);

        assertThrows(RuntimeException.class, () -> spyService.deleteEvent(projectId, calendarId, eventId));
    }

    @Test
    void testCreateCalendarSuccess() throws IOException {
        com.google.api.services.calendar.model.Calendar newCalendar = new com.google.api.services.calendar.model.Calendar();
        com.google.api.services.calendar.model.Calendar createdCalendar = new com.google.api.services.calendar.model.Calendar();
        CalendarDto expectedDto = CalendarDto.builder().build();

        when(calendarMapper.toModel(calendarDto)).thenReturn(newCalendar);
        when(calendarsInsert.execute()).thenReturn(createdCalendar);
        when(calendarMapper.toDto(createdCalendar)).thenReturn(expectedDto);

        CalendarServiceImpl spyService = spy(calendarService);
        doReturn(googleCalendar).when(spyService).buildCalendar(anyLong());

        CalendarDto actualDto = spyService.createCalendar(1L, calendarDto);

        assertEquals(expectedDto, actualDto);
        verify(calendarMapper).toModel(calendarDto);
        verify(calendarMapper).toDto(createdCalendar);
    }

    @Test
    void testCreateCalendarThrowsRuntimeException() throws IOException {
        com.google.api.services.calendar.model.Calendar newCalendar = new com.google.api.services.calendar.model.Calendar();

        when(calendarMapper.toModel(calendarDto)).thenReturn(newCalendar);
        when(calendarsInsert.execute()).thenThrow(new IOException("IO Exception"));

        CalendarServiceImpl spyService = spy(calendarService);
        doReturn(googleCalendar).when(spyService).buildCalendar(anyLong());

        assertThrows(RuntimeException.class, () -> spyService.createCalendar(1L, calendarDto));
    }

    @Test
    void testGetCalendarSuccess() throws IOException {
        long projectId = 1L;
        String calendarId = "calendarId";
        com.google.api.services.calendar.model.Calendar calendarModel = new com.google.api.services.calendar.model.Calendar();
        CalendarDto expectedDto = CalendarDto.builder().build();

        when(calendarsGet.execute()).thenReturn(calendarModel);
        when(calendarMapper.toDto(calendarModel)).thenReturn(expectedDto);

        CalendarServiceImpl spyService = spy(calendarService);
        doReturn(googleCalendar).when(spyService).buildCalendar(anyLong());

        CalendarDto actualDto = spyService.getCalendar(projectId, calendarId);

        assertEquals(expectedDto, actualDto);
        verify(calendarMapper).toDto(calendarModel);
    }

    @Test
    void testGetCalendarThrowsRuntimeException() throws IOException {
        long projectId = 1L;
        String calendarId = "calendarId";

        when(calendarsGet.execute()).thenThrow(new IOException("IO Exception"));

        CalendarServiceImpl spyService = spy(calendarService);
        doReturn(googleCalendar).when(spyService).buildCalendar(anyLong());

        assertThrows(RuntimeException.class, () -> spyService.getCalendar(projectId, calendarId));
    }

    @Test
    void testCreateAclSuccess() throws IOException {
        long projectId = 1L;
        String calendarId = "calendarId";
        AclRule newAcl = new AclRule();
        AclRule createdAcl = new AclRule();
        AclDto expectedDto = AclDto.builder().build();

        when(aclMapper.toModel(aclDto)).thenReturn(newAcl);
        when(aclInsert.execute()).thenReturn(createdAcl);
        when(aclMapper.toDto(createdAcl)).thenReturn(expectedDto);

        CalendarServiceImpl spyService = spy(calendarService);
        doReturn(googleCalendar).when(spyService).buildCalendar(anyLong());

        AclDto actualDto = spyService.createAcl(projectId, calendarId, aclDto);

        assertEquals(expectedDto, actualDto);
        verify(aclMapper).toModel(aclDto);
        verify(aclMapper).toDto(createdAcl);
    }

    @Test
    void testCreateAclThrowsRuntimeException() throws IOException {
        long projectId = 1L;
        String calendarId = "calendarId";
        AclRule newAcl = new AclRule();

        when(aclMapper.toModel(aclDto)).thenReturn(newAcl);
        when(aclInsert.execute()).thenThrow(new IOException("IO Exception"));

        CalendarServiceImpl spyService = spy(calendarService);
        doReturn(googleCalendar).when(spyService).buildCalendar(anyLong());

        assertThrows(RuntimeException.class, () -> spyService.createAcl(projectId, calendarId, aclDto));
    }

    @Test
    void testListAclSuccess() throws IOException {
        long projectId = 1L;
        String calendarId = "calendarId";
        List<AclRule> aclRuleList = Collections.singletonList(new AclRule());
        AclDto aclDto = AclDto.builder().build();
        List<AclDto> expectedDtos = Collections.singletonList(aclDto);

        com.google.api.services.calendar.model.Acl aclResponse = new com.google.api.services.calendar.model.Acl();
        aclResponse.setItems(aclRuleList);

        when(aclList.execute()).thenReturn(aclResponse);
        when(aclMapper.toDtos(aclRuleList)).thenReturn(expectedDtos);

        CalendarServiceImpl spyService = spy(calendarService);
        doReturn(googleCalendar).when(spyService).buildCalendar(anyLong());

        List<AclDto> actualDtos = spyService.listAcl(projectId, calendarId);

        assertEquals(expectedDtos, actualDtos);
        verify(aclMapper).toDtos(aclRuleList);
    }


    @Test
    void testListAclThrowsRuntimeException() throws IOException {
        long projectId = 1L;
        String calendarId = "calendarId";

        when(aclList.execute()).thenThrow(new IOException("IO Exception"));

        CalendarServiceImpl spyService = spy(calendarService);
        doReturn(googleCalendar).when(spyService).buildCalendar(anyLong());

        assertThrows(RuntimeException.class, () -> spyService.listAcl(projectId, calendarId));
    }

    @Test
    void testDeleteAclSuccess() throws IOException {
        long projectId = 1L;
        String calendarId = "calendarId";
        long aclId = 1L;

        when(acl.delete(calendarId, String.valueOf(aclId))).thenReturn(aclDelete);

        CalendarServiceImpl spyService = spy(calendarService);
        doReturn(googleCalendar).when(spyService).buildCalendar(projectId);

        spyService.deleteAcl(projectId, calendarId, aclId);

        verify(acl).delete(calendarId, String.valueOf(aclId));
        verify(aclDelete).execute();
    }

    @Test
    void testDeleteAclThrowsRuntimeException() throws IOException {
        long projectId = 1L;
        String calendarId = "calendarId";
        long aclId = 1L;

        when(acl.delete(calendarId, String.valueOf(aclId))).thenReturn(aclDelete);
        when(aclDelete.execute()).thenThrow(new IOException("IO Exception"));

        CalendarServiceImpl spyService = spy(calendarService);
        doReturn(googleCalendar).when(spyService).buildCalendar(projectId);

        assertThrows(RuntimeException.class, () -> spyService.deleteAcl(projectId, calendarId, aclId));
    }

    @Test
    void testBuildCalendarThrowsNoCredentialsException() {
        long projectId = 1L;

        CalendarServiceImpl spyService = spy(calendarService);
        doThrow(new NoCredentialsException("No credentials")).when(spyService).auth(projectId, null);

        assertThrows(NoCredentialsException.class, () -> spyService.buildCalendar(projectId));
    }
}
