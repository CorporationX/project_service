package faang.school.projectservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.vacancy.CreateVacancyRequest;
import faang.school.projectservice.dto.vacancy.CreateVacancyResponse;
import faang.school.projectservice.dto.vacancy.GetVacancyResponse;
import faang.school.projectservice.dto.vacancy.UpdateVacancyRequest;
import faang.school.projectservice.dto.vacancy.UpdateVacancyResponse;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.service.VacancyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;


import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class VacancyControllerTest {
    private MockMvc mockMvc;

    @Mock
    private VacancyService vacancyService;

    @InjectMocks
    private VacancyController vacancyController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(vacancyController).build();
    }

    @Test
    public void testGetVacancy() throws Exception {
        GetVacancyResponse response = GetVacancyResponse.builder()
                .id(1L)
                .name("vacancy")
                .description("description")
                .position(TeamRole.DEVELOPER)
                .status(VacancyStatus.OPEN)
                .build();

        when(vacancyService.getById(1)).thenReturn(response);

        mockMvc.perform(get("/vacancies/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("vacancy"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.position").value("DEVELOPER"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    public void testCreateVacancy() throws Exception {
        CreateVacancyRequest request = new CreateVacancyRequest();
        request.setName("vacancy");

        CreateVacancyResponse response = CreateVacancyResponse.builder()
                .id(1L)
                .name("vacancy")
                .build();

        when(vacancyService.create(request)).thenReturn(response);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBodyJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/vacancies")
                        .header("x-user-id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("vacancy")));
    }

    @Test
    public void testUpdateVacancy() throws Exception {
        UpdateVacancyRequest request = new UpdateVacancyRequest();
        request.setId(1L);
        request.setName("name");

        UpdateVacancyResponse response = UpdateVacancyResponse.builder()
                .id(1L)
                .name("name")
                .build();

        when(vacancyService.update(request)).thenReturn(response);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBodyJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(put("/vacancies/{id}", 1)
                        .header("x-user-id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("name")));
    }

    @Test
    public void testGetAllVacancies() throws Exception {
        GetVacancyResponse response = GetVacancyResponse.builder()
                .id(1L)
                .name("vacancy")
                .position(TeamRole.DEVELOPER)
                .build();

        List<GetVacancyResponse> vacancies = new ArrayList<>();
        vacancies.add(response);

        VacancyFilterDto filters = new VacancyFilterDto();
        filters.setPositionPattern(TeamRole.DEVELOPER);
        filters.setNamePattern("vacancy");

        when(vacancyService.get(filters)).thenReturn(vacancies);

        mockMvc.perform(get("/vacancies")
                        .param("positionPattern", "DEVELOPER")
                        .param("namePattern", "vacancy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("vacancy")))
                .andExpect(jsonPath("$[0].position", is("DEVELOPER")));
    }
}
