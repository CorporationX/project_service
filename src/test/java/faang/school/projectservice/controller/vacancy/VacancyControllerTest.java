package faang.school.projectservice.controller.vacancy;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.vacancy.VacancyCreateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.mapper.CandidateMapper;
import faang.school.projectservice.mapper.CandidateMapperImpl;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.mapper.VacancyMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import faang.school.projectservice.service.vacancy.VacancyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(VacancyController.class)
@DisplayName("Тесты для VacancyController")
@Import({VacancyMapperImpl.class, CandidateMapperImpl.class})
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

    @Test
    @DisplayName("успешное создание вакансии")
    void create_success() throws Exception {
        var createDto = new VacancyCreateDto(
                "Java Dev",
                "we are looking for Java enjoyer",
                TeamRole.DEVELOPER,
                1L,
                1,
                300_000.0,
                WorkSchedule.REMOTE,
                List.of(1L, 2L, 3L, 4L, 5L),
                "DSKJFHKSDJH-FSDKJF1231"
        );
        var project = new Project();
        project.setId(1L);
        var vacancy = vacancyMapper.toEntity(createDto);
        vacancy.setProject(project);
        vacancy.setId(1L);
        vacancy.setCreatedBy(1L);
        vacancy.setCreatedAt(LocalDateTime.now());
        vacancy.setStatus(VacancyStatus.OPEN);
        var dto = vacancyMapper.toViewDto(vacancy);
        when(vacancyService.create(eq(createDto)))
                .thenReturn(dto);
        mockMvc.perform(post("/vacancies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(content().json(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
        verify(vacancyService).create(eq(createDto));
    }
}