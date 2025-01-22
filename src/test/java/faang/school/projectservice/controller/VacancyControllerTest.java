package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.VacancyDTO;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.VacancyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class VacancyControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private VacancyService vacancyService;

    @InjectMocks
    private VacancyController vacancyController;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(vacancyController).build();
    }

    @Test
    void testCreateVacancy() throws Exception {
        VacancyDTO vacancyDTO = new VacancyDTO();
        vacancyDTO.setName("Developer");
        vacancyDTO.setDescription("Desc");
        vacancyDTO.setPosition(TeamRole.TESTER);
        vacancyDTO.setProjectId(1L);
        vacancyDTO.setCount(3);

        Mockito.when(vacancyService.create(Mockito.any(VacancyDTO.class))).thenReturn(vacancyDTO);

        mockMvc.perform(post("/api/v1/vacancy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vacancyDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Developer"))
                .andExpect(jsonPath("$.description").value("Desc"))
                .andExpect(jsonPath("$.position").value("TESTER"))
                .andExpect(jsonPath("$.projectId").value(1))
                .andExpect(jsonPath("$.count").value(3));

        Mockito.verify(vacancyService).create(Mockito.any(VacancyDTO.class));
    }


    @Test
    void testUpdateVacancy() throws Exception {
        VacancyDTO vacancyDTO = new VacancyDTO();
        vacancyDTO.setName("Developer");
        vacancyDTO.setDescription("Updated Desc");
        vacancyDTO.setPosition(TeamRole.TESTER);
        vacancyDTO.setProjectId(1L);
        vacancyDTO.setCount(3);

        Mockito.when(vacancyService.update(Mockito.eq(1L), Mockito.any(VacancyDTO.class))).thenReturn(vacancyDTO);

        mockMvc.perform(put("/api/v1/vacancy/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vacancyDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Developer"))
                .andExpect(jsonPath("$.description").value("Updated Desc"))
                .andExpect(jsonPath("$.position").value("TESTER"))
                .andExpect(jsonPath("$.projectId").value(1))
                .andExpect(jsonPath("$.count").value(3));

        Mockito.verify(vacancyService).update(Mockito.eq(1L), Mockito.any(VacancyDTO.class));
    }

    @Test
    void testDeleteVacancyById() throws Exception {
        mockMvc.perform(delete("/api/v1/vacancy/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(vacancyService).deleteById(1L);
    }

    @Test
    void testGetVacancyById() throws Exception {
        VacancyDTO vacancyDTO = new VacancyDTO();
        vacancyDTO.setName("Developer");
        vacancyDTO.setDescription("Desc");
        vacancyDTO.setPosition(TeamRole.TESTER);
        vacancyDTO.setProjectId(1L);
        vacancyDTO.setCount(3);

        Mockito.when(vacancyService.getById(1L)).thenReturn(vacancyDTO);

        mockMvc.perform(get("/api/v1/vacancy/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Developer"))
                .andExpect(jsonPath("$.description").value("Desc"))
                .andExpect(jsonPath("$.position").value("TESTER"))
                .andExpect(jsonPath("$.projectId").value(1))
                .andExpect(jsonPath("$.count").value(3));

        Mockito.verify(vacancyService).getById(1L);
    }

    @Test
    void testGetAllVacancies() throws Exception {
        VacancyDTO vacancy1 = new VacancyDTO();
        vacancy1.setName("Developer");
        vacancy1.setDescription("Desc");
        vacancy1.setPosition(TeamRole.TESTER);
        vacancy1.setProjectId(1L);
        vacancy1.setCount(3);

        VacancyDTO vacancy2 = new VacancyDTO();
        vacancy2.setName("Designer");
        vacancy2.setDescription("Another Desc");
        vacancy2.setPosition(TeamRole.DESIGNER);
        vacancy2.setProjectId(1L);
        vacancy2.setCount(2);

        Mockito.when(vacancyService.getAll(Mockito.eq(TeamRole.TESTER), Mockito.eq("Developer")))
                .thenReturn(List.of(vacancy1, vacancy2));

        mockMvc.perform(get("/api/v1/vacancy")
                        .param("position", "TESTER")
                        .param("name", "Developer")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Developer"))
                .andExpect(jsonPath("$[0].position").value("TESTER"))
                .andExpect(jsonPath("$[1].name").value("Designer"))
                .andExpect(jsonPath("$[1].position").value("DESIGNER"));

        Mockito.verify(vacancyService).getAll(Mockito.eq(TeamRole.TESTER), Mockito.eq("Developer"));
    }


}
