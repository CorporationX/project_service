package faang.school.projectservice.service;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Acl;
import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import faang.school.projectservice.dto.calendar.CalendarDto;
import faang.school.projectservice.dto.calendar.EventDto;
import faang.school.projectservice.mapper.CalendarMapper;
import faang.school.projectservice.mapper.EventMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalendarServiceTest {

    @Mock
    private Calendar googleCalendar;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private CalendarMapper calendarMapper;

    @Mock
    private ProjectValidator projectValidator;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private CalendarService calendarService;

    private long projectId;
    private String eventId;
    private String calendarId;
    private String email;
    private String role;
    private String ruleId;
    private String scopeType;
    private Project project;

    private CalendarDto calendarDto;
    private com.google.api.services.calendar.model.Calendar calendar;
    private EventDto eventDto;
    private Event event;
    private AclRule aclRule;
    private Calendar.Calendars calendars;
    private Calendar.Calendars.Insert insertCalendar;
    private Calendar.Events events;
    private Calendar.Events.Insert insertEvents;
    private Calendar.Events.Get getEvents;
    private Calendar.Events.Delete deleteEvents;
    private Calendar.Events.List listEvents;
    private Calendar.Acl acl;
    private Calendar.Acl.Insert aclInsert;
    private Calendar.Acl.List aclList;
    private Calendar.Acl.Delete aclDelete;


    @BeforeEach
    public void setUp() {
        projectId = 1L;
        eventId = "eventId";
        calendarId = "calendarId";
        email = "test@mail.com";
        role = "owner";
        ruleId = "ruleId";
        scopeType = "user";
        project = Project.builder()
                .id(projectId).build();
        String calendarSummary = "calendarSummary";

        calendarDto = CalendarDto.builder()
                .summary(calendarSummary).build();
        calendar = new com.google.api.services.calendar.model.Calendar().setSummary(calendarSummary);
        eventDto = EventDto.builder()
                .id(eventId)
                .build();
        event = new Event().setId(eventId);
        aclRule = new AclRule().setRole(role);

        calendars = mock(Calendar.Calendars.class);
        insertCalendar = mock(Calendar.Calendars.Insert.class);
        events = mock(Calendar.Events.class);
        insertEvents = mock(Calendar.Events.Insert.class);
        getEvents = mock(Calendar.Events.Get.class);
        deleteEvents = mock(Calendar.Events.Delete.class);
        listEvents = mock(Calendar.Events.List.class);
        acl = mock(Calendar.Acl.class);
        aclInsert = mock(Calendar.Acl.Insert.class);
        aclList = mock(Calendar.Acl.List.class);
        aclDelete = mock(Calendar.Acl.Delete.class);
    }

    private void setUpMockForCreateEvent() throws IOException {
        when(eventMapper.toEvent(eventDto)).thenReturn(event);
        when(googleCalendar.events()).thenReturn(events);
        when(events.insert(calendarId, event)).thenReturn(insertEvents);
        when(insertEvents.execute()).thenReturn(event);
        when(eventMapper.toDto(event)).thenReturn(eventDto);
    }

    @Test
    @DisplayName("testing createCalendarForProject")
    void testCreateCalendarForProject() throws IOException {
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(calendarMapper.toEntity(calendarDto)).thenReturn(calendar);
        when(googleCalendar.calendars()).thenReturn(calendars);
        when(calendars.insert(any(com.google.api.services.calendar.model.Calendar.class))).thenReturn(insertCalendar);
        when(insertCalendar.execute()).thenReturn(calendar);
        calendarService.createCalendarForProject(projectId, calendarDto);
        verify(projectRepository, times(1)).getProjectById(projectId);
        verify(projectValidator, times(1)).verifyProjectDoesNotHaveCalendar(project);
        verify(calendarMapper, times(1)).toEntity(calendarDto);
        verify(googleCalendar, times(1)).calendars();
        verify(calendars, times(1))
                .insert(any(com.google.api.services.calendar.model.Calendar.class));
        verify(insertCalendar, times(1)).execute();
        verify(projectRepository, times(1)).save(project);
        verify(calendarMapper, times(1)).toDto(calendar);
    }

    @Test
    @DisplayName("testing createEvent method")
    void testCreateEvent() throws IOException {
        setUpMockForCreateEvent();
        calendarService.createEvent(calendarId, eventDto);
        verify(eventMapper, times(1)).toEvent(eventDto);
        verify(googleCalendar, times(1)).events();
        verify(events, times(1)).insert(calendarId, event);
        verify(insertEvents, times(1)).execute();
        verify(eventMapper, times(1)).toEvent(eventDto);
    }

    @Test
    @DisplayName("testing getEvent method")
    void testGetEvent() throws IOException {
        when(googleCalendar.events()).thenReturn(events);
        when(events.get(calendarId, eventId)).thenReturn(getEvents);
        when(getEvents.execute()).thenReturn(event);
        calendarService.getEvent(calendarId, eventId);
        verify(googleCalendar, times(1)).events();
        verify(events, times(1)).get(calendarId, eventId);
        verify(getEvents, times(1)).execute();
        verify(eventMapper, times(1)).toDto(event);
    }

    @Test
    @DisplayName("testing deleteEvent method")
    void testDeleteEvent() throws IOException {
        when(googleCalendar.events()).thenReturn(events);
        when(events.delete(calendarId, eventId)).thenReturn(deleteEvents);
        calendarService.deleteEvent(calendarId, eventId);
        verify(googleCalendar, times(1)).events();
        verify(events, times(1)).delete(calendarId, eventId);
        verify(deleteEvents, times(1)).execute();
    }

    @Test
    @DisplayName("testing createEvents method")
    void testCreateEvents() throws IOException {
        setUpMockForCreateEvent();
        List<EventDto> eventDtoList = List.of(eventDto);

        List<EventDto> result = calendarService.createEvents(calendarId, eventDtoList);
        verify(eventMapper, times(1)).toEvent(eventDto);
        verify(googleCalendar, times(1)).events();
        verify(events, times(1)).insert(calendarId, event);
        verify(insertEvents, times(1)).execute();
        verify(eventMapper, times(1)).toDto(event);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("testing getEvents method")
    void testGetEvents() throws IOException {
        Events eventsResponse = new Events().setItems(List.of(event));
        when(googleCalendar.events()).thenReturn(events);
        when(events.list(calendarId)).thenReturn(listEvents);
        when(listEvents.execute()).thenReturn(eventsResponse);
        when(eventMapper.toDto(event)).thenReturn(eventDto);
        List<EventDto> eventsList = calendarService.getEvents(calendarId);
        verify(googleCalendar, times(1)).events();
        verify(events, times(1)).list(calendarId);
        verify(listEvents, times(1)).execute();
        verify(eventMapper, times(1)).toDto(event);
        assertEquals(1, eventsList.size());
    }

    @Test
    @DisplayName("testing addAclRule method")
    void testAddAclRule() throws IOException {
        when(googleCalendar.acl()).thenReturn(acl);
        when(acl.insert(eq(calendarId), any(AclRule.class))).thenReturn(aclInsert);
        calendarService.addAclRule(calendarId, email, role, scopeType);
        verify(googleCalendar, times(1)).acl();
        verify(acl, times(1)).insert(eq(calendarId), any(AclRule.class));
        verify(aclInsert, times(1)).execute();
    }

    @Test
    @DisplayName("testing getAclRules method")
    void testGetAclRules() throws IOException {
        Acl aclResponse = new Acl().setItems(List.of(aclRule));
        when(googleCalendar.acl()).thenReturn(acl);
        when(acl.list(calendarId)).thenReturn(aclList);
        when(aclList.execute()).thenReturn(aclResponse);
        List<AclRule> result = calendarService.getAclRules(calendarId);
        verify(googleCalendar, times(1)).acl();
        verify(acl, times(1)).list(calendarId);
        verify(aclList, times(1)).execute();
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("testing deleteAclRule method")
    void testDeleteAclRule() throws IOException {
        when(googleCalendar.acl()).thenReturn(acl);
        when(acl.delete(calendarId, ruleId)).thenReturn(aclDelete);
        calendarService.deleteAclRule(calendarId, ruleId);
        verify(googleCalendar, times(1)).acl();
        verify(acl, times(1)).delete(calendarId, ruleId);
        verify(aclDelete, times(1)).execute();
    }
}