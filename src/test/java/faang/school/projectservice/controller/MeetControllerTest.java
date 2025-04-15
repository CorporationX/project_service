package faang.school.projectservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.MeetDto;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.service.MeetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {MeetController.class, UserContext.class})
class MeetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserContext userContext;

    @MockBean
    private MeetService meetService;

    private MeetDto meetDto;

    private final ObjectMapper objectMapper = new ObjectMapper();

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
        when(userContext.getUserId()).thenReturn(1L);
        when(meetService.updateMeet(1L, meetDto, 1L)).thenReturn(meetDto);

        mockMvc.perform(put("/meets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(meetDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Test Meet"));
    }

    @Test
    void cancelMeet_ShouldReturnCancelledMeetDto() throws Exception {
        meetDto = MeetDto.builder().status(MeetStatus.CANCELLED).build();
        when(userContext.getUserId()).thenReturn(1L);
        when(meetService.cancelMeet(1L, 1L)).thenReturn(meetDto);

        mockMvc.perform(patch("/meets/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void deleteMeet_ShouldReturnNoContent() throws Exception {
        when(userContext.getUserId()).thenReturn(1L);
        doNothing().when(meetService).deleteMeet(1L, 1L);

        mockMvc.perform(delete("/meets/1"))
                .andExpect(status().isNoContent());
    }
}
