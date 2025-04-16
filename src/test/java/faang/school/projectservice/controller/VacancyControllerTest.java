package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.service.VacancyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VacancyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VacancyService vacancyService;

    @MockBean
    private UserContext userContext;

    private final long vacancyId = 1;
    private final String addCoverUrl = "/api/v1/vacancies/{vacancyId}/cover";
    private final VacancyDto expectedDto = VacancyDto.builder().vacancyId(vacancyId).build();
    MockMultipartFile file = new MockMultipartFile("cover", "cover.jpg", "image/jpeg", new byte[]{1, 2, 3});

    @Test
    void addCover_shouldAdd() throws Exception {
        when(vacancyService.addCover(anyLong(), any())).thenReturn(expectedDto);

        mockMvc.perform(multipart(addCoverUrl, vacancyId)
                        .file(file)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.vacancyId", is(1)));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void addCover_shouldReturnBadRequest_whenVacancyIdIsInvalid(long invalidVacancyId) throws Exception {
        mockMvc.perform(multipart(addCoverUrl, invalidVacancyId)
                        .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("addCover.vacancyId: can`t be less than 1"));
    }
}