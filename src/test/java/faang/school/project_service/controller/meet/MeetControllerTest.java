package faang.school.project_service.controller.meet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.projectservice.controller.meet.MeetController;
import faang.school.projectservice.dto.meet.CreateMeetDto;
import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.dto.meet.UpdateMeetDto;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.service.meet.MeetServiceImpl;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MeetControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String basePath = "/meets";
    private final MeetDto meetDtoOne = MeetDto.builder()
            .id(1L)
            .title("first")
            .build();

    private final MeetDto meetDtoTwo = MeetDto.builder()
            .id(2L)
            .title("second")
            .build();
    private final List<MeetDto> meets = List.of(meetDtoOne, meetDtoTwo);
    private final List<String> meetsTitles = meets.stream().map(MeetDto::title).toList();
    private MockMvc mockMvc;

    @Mock
    private MeetServiceImpl meetService;
    @InjectMocks
    private MeetController meetController;

    @BeforeEach
    void setup() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(meetController).build();
    }

    @Test
    void testCreate() throws Exception {
        CreateMeetDto createMeetDto = CreateMeetDto.builder()
                .title("some title")
                .description("some desc")
                .userIds(List.of(1L, 2L, 3L))
                .startsAt(LocalDateTime.now().plusMonths(1))
                .projectId(23L)
                .build();

        MeetDto meetDto = MeetDto.builder()
                .title(createMeetDto.title())
                .description(createMeetDto.description())
                .build();

        when(meetService.create(createMeetDto)).thenReturn(meetDto);

        mockMvc.perform(MockMvcRequestBuilders.post(basePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createMeetDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath(MeetDto.Fields.title, Matchers.equalTo(meetDto.title())))
                .andExpect(jsonPath(MeetDto.Fields.description, Matchers.equalTo(meetDto.description())));
    }

    @Test
    void testUpdate() throws Exception {
        long id = 1L;

        UpdateMeetDto updateMeetDto = UpdateMeetDto.builder()
                .title("new title")
                .status(MeetStatus.CANCELLED)
                .build();

        MeetDto meetDto = MeetDto.builder()
                .title(updateMeetDto.title())
                .status(updateMeetDto.status())
                .build();

        when(meetService.update(id, updateMeetDto)).thenReturn(meetDto);

        mockMvc.perform(MockMvcRequestBuilders.patch(basePath + "/{meetId}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateMeetDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(MeetDto.Fields.title, Matchers.equalTo(meetDto.title())))
                .andExpect(jsonPath(MeetDto.Fields.status, Matchers.equalTo(meetDto.status().toString())));
    }

    @Test
    void testGetByFilters() throws Exception {
        MeetFilterDto meetFilterDto = MeetFilterDto.builder()
                .title("first second")
                .startsAt(LocalDateTime.now())
                .build();

        when(meetService.getByFilters(meetFilterDto)).thenReturn(List.of(meetDtoOne, meetDtoTwo));

        String response = mockMvc.perform(MockMvcRequestBuilders.get(basePath + "/filter")
                        .param(MeetFilterDto.Fields.title, String.valueOf(meetFilterDto.title()))
                        .param(MeetFilterDto.Fields.startsAt, String.valueOf(meetFilterDto.startsAt())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(meets.size())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertMeetTitles(response);
    }

    @Test
    void testGetById() throws Exception {
        when(meetService.getById(meetDtoOne.id())).thenReturn(meetDtoOne);

        mockMvc.perform(MockMvcRequestBuilders.get(basePath + "/{meetId}", meetDtoOne.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath(MeetDto.Fields.title, Matchers.equalTo(meetDtoOne.title())))
                .andExpect(jsonPath(MeetDto.Fields.id, Matchers.equalTo(meetDtoOne.id().intValue())));
    }

    @Test
    void testGetAll() throws Exception {
        when(meetService.getAll()).thenReturn(meets);

        String response = mockMvc.perform(MockMvcRequestBuilders.get(basePath))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(meets.size())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertMeetTitles(response);
    }

    @Test
    void testDelete() throws Exception {
        long meetId = 1L;
        mockMvc.perform(MockMvcRequestBuilders.delete(basePath + "/{meetId}", meetId))
                .andExpect(status().isOk());

        verify(meetService).delete(meetId);
    }

    private void assertMeetTitles(String response) throws JsonProcessingException {
        List<MeetDto> actualMeets = objectMapper.readValue(response,
                objectMapper.getTypeFactory().constructCollectionType(List.class, MeetDto.class));

        Assertions.assertTrue(actualMeets.stream().map(MeetDto::title).toList().containsAll(meetsTitles));
    }
}