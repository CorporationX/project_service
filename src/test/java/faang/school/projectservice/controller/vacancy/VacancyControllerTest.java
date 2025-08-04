package faang.school.projectservice.controller.vacancy;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.mapper.CandidateMapper;
import faang.school.projectservice.mapper.CandidateMapperImpl;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.mapper.VacancyMapperImpl;
import faang.school.projectservice.service.vacancy.VacancyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(VacancyController.class)
@ContextConfiguration(classes = {VacancyController.class, VacancyMapperImpl.class,
        CandidateMapperImpl.class, VacancyControllerTestData.class})
@DisplayName("Тесты для VacancyController")
class VacancyControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private VacancyService vacancyService;
    @MockBean
    private UserContext userContext;
    @Autowired
    private VacancyMapper vacancyMapper;
    @Autowired
    private CandidateMapper candidateMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private VacancyControllerTestData testData;

    @Test
    @DisplayName("успешное создание вакансии")
    void create_success() throws Exception {
        var createDto = testData.getCreateDto();
        var viewDto = testData.getViewDto();

        when(vacancyService.create(eq(createDto)))
                .thenReturn(viewDto);

        mockMvc.perform(post("/vacancies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .header("x-user-id", 1L))
                .andExpect(content().json(objectMapper.writeValueAsString(viewDto)))
                .andExpect(status().isOk());

        verify(vacancyService).create(eq(createDto));
    }

    @Test
    @DisplayName("обновление вакансии успешный кейс")
    void update_success() throws Exception {
        var vacancyId = 1L;
        var updateDto = testData.getUpdateDto();
        var vacancy = testData.getEntity();
        vacancyMapper.update(updateDto, vacancy);
        var viewDto = vacancyMapper.toViewDto(vacancy);

        when(vacancyService.update(eq(vacancyId), eq(updateDto)))
                .thenReturn(viewDto);

        mockMvc.perform(put("/vacancies/" + vacancyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(content().json(objectMapper.writeValueAsString(viewDto)));

        verify(vacancyService).update(eq(vacancyId), eq(updateDto));
    }

    @Test
    @DisplayName("получение списка вакансии успешный кейс")
    void getList_success() throws Exception {
        var filter = testData.getFilter();
        var list = testData.getList();

        when(vacancyService.getList(eq(filter)))
                .thenReturn(list);

        mockMvc.perform(get("/vacancies"))
                .andExpect(content().json(objectMapper.writeValueAsString(list)))
                .andExpect(status().isOk());

        verify(vacancyService).getList(eq(filter));
    }

    @Test
    @DisplayName("получить вакансию по id успешный кейс")
    void getById_success() throws Exception {
        var viewDto = testData.getViewDto();
        var vacancyId = viewDto.id();
        when(vacancyService.getById(vacancyId)).thenReturn(viewDto);

        mockMvc.perform(get("/vacancies/" + vacancyId))
                .andExpect(content().json(objectMapper.writeValueAsString(viewDto)))
                .andExpect(status().isOk());

        verify(vacancyService).getById(vacancyId);
    }
}