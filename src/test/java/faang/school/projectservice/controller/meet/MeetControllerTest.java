package faang.school.projectservice.controller.meet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.meet.MeetCreateDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.dto.meet.MeetResponseDto;
import faang.school.projectservice.dto.meet.MeetUpdateDto;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.service.meet.interfaces.MeetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@ExtendWith(MockitoExtension.class)
//@WebMvcTest(controllers = MeetController.class)
@SpringBootTest
@AutoConfigureMockMvc
class MeetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserContext userContext;

    @MockBean
    private MeetService meetService;

    private ObjectMapper objectMapper;
    long projectId = 1L;
    long userId = 1L;
    long meetId = 1L;
    MeetCreateDto meetCreateDto;
    MeetUpdateDto meetUpdateDto;
    MeetFilterDto filterDto;
    MeetResponseDto meetResponseDto;


    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        meetCreateDto = MeetCreateDto.builder()
                .title("meet title")
                .description("meet description")
                .startsAt(LocalDateTime.of(2025, 4, 7, 10, 30))
                .projectId(projectId)
                .status(MeetStatus.PENDING)
                .build();
        meetUpdateDto = MeetUpdateDto.builder()
                .title("some meet title")
                .description("some meet description")
                .startsAt(LocalDateTime.of(2025, 4, 7, 12, 0))
                .projectId(projectId)
                .id(meetId)
                .build();
        meetResponseDto = MeetResponseDto.builder()
                .id(meetId)
                .title(meetCreateDto.getTitle())
                .description(meetCreateDto.getDescription())
                .projectId(meetCreateDto.getProjectId())
                .status(meetCreateDto.getStatus())
                .startsAt(meetCreateDto.getStartsAt())
                .creatorId(userId)
                .build();
    }

    @Test
    @DisplayName("Create meet when successful")
    void testCreateMeetWhenSuccessful() throws Exception {

        when(meetService.createMeet(any(MeetCreateDto.class)))
                .thenReturn(meetResponseDto);

        mockMvc.perform(post("/api/v1/projects/{projectId}/meets", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", userId)
                        .content(objectMapper.writeValueAsString(meetCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value(meetCreateDto.getTitle()))
                .andExpect(jsonPath("$.projectId").value(projectId));
        verify(meetService, Mockito.times(1)).createMeet(any(MeetCreateDto.class));
    }

    @Test
    @DisplayName("Create meet when projectId in path and body do not match")
    void testCreateMeetWhenProjectIdMismatch() throws Exception {
        meetCreateDto.setProjectId(2L);

        mockMvc.perform(post("/api/v1/projects/{projectId}/meets", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", userId)
                        .content(objectMapper.writeValueAsString(meetCreateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create meet with projectId less zero")
    void testCreateMeetWithInvalidProjectId() throws Exception {
        projectId = 123L;

        mockMvc.perform(post("/api/v1/projects/{projectId}/meets", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", userId)
                        .content(objectMapper.writeValueAsString(meetCreateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create meet without userId in header")
    void testCreateMeetWithoutUserId() throws Exception {
        userId = 0L;

        mockMvc.perform(post("/api/v1/projects/{projectId}/meets", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(meetCreateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateMeetWhenDtoIsInvalid() throws Exception {
        meetCreateDto.setStartsAt(null);

        mockMvc.perform(post("/api/v1/projects/1/meets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(meetCreateDto))
                        .header("x-user-id", 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateMeetWhenSuccessful() throws Exception {
        meetResponseDto.setTitle(meetUpdateDto.getTitle());
        meetResponseDto.setDescription(meetUpdateDto.getDescription());
        meetResponseDto.setStartsAt(meetUpdateDto.getStartsAt());
        when(meetService.updateMeet(any(MeetUpdateDto.class)))
                .thenReturn(meetResponseDto);

        mockMvc.perform(put("/api/v1/projects/{projectId}/meets/{meetId}", projectId, meetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", userId)
                        .content(objectMapper.writeValueAsString(meetUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(meetId))
                .andExpect(jsonPath("$.title").value(meetUpdateDto.getTitle()))
                .andExpect(jsonPath("$.projectId").value(projectId));

    }

    @Test
    void testDeleteMeetWhenSuccessful() throws Exception {
        mockMvc.perform(delete("/api/v1/projects/{projectId}/meets/{meetId}", projectId, meetId)
                        .header("x-user-id", userId))
                .andExpect(status().isNoContent());

        verify(meetService, Mockito.times(1)).deleteMeet(meetId);
    }

    @Test
    void testGetMeetWhenReturnMeet() throws Exception {
        when(meetService.getMeet(meetId))
                .thenReturn(meetResponseDto);

        mockMvc.perform(get("/api/v1/projects/{projectId}/meets/{meetId}", projectId, meetId)
                        .header("x-user-id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(meetId))
                .andExpect(jsonPath("$.title").value(meetResponseDto.getTitle()))
                .andExpect(jsonPath("$.projectId").value(projectId));

        verify(meetService, Mockito.times(1)).getMeet(meetId);
    }

    @Test
    void testGetMeetsByProjectIdWhenReturnMeets()  throws Exception {
        filterDto = MeetFilterDto.builder()
                .title("some title")
                .startDate(LocalDateTime.parse("2025-03-01T10:30:00"))
                .endDate(LocalDateTime.parse("2025-05-01T10:30:00"))
                .build();
        when(meetService.getMeetsByProjectId(projectId, filterDto))
                .thenReturn(java.util.List.of(meetResponseDto));

        mockMvc.perform(get("/api/v1/projects/{projectId}/meets", projectId)
                        .param("title", String.valueOf(filterDto.getTitle()))
                        .param("startDate", String.valueOf(filterDto.getStartDate()))
                        .param("endDate", String.valueOf(filterDto.getEndDate()))
                        .header("x-user-id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(meetId))
                .andExpect(jsonPath("$[0].title").value(meetResponseDto.getTitle()))
                .andExpect(jsonPath("$[0].projectId").value(projectId));

        verify(meetService, Mockito.times(1)).getMeetsByProjectId(projectId, filterDto);
    }
}