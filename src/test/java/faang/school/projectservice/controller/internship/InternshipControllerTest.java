package faang.school.projectservice.controller.internship;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.filter.internship.InternshipFilterDto;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.internship.InternshipService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@WebMvcTest
@ContextConfiguration(classes = {InternshipController.class})
public class InternshipControllerTest {
    private final static String POST_URL = "/internship";
    private final static String PUT_URL = "/internship/update";
    private final static String GET_URL_BY_STATUS = "/internship/status";
    private final static String GET_URL_LIST_DTO = "/internship/allinternship";
    private final static String GET_URL_BY_ID = "/internship/{id}";

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    @MockBean
    private InternshipService service;

    @Autowired
    private MockMvc mockMvc;

//    @Autowired
//    private ObjectMapper objectMapper;

    @Test
    void positiveTestForCreateDtoMethod() throws Exception {
        InternshipDto internshipDto = InternshipDto.builder()
                                                    .id(1L)
                                                    .projectId(1L)
                                                    .mentorId(1L)
                                                    .internsIds(List.of(1L, 2L))
                                                    .startDate(LocalDateTime.now())
                                                    .endDate(LocalDateTime.now().plusMonths(1))
                                                    .status(InternshipStatus.COMPLETED)
                                                    .build();

        when(service.create(any())).thenReturn(internshipDto);

        mockMvc.perform(post(POST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(internshipDto)))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(internshipDto)))
                .andExpect(status().isOk());
    }

    static Stream<Object[]> invalidInternshipDto() {
        return Stream.of(
                new Object[]{InternshipDto.builder()
                        .projectId(1L)
                        .mentorId(1L)
                        .internsIds(List.of(1L, 2L))
                        .startDate(LocalDateTime.now())
                        .endDate(LocalDateTime.now().plusMonths(1))
                        .status(InternshipStatus.COMPLETED)
                        .build()},
                new Object[]{InternshipDto.builder()
                        .id(1L)
                        .mentorId(1L)
                        .internsIds(List.of(1L, 2L))
                        .startDate(LocalDateTime.now())
                        .endDate(LocalDateTime.now().plusMonths(1))
                        .status(InternshipStatus.COMPLETED)
                        .build()},
                new Object[]{InternshipDto.builder()
                        .id(1L)
                        .projectId(1L)
                        .internsIds(List.of(1L, 2L))
                        .startDate(LocalDateTime.now())
                        .endDate(LocalDateTime.now().plusMonths(1))
                        .status(InternshipStatus.COMPLETED)
                        .build()},
                new Object[]{InternshipDto.builder()
                        .id(1L)
                        .projectId(1L)
                        .mentorId(1L)
                        .internsIds(List.of())
                        .startDate(LocalDateTime.now())
                        .endDate(LocalDateTime.now().plusMonths(1))
                        .status(InternshipStatus.COMPLETED)
                        .build()},
                new Object[]{InternshipDto.builder()
                        .id(1L)
                        .projectId(1L)
                        .mentorId(1L)
                        .internsIds(List.of(1L, 2L))
                        .endDate(LocalDateTime.now().plusMonths(1))
                        .status(InternshipStatus.COMPLETED)
                        .build()},
                new Object[]{InternshipDto.builder()
                        .id(1L)
                        .projectId(1L)
                        .mentorId(1L)
                        .internsIds(List.of(1L, 2L))
                        .startDate(LocalDateTime.now())
                        .status(InternshipStatus.COMPLETED)
                        .build()}
        );
    }

    @ParameterizedTest
    @MethodSource("invalidInternshipDto")
    void negativeTestForCreateDtoMethod(InternshipDto internshipDto) throws Exception {
        mockMvc.perform(post(POST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(internshipDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void positiveTestForUpdateInternship() throws Exception {
        InternshipDto internshipDto = InternshipDto.builder()
                .id(1L)
                .projectId(1L)
                .mentorId(1L)
                .internsIds(List.of(1L, 2L))
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .status(InternshipStatus.COMPLETED)
                .build();

        doNothing().when(service).updateInternship(any(InternshipDto.class));

        mockMvc.perform(put(PUT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(internshipDto)))
                .andExpect(status().isOk());

        verify(service).updateInternship(argThat(argument ->
                argument.getId().equals(internshipDto.getId()) &&
                        argument.getProjectId().equals(internshipDto.getProjectId()) &&
                        argument.getMentorId().equals(internshipDto.getMentorId()) &&
                        argument.getInternsIds().equals(internshipDto.getInternsIds()) &&
                        argument.getStatus().equals(internshipDto.getStatus())
        ));
    }

    @ParameterizedTest
    @MethodSource("invalidInternshipDto")
    void negativeTestForUpdateInternship(InternshipDto internshipDto) throws Exception {
        mockMvc.perform(put(PUT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(internshipDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void positiveTestForGetAllInternshipByStatus() throws Exception {
        InternshipFilterDto internshipFilterDto = InternshipFilterDto.builder()
                .intern(TeamRole.INTERN)
                .status(InternshipStatus.COMPLETED)
                .build();

        InternshipDto internshipDto = InternshipDto.builder()
                .id(1L)
                .projectId(1L)
                .mentorId(1L)
                .internsIds(List.of(1L, 2L))
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .status(InternshipStatus.COMPLETED)
                .build();

        InternshipDto projectId = InternshipDto.builder()
                .projectId(1L)
                .build();

        when(service.getAllInternshipByStatus(eq(1L), any(InternshipFilterDto.class)))
                .thenReturn(List.of(internshipDto));

        mockMvc.perform(get(GET_URL_BY_STATUS)
                        .param("projectId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(projectId))
                        .content(OBJECT_MAPPER.writeValueAsString(internshipFilterDto)))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(List.of(internshipDto))))
                .andExpect(status().isOk());
    }

    @Test
    void negativeTestForGetAllInternshipByStatus() throws Exception {
        InternshipFilterDto invalidFilters = InternshipFilterDto.builder()
                .intern(null)
                .status(null)
                .build();

        mockMvc.perform(get(GET_URL_BY_STATUS, 1L)
                .param("projectId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(invalidFilters)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void positiveTestForGetAllInternship() throws Exception {
        InternshipDto internshipDto = InternshipDto.builder()
                .id(1L)
                .projectId(1L)
                .mentorId(1L)
                .internsIds(List.of(1L, 2L))
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .status(InternshipStatus.COMPLETED)
                .build();

        when(service.getAllInternship()).thenReturn(List.of(internshipDto));

        mockMvc.perform(get(GET_URL_LIST_DTO))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(List.of(internshipDto))))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn500WForGetAllInternship() throws Exception {
        when(service.getAllInternship()).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get(GET_URL_LIST_DTO))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void positiveTestForGetInternshipById() throws Exception {
        InternshipDto internshipDto = InternshipDto.builder()
                                                        .id(1L)
                                                        .projectId(1L)
                                                        .mentorId(1L)
                                                        .internsIds(List.of(1L, 2L))
                                                        .startDate(LocalDateTime.now())
                                                        .endDate(LocalDateTime.now().plusMonths(1))
                                                        .status(InternshipStatus.COMPLETED)
                                                        .build();

        when(service.getInternshipById(any())).thenReturn(internshipDto);

        mockMvc.perform(get(GET_URL_BY_ID, 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(internshipDto)))
                .andExpect(status().isOk());
    }

    @Test
    void negativeTestForGetInternshipById() throws Exception {
        when(service.getInternshipById(999L))
                .thenThrow(new EntityNotFoundException("Стажировка с ID " + 999L + " не найдена"));

        mockMvc.perform(get(GET_URL_BY_ID, 999L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Стажировка с ID 999 не найдена"));
    }
}
