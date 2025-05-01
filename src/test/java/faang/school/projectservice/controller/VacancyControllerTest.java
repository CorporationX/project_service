package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.service.VacancyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(VacancyController.class)
@ContextConfiguration(classes = {VacancyController.class})
class VacancyControllerTest {

    private final static Long USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VacancyService vacancyService;

    @MockBean
    private UserContext userContext;

    @Test
    void testGetVacancy() throws Exception {
        VacancyDto dto = new VacancyDto(
                1L,
                "Test Vacancy",
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L,
                1L,
                "test_cover.jpg"
        );

        when(vacancyService.getVacancy(1L)).thenReturn(dto);

        mockMvc.perform(get("/vacancy/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testUploadCover() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test".getBytes()
        );

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(vacancyService.saveCoverImage(1L, file)).thenReturn("key123");

        mockMvc.perform(multipart("/vacancy/1/cover")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("key123"));
    }

    @Test
    void testDeleteCoverImageFromVacancy() throws Exception {
        doNothing().when(vacancyService).deleteCoverImageFromVacancy(1L);

        mockMvc.perform(delete("/vacancy/1/deleteCover"))
                .andExpect(status().isOk());
    }
}