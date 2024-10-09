package faang.school.projectservice.controller.meet;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.model.dto.meet.MeetFilterDto;
import faang.school.projectservice.model.dto.meet.MeetRequestDto;
import faang.school.projectservice.model.dto.meet.MeetResponseDto;
import faang.school.projectservice.model.entity.meet.MeetStatus;
import faang.school.projectservice.service.impl.meet.MeetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MeetControllerTest {

    @Mock
    private MeetServiceImpl meetService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private MeetController meetController;

    private MockMvc mockMvc;

    private MeetRequestDto requestDto;
    private MeetResponseDto responseDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(meetController).build();

    }

    @Test
    void create_shouldReturnCreatedMeet() throws Exception {
        // given
        requestDto = MeetRequestDto.builder()
                .title("Team Meeting")
                .description("Discuss project updates")
                .status(MeetStatus.PENDING)
                .projectId(1L)
                .build();
        responseDto = MeetResponseDto.builder()
                .title("Team Meeting")
                .description("Discuss project updates")
                .status(MeetStatus.PENDING)
                .projectId(1L)
                .creatorId(1L)
                .build();
        when(userContext.getUserId()).thenReturn(1L);
        when(meetService.create(anyLong(), any(MeetRequestDto.class))).thenReturn(responseDto);
        // when & then
        mockMvc.perform(post("/api/v1/meets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Team Meeting",
                                    "description": "Discuss project updates",
                                    "status": "PENDING",
                                    "projectId": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Team Meeting"))
                .andExpect(jsonPath("$.description").value("Discuss project updates"))
                .andExpect(jsonPath("$.status").value("PENDING"));
        verify(meetService).create(anyLong(), any(MeetRequestDto.class));
    }

    @Test
    void update_shouldReturnUpdatedMeet() throws Exception {
        // given
        requestDto = MeetRequestDto.builder()
                .id(1L)
                .title("Updated Meeting")
                .description("Discuss new features")
                .status(MeetStatus.PENDING)
                .projectId(1L)
                .build();
        responseDto = MeetResponseDto.builder()
                .id(1L)
                .title("Updated Meeting")
                .description("Discuss new features")
                .status(MeetStatus.PENDING)
                .projectId(1L)
                .creatorId(1L)
                .build();
        when(userContext.getUserId()).thenReturn(1L);
        when(meetService.update(anyLong(), any(MeetRequestDto.class))).thenReturn(responseDto);
        // when & then
        mockMvc.perform(put("/api/v1/meets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": 1,
                                    "title": "Updated Meeting",
                                    "description": "Discuss new features",
                                    "status": "PENDING",
                                    "projectId": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Meeting"))
                .andExpect(jsonPath("$.description").value("Discuss new features"))
                .andExpect(jsonPath("$.status").value("PENDING"));
        verify(meetService).update(anyLong(), any(MeetRequestDto.class));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        // given
        when(userContext.getUserId()).thenReturn(1L);
        doNothing().when(meetService).delete(anyLong(), anyLong());
        // when & then
        mockMvc.perform(delete("/api/v1/meets/{id}", 1L))
                .andExpect(status().isNoContent());
        verify(meetService).delete(anyLong(), eq(1L));
    }

    @Test
    void findAllByProjectIdFilter_shouldReturnMeetList() throws Exception {
        // given
        responseDto = MeetResponseDto.builder()
                .id(1L)
                .title("Team Meeting")
                .description("Discuss project updates")
                .status(MeetStatus.PENDING)
                .projectId(1L)
                .creatorId(1L)
                .build();
        when(meetService.findAllByProjectIdFilter(anyLong(), any(MeetFilterDto.class)))
                .thenReturn(Collections.singletonList(responseDto));
        // when & then
        mockMvc.perform(get("/api/v1/meets/projects/{projectId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Team Meeting"))
                .andExpect(jsonPath("$[0].description").value("Discuss project updates"))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
        verify(meetService).findAllByProjectIdFilter(anyLong(), any(MeetFilterDto.class));
    }

    @Test
    void findById_shouldReturnMeet() throws Exception {
        // given
        responseDto = MeetResponseDto.builder()
                .id(1L)
                .title("Team Meeting")
                .description("Discuss project updates")
                .status(MeetStatus.PENDING)
                .projectId(1L)
                .creatorId(1L)
                .build();
        when(meetService.findById(anyLong())).thenReturn(responseDto);
        // when & then
        mockMvc.perform(get("/api/v1/meets/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Team Meeting"))
                .andExpect(jsonPath("$.description").value("Discuss project updates"))
                .andExpect(jsonPath("$.status").value("PENDING"));
        verify(meetService).findById(anyLong());
    }
}