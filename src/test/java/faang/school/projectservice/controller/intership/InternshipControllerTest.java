package faang.school.projectservice.controller.intership;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.service.internship.InternshipService;
import org.junit.jupiter.api.BeforeEach;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class InternshipControllerTest {
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Mock
    InternshipService internshipService;

    @InjectMocks
    InternshipController internshipController;

    private InternshipDto internshipDto;
    private InternshipDto internshipDtoAnswer;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(internshipController).build();

        internshipDto = InternshipDto.builder()
                .projectId(9L)
                .mentorId(15L)
                .internsId(List.of(4L, 5L, 7L))
                .name("Internship Name")
                .description("Internship description")
                .status(InternshipStatus.IN_PROGRESS)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .build();

        internshipDtoAnswer = InternshipDto.builder()
                .id(1L)
                .projectId(9L)
                .mentorId(15L)
                .internsId(List.of(4L, 5L, 7L))
                .name("Updated Internship")
                .description("Updated description")
                .status(InternshipStatus.COMPLETED)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .build();
    }

    @Test
    void testCreateInternship() throws Exception {
        doReturn(internshipDto).when(internshipService).createInternship(any(InternshipDto.class));
        var body = objectMapper.writeValueAsString(internshipDto);

        mockMvc.perform(post("/api/v1/internships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Internship Name"))
                .andExpect(jsonPath("$.description").value("Internship description"))
                .andExpect(jsonPath("$.projectId").value(9L));

    }

    @Test
    void testUpdateInternship() throws Exception {
        doReturn(internshipDtoAnswer).when(internshipService).updateInternship(anyLong(), any(InternshipDto.class));
        var body = objectMapper.writeValueAsString(internshipDto);

        mockMvc.perform(put("/api/v1/internships/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Internship"));
    }

    @Test
    void getAllInternshipsByFilter() throws Exception {
        doReturn(List.of(internshipDtoAnswer)).when(internshipService).getAllInternshipsByFilter(any(InternshipFilterDto.class));

        mockMvc.perform(get("/api/v1/internships/filter")
                .param("projectId", "9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Updated Internship"));
    }

    @Test
    void testGetAllInternships() throws Exception {
        InternshipDto internship1 = InternshipDto.builder()
                .projectId(9L)
                .mentorId(15L)
                .internsId(List.of(4L, 5L, 7L))
                .name("Internship 1")
                .description("Description 1")
                .status(InternshipStatus.IN_PROGRESS)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .build();

        InternshipDto internship2 = InternshipDto.builder()
                .projectId(10L)
                .mentorId(16L)
                .internsId(List.of(6L, 8L))
                .name("Internship 2")
                .description("Description 2")
                .status(InternshipStatus.IN_PROGRESS)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .build();

        doReturn(List.of(internship1, internship2)).when(internshipService).getAllInternshipsByFilter(any(InternshipFilterDto.class));

        mockMvc.perform(get("/api/v1/internships/filter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Internship 1"))
                .andExpect(jsonPath("$[1].name").value("Internship 2"));
    }

    @Test
    void testGetInternshipById() throws Exception {
        doReturn(internshipDtoAnswer).when(internshipService).getInternshipById(anyLong());

        mockMvc.perform(get("/api/v1/internships/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Internship"));
    }
}