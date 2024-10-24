package faang.school.projectservice.google.calendar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.projectservice.controller.GoogleCalendarController;
import faang.school.projectservice.model.dto.CalendarEventDto;
import faang.school.projectservice.model.dto.ZonedDateTimeDto;
import faang.school.projectservice.service.GoogleCalendarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GoogleCalendarControllerTest {

    @Mock
    private GoogleCalendarService googleCalendarService;

    @InjectMocks
    private GoogleCalendarController googleCalendarController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(googleCalendarController).build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testCreateEvent_Success() throws Exception {
        CalendarEventDto eventDto = new CalendarEventDto();
        eventDto.setId("1");
        eventDto.setSummary("Test Event");
        eventDto.setDescription("This is a test event");
        eventDto.setStartDate(new ZonedDateTimeDto(LocalDateTime.now(), "UTC"));
        eventDto.setEndDate(new ZonedDateTimeDto(LocalDateTime.now().plusHours(1), "UTC"));

        when(googleCalendarService.createEvent(any(CalendarEventDto.class))).thenReturn(eventDto);

        mockMvc.perform(post("/api/calendar/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.summary").value("Test Event"))
                .andExpect(jsonPath("$.description").value("This is a test event"));
    }

    @Test
    public void testUpdateEvent_Success() throws Exception {
        CalendarEventDto eventDto = new CalendarEventDto();
        eventDto.setId("1");
        eventDto.setSummary("Updated Event");
        eventDto.setDescription("This is an updated event");
        eventDto.setStartDate(new ZonedDateTimeDto(LocalDateTime.now(), "UTC"));
        eventDto.setEndDate(new ZonedDateTimeDto(LocalDateTime.now().plusHours(1), "UTC"));

        when(googleCalendarService.update(any(CalendarEventDto.class))).thenReturn(eventDto);

        mockMvc.perform(put("/api/calendar/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.summary").value("Updated Event"))
                .andExpect(jsonPath("$.description").value("This is an updated event"));
    }

    @Test
    public void testDeleteEvent_Success() throws Exception {
        doNothing().when(googleCalendarService).delete(anyString());

        mockMvc.perform(delete("/api/calendar/event/{calendarEventId}", "1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteEvent_NotFound() throws Exception {
        doNothing().when(googleCalendarService).delete(anyString());

        mockMvc.perform(delete("/api/calendar/event/{calendarEventId}", "non-existent-id"))
                .andExpect(status().isOk());
    }
}
