package faang.school.project_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.projectservice.controller.internship.InternshipController;
import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.UpdateInternshipDto;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.internship.InternshipServiceImpl;
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
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class InternshipControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String basePath = "/internships";
    private MockMvc mockMvc;

    @Mock
    private InternshipServiceImpl internshipService;
    @InjectMocks
    private InternshipController internshipController;

    @BeforeEach
    void setup() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(internshipController).build();
    }

    @Test
    void testCreate() throws Exception {
        CreateInternshipDto createInternshipDto = CreateInternshipDto.builder()
                .projectId(1L)
                .mentorId(2L)
                .internsIds(List.of(3L, 4L))
                .name("New internship")
                .role(TeamRole.ANALYST)
                .startDate(LocalDateTime.now().plusDays(6))
                .endDate(LocalDateTime.now().plusMonths(3))
                .description("some desc")
                .build();

        InternshipDto internshipDto = InternshipDto.builder()
                .name(createInternshipDto.name())
                .role(createInternshipDto.role())
                .build();

        when(internshipService.create(createInternshipDto)).thenReturn(internshipDto);

        mockMvc.perform(MockMvcRequestBuilders.post(basePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInternshipDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath(InternshipDto.Fields.name, Matchers.equalTo(internshipDto.name())))
                .andExpect(jsonPath(InternshipDto.Fields.role, Matchers.equalTo(internshipDto.role().toString())));
    }

    @Test
    void testUpdate() throws Exception {
        long id = 1L;
        UpdateInternshipDto updateInternshipDto = UpdateInternshipDto.builder()
                .mentorId(2L)
                .status(InternshipStatus.CREATED)
                .build();

        InternshipDto internshipDto = InternshipDto.builder()
                .mentorId(updateInternshipDto.mentorId())
                .status(updateInternshipDto.status())
                .build();

        when(internshipService.update(id, updateInternshipDto)).thenReturn(internshipDto);

        mockMvc.perform(MockMvcRequestBuilders.patch(basePath + "/{internshipId}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateInternshipDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(InternshipDto.Fields.mentorId, Matchers.equalTo(internshipDto.mentorId().intValue())))
                .andExpect(jsonPath(InternshipDto.Fields.status, Matchers.equalTo(internshipDto.status().toString())));
    }

    @Test
    void testGetByFilters() throws Exception {
        InternshipFilterDto internshipFilterDto = new InternshipFilterDto(TeamRole.ANALYST, InternshipStatus.CREATED);

        InternshipDto internshipDtoOne = InternshipDto.builder()
                .mentorId(1L)
                .status(InternshipStatus.CREATED)
                .build();

        InternshipDto internshipDtoTwo = InternshipDto.builder()
                .mentorId(2L)
                .status(InternshipStatus.IN_PROGRESS)
                .build();

        List<InternshipDto> expectedInternshipsDto = new ArrayList<>(List.of(internshipDtoOne, internshipDtoTwo));

        when(internshipService.getByFilters(internshipFilterDto)).thenReturn(expectedInternshipsDto);

        String response = mockMvc.perform(MockMvcRequestBuilders.get(basePath)
                        .param(InternshipFilterDto.Fields.role, String.valueOf(internshipFilterDto.role()))
                        .param(InternshipFilterDto.Fields.status, String.valueOf(internshipFilterDto.status())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(expectedInternshipsDto.size())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<InternshipDto> actualUsers = objectMapper.readValue(response,
                objectMapper.getTypeFactory().constructCollectionType(List.class, InternshipDto.class));

        Assertions.assertTrue(actualUsers.containsAll(expectedInternshipsDto));
    }

    @Test
    void testGetById() throws Exception {
        long id = 1L;
        InternshipDto internshipDto = InternshipDto.builder()
                .mentorId(1L)
                .status(InternshipStatus.CREATED)
                .build();

        when(internshipService.getById(id)).thenReturn(internshipDto);

        mockMvc.perform(MockMvcRequestBuilders.get(basePath + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath(InternshipDto.Fields.mentorId, Matchers.equalTo(internshipDto.mentorId().intValue())))
                .andExpect(jsonPath(InternshipDto.Fields.status, Matchers.equalTo(internshipDto.status().toString())));
    }
}