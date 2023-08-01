package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class VacancyControllerTest {
    @Mock
    private VacancyValidator vacancyValidator;
    @InjectMocks
    private VacancyController controller;
    private MockMvc mockMvc;
    private final VacancyDto vacancyDto = VacancyDto.builder().name("Java").build();
    private final VacancyDto vacancyDto2 = VacancyDto.builder().id(1L).name("Java").build();
    @Mock
    private VacancyService vacancyService;
    @Spy
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void controllerTest() throws Exception {
        Mockito.when(vacancyService.createVacancy(vacancyDto)).thenReturn(vacancyDto2);

        mockMvc.perform(post("/api/v1/vacancy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vacancyDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(vacancyDto2)));
    }
}
