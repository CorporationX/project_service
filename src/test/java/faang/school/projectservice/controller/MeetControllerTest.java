package faang.school.projectservice.controller;

import faang.school.projectservice.dto.MeetDto;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.service.MeetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MeetControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MeetService meetService;

    private MeetDto meetDto;

    @BeforeEach
    void setUp() {
        meetDto = MeetDto.builder()
                .id(1L)
                .title("Test Meet")
                .description("Test Description")
                .status(MeetStatus.PENDING)
                .creatorId(1L)
                .projectId(1L)
                .startsAt(null)
                .build();
    }

    @Test
    void getAllMeets_ShouldReturnMeetDtoList() throws Exception {
        when(meetService.getAllMeets()).thenReturn(List.of(meetDto));

        mockMvc.perform(get("/meets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Meet"));
    }

    @Test
    void getMeetById_ShouldReturnMeetDto() throws Exception {
        when(meetService.getMeetById(1L)).thenReturn(meetDto);

        mockMvc.perform(get("/meets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Meet"));
    }

    @Test
    void createMeet_ShouldReturnCreatedMeetDto() throws Exception {
        when(meetService.createMeet(any(MeetDto.class))).thenReturn(meetDto);

        mockMvc.perform(post("/meets")
                        .contentType("application/json")
                        .content("{\"title\":\"Test Meet\",\"description\":\"Test Description\",\"status\":\"PENDING\",\"creatorId\":1,\"projectId\":1}")
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Meet"));
    }

    @Test
    void updateMeet_ShouldReturnUpdatedMeetDto() throws Exception {
        meetDto = MeetDto.builder().title("Updated Test Meet").build();
        when(meetService.updateMeet(anyLong(), any(MeetDto.class))).thenReturn(meetDto);

        mockMvc.perform(put("/meets/1")
                        .contentType("application/json")
                        .content("{\"title\":\"Updated Test Meet\",\"description\":\"Updated Description\",\"status\":\"PENDING\",\"creatorId\":1,\"projectId\":1}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Test Meet"));
    }

    @Test
    void cancelMeet_ShouldReturnCancelledMeetDto() throws Exception {
        meetDto = MeetDto.builder().status(MeetStatus.CANCELLED).build();
        when(meetService.cancelMeet(1L)).thenReturn(meetDto);

        mockMvc.perform(patch("/meets/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void deleteMeet_ShouldReturnNoContent() throws Exception {
        doNothing().when(meetService).deleteMeet(1L);

        mockMvc.perform(delete("/meets/1"))
                .andExpect(status().isNoContent());
    }
}
