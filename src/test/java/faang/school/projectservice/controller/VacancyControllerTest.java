package faang.school.projectservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.vacancy.CreateVacancyRequest;
import faang.school.projectservice.dto.vacancy.CreateVacancyResponse;
import faang.school.projectservice.dto.vacancy.GetVacancyResponse;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
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

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        CreateVacancyRequest request = CreateVacancyRequest.builder()
                .name("vacancy")
                .description("description")
                .position(TeamRole.DEVELOPER)
                .projectId(1L)
                .createdBy(1L)
                .salary(90000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(1)
                .requiredSkillIds(List.of(1L, 2L))
                .build();

        CreateVacancyResponse response = CreateVacancyResponse.builder()
                .id(1L)
                .name("vacancy")
                .description("description")
                .position(TeamRole.DEVELOPER)
                .projectId(1L)
                .createdBy(1L)
                .status(VacancyStatus.OPEN)
                .salary(90000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(1)
                .requiredSkillIds(List.of(1L, 2L))
                .build();

        when(vacancyService.create(request)).thenReturn(response);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBodyJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/vacancies").contentType(MediaType.APPLICATION_JSON).content(requestBodyJson))
                .andExpect(status().isOk());
    }




}
