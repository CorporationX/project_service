package faang.school.projectservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.projectservice.dto.calendar.CalendarDto;
import faang.school.projectservice.dto.calendar.EventDto;
import faang.school.projectservice.service.CalendarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CalendarControllerTest {

    @Mock
    private CalendarService calendarService;

    @InjectMocks
    private CalendarController calendarController;
    private MockMvc mockMvc;
    private long projectId;
    private String calendarId;
    private String eventId;
    private String email;
    private String role;
    private String scopeType;
    private String ruleId;

    private CalendarDto calendarDto;
    private String calendarDtoJson;
    private EventDto eventDto;
    private String eventDtoJson;
    private List<EventDto> eventDtoList;
    private String eventDtoListJson;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        projectId = 1L;
        calendarId = "calendarId";
        eventId = "eventId";
        email = "email@mail.com";
        role = "owner";
        scopeType = "user";
        ruleId = "ruleId";
        String calendarSummary = "calendarSummary";
        String eventSummary = "eventSummary";

        calendarDto = CalendarDto.builder()
                .summary(calendarSummary).build();
        eventDto = EventDto.builder()
                .summary(eventSummary)
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now())
                .build();
        eventDtoList = List.of(eventDto);
        mockMvc = MockMvcBuilders.standaloneSetup(calendarController).build();

        calendarDtoJson = objectMapper.writeValueAsString(calendarDto);
        eventDtoJson = objectMapper.writeValueAsString(eventDto);
        eventDtoListJson = objectMapper.writeValueAsString(eventDtoList);
    }

    @Test
    @DisplayName("testing createCalendarForProject endpoint")
    void testCreateCalendarForProject() throws Exception {
        mockMvc.perform(post("/api/v1/calendar")
                        .param("projectId", String.valueOf(projectId))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(calendarDtoJson))
                .andExpect(status().isCreated());
        verify(calendarService, times(1)).createCalendarForProject(projectId, calendarDto);
    }

    @Test
    @DisplayName("testing createEvent endpoint")
    void testCreateEvent() throws Exception {
        mockMvc.perform(post("/api/v1/calendar/{calendarId}/event", calendarId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(eventDtoJson))
                .andExpect(status().isCreated());
        verify(calendarService, times(1)).createEvent(calendarId, eventDto);
    }

    @Test
    @DisplayName("testing getEvent endpoint")
    void testGetEvent() throws Exception {
        mockMvc.perform(get("/api/v1/calendar/{calendarId}/event/{eventId}", calendarId, eventId))
                .andExpect(status().isOk());
        verify(calendarService, times(1)).getEvent(calendarId, eventId);
    }

    @Test
    @DisplayName("testing deleteEvent endpoint")
    void testDeleteEvent() throws Exception {
        mockMvc.perform(delete("/api/v1/calendar/{calendarId}/event/{eventId}", calendarId, eventId))
                .andExpect(status().isOk());
        verify(calendarService, times(1)).deleteEvent(calendarId, eventId);
    }

    @Test
    @DisplayName("testing createEvents endpoint")
    void testCreateEvents() throws Exception {
        mockMvc.perform(post("/api/v1/calendar/{calendarId}/events", calendarId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventDtoListJson))
                .andExpect(status().isCreated());
        verify(calendarService, times(1)).createEvents(calendarId, eventDtoList);
    }

    @Test
    @DisplayName("testing getEvents endpoint")
    void testGetEvents() throws Exception {
        mockMvc.perform(get("/api/v1/calendar/{calendarId}/events", calendarId))
                .andExpect(status().isOk());
        verify(calendarService, times(1)).getEvents(calendarId);
    }

    @Test
    @DisplayName("testing addAclRule endpoint")
    void testAddAclRule() throws Exception {
        mockMvc.perform(post("/api/v1/calendar/{calendarId}/acl", calendarId)
                        .param("email", email)
                        .param("role", role)
                        .param("scopeType", scopeType))
                .andExpect(status().isCreated());
        verify(calendarService, times(1)).addAclRule(calendarId, email, role, scopeType);
    }

    @Test
    @DisplayName("testing getAclRules endpoint")
    void testGetAclRules() throws Exception {
        mockMvc.perform(get("/api/v1/calendar/{calendarId}/acl", calendarId))
                .andExpect(status().isOk());
        verify(calendarService, times(1)).getAclRules(calendarId);
    }

    @Test
    @DisplayName("testing deleteAclRule endpoint")
    void testDeleteAclRule() throws Exception {
        mockMvc.perform(delete("/api/v1/calendar/{calendarId}/acl/{ruleId}", calendarId, ruleId))
                .andExpect(status().isOk());
        verify(calendarService, times(1)).deleteAclRule(calendarId, ruleId);
    }
}