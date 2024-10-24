package faang.school.projectservice.meet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.projectservice.controller.MeetController;
import faang.school.projectservice.model.dto.ZonedDateTimeDto;
import faang.school.projectservice.model.dto.MeetDto;
import faang.school.projectservice.model.dto.MeetFilterDto;
import faang.school.projectservice.model.enums.MeetStatus;
import faang.school.projectservice.service.impl.MeetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MeetControllerTest {

    @Mock
    private MeetServiceImpl meetService;

    @InjectMocks
    private MeetController meetController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(meetController).build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    private MeetDto createValidMeetDto() {
        MeetDto meetDto = new MeetDto();
        meetDto.setTitle("Team Meeting");
        meetDto.setDescription("Description of the meeting");
        meetDto.setStartDate(new ZonedDateTimeDto(LocalDateTime.now(), "UTC"));
        meetDto.setEndDate(new ZonedDateTimeDto(LocalDateTime.now().plusHours(1), "UTC"));
        meetDto.setStatus(MeetStatus.CONFIRMED);
        meetDto.setCreatorId(1L);
        meetDto.setProjectId(1L);
        return meetDto;
    }

    @Test
    public void testCreateMeet_Success() throws Exception {
        MeetDto meetDto = createValidMeetDto();

        when(meetService.create(any(MeetDto.class))).thenReturn(meetDto);

        mockMvc.perform(post("/meets")
                        .header("x-user-id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(meetDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Team Meeting"))
                .andExpect(jsonPath("$.description").value("Description of the meeting"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    public void testUpdateMeet_Success() throws Exception {
        MeetDto meetDto = createValidMeetDto();
        meetDto.setTitle("Updated Meeting");

        when(meetService.update(anyLong(), any(MeetDto.class))).thenReturn(meetDto);

        mockMvc.perform(put("/meets/1")
                        .header("x-user-id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(meetDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Meeting"));
    }

    @Test
    public void testDeleteMeet_Success() throws Exception {
        doNothing().when(meetService).delete(anyLong());

        mockMvc.perform(delete("/meets/1")
                        .header("x-user-id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetMeetsByFilter_Success() throws Exception {
        MeetDto meetDto = createValidMeetDto();

        List<MeetDto> meets = Collections.singletonList(meetDto);
        MeetFilterDto filterDto = new MeetFilterDto();

        when(meetService.getByFilter(any(MeetFilterDto.class))).thenReturn(meets);

        mockMvc.perform(post("/meets/filter")
                        .header("x-user-id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Team Meeting"));
    }

    @Test
    public void testGetAllMeets_Success() throws Exception {
        MeetDto meetDto = createValidMeetDto();

        List<MeetDto> meets = Collections.singletonList(meetDto);
        Page<MeetDto> meetPage = new PageImpl<>(meets, PageRequest.of(0, 10), meets.size());

        when(meetService.getAll(any(Pageable.class))).thenReturn(meetPage);

        mockMvc.perform(get("/meets")
                        .header("x-user-id", "1")
                        .param("page", "0")  // Передаем параметры для пагинации
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Team Meeting"));
    }

    @Test
    public void testGetMeetById_Success() throws Exception {
        MeetDto meetDto = createValidMeetDto();

        when(meetService.getById(anyLong())).thenReturn(meetDto);

        mockMvc.perform(get("/meets/1")
                        .header("x-user-id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Team Meeting"));
    }
}
