package faang.school.projectservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.service.internship.InternshipContainer;
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

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class InternshipControllerTest {
    @InjectMocks
    private InternshipController controller;
    @Mock
    private InternshipService service;

    private ObjectMapper objectMapper;
    private InternshipContainer container = new InternshipContainer();
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

//    @Test
//    void testCreate() throws Exception {
//        // given
//        String uri = "/internships/";
//        List<Long> internIds = container.internIds();
//        Long createdBy = container.createdBy();
//        InternshipDto requestDto = container.validDto();
//        requestDto.setId(null);
//        requestDto.setUpdatedBy(createdBy);
//        InternshipDto responseDto = container.validDto();
//        responseDto.setUpdatedBy(createdBy);
//        when(service.create(requestDto)).thenReturn(responseDto);
//
//        String startDateString = dateString(container.startDate().toString());
//        String endDateString = dateString(container.endDate().toString());
//
//        // then
//        mockMvc.perform(post(uri)
//                        .content(objectMapper.writeValueAsString(requestDto))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(container.internshipId()))
//                .andExpect(jsonPath("$.mentorId").value(container.mentorId()))
//                .andExpect(jsonPath("$.projectId").value(container.projectId()))
//                .andExpect(jsonPath("$.scheduleId").value(container.scheduleId()))
//                .andExpect(jsonPath("$.internIds", hasSize(2)))
//                .andExpect(jsonPath("$.internIds[0]").value(internIds.get(0)))
//                .andExpect(jsonPath("$.internIds[1]").value(internIds.get(1)))
//                .andExpect(jsonPath("$.startDate").value(startDateString))
//                .andExpect(jsonPath("$.endDate").value(endDateString))
//                .andExpect(jsonPath("$.status").value(container.statusInProgress().name()))
//                .andExpect(jsonPath("$.description").value(container.description()))
//                .andExpect(jsonPath("$.name").value(container.name()))
//                .andExpect(jsonPath("$.createdBy").value(container.createdBy()))
//                .andExpect(jsonPath("$.updatedBy").value(createdBy));
//    }
//
//    @Test
//    void testUpdate() throws Exception {
//        // given
//        String uri = "/internships/";
//        List<Long> internIds = container.internIds();
//        InternshipDto requestDto = container.validDto();
//        InternshipDto responseDto = container.validDto();
//        when(service.update(requestDto)).thenReturn(responseDto);
//
//        String startDateString = dateString(container.startDate().toString());
//        String endDateString = dateString(container.endDate().toString());
//
//        // then
//        mockMvc.perform(put(uri)
//                        .content(objectMapper.writeValueAsString(requestDto))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(container.internshipId()))
//                .andExpect(jsonPath("$.mentorId").value(container.mentorId()))
//                .andExpect(jsonPath("$.projectId").value(container.projectId()))
//                .andExpect(jsonPath("$.scheduleId").value(container.scheduleId()))
//                .andExpect(jsonPath("$.internIds", hasSize(2)))
//                .andExpect(jsonPath("$.internIds[0]").value(internIds.get(0)))
//                .andExpect(jsonPath("$.internIds[1]").value(internIds.get(1)))
//                .andExpect(jsonPath("$.startDate").value(startDateString))
//                .andExpect(jsonPath("$.endDate").value(endDateString))
//                .andExpect(jsonPath("$.status").value(container.statusInProgress().name()))
//                .andExpect(jsonPath("$.description").value(container.description()))
//                .andExpect(jsonPath("$.name").value(container.name()))
//                .andExpect(jsonPath("$.createdBy").value(container.createdBy()))
//                .andExpect(jsonPath("$.updatedBy").value(container.updatedBy()));
//    }

    @Test
    void testGetInternship() throws Exception {
        // given
        String uri = "/internships/" + container.internshipId();
        Long internshipId = container.internshipId();
        String name = container.name();

        InternshipDto responseDto = InternshipDto.builder()
                .id(internshipId)
                .name(name)
                .build();

        when(service.getInternship(internshipId)).thenReturn(responseDto);

        // then
        mockMvc.perform(get(uri))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(internshipId))
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    void testGetFilteredInternship() throws Exception {
        // given
        String uri = "/internships/filtered";
        InternshipFilterDto requestFilters = container.filters();
        InternshipDto firstDto = container.validDto();
        InternshipDto secondDto = container.validDto();
        secondDto.setId(firstDto.getId() + 1);
        List<InternshipDto> responseFilteredDtos = List.of(firstDto, secondDto);

        when(service.getFilteredInternships(requestFilters)).thenReturn(responseFilteredDtos);

        // then
        mockMvc.perform(get(uri)
                        .content(objectMapper.writeValueAsString(requestFilters))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(firstDto.getId()))
                .andExpect(jsonPath("$[1].id").value(secondDto.getId()));
    }

    @Test
    void testGetAllInternships() throws Exception {
        // given
        String uri = "/internships/all";
        InternshipDto firstDto = container.validDto();
        InternshipDto secondDto = container.validDto();
        secondDto.setId(firstDto.getId() + 1);
        List<InternshipDto> responseDtos = List.of(firstDto, secondDto);

        when(service.getAllInternships()).thenReturn(responseDtos);

        // then
        mockMvc.perform(get(uri))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(firstDto.getId()))
                .andExpect(jsonPath("$[1].id").value(secondDto.getId()));
    }

    private String dateString(String stringDate) {
        return stringDate.substring(0, stringDate.length() - 2);
    }
}