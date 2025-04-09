package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.service.VacancyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = VacancyController.class)
public class VacancyControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private VacancyService vacancyService;
    @MockBean
    private UserContext userContext;

    private static final Long VACANCY_ID = 1L;
    private static final String COVER_URL = "/vacancies/" + VACANCY_ID + "/cover";
    private static final String TEST_IMAGE_NAME = "test.jpg";
    private static final byte[] IMAGE_BYTES = "test image content".getBytes();

    @Test
    @DisplayName("Загрузка обложки - успешный запрос")
    public void uploadCover_WhenValidRequest_ReturnsOk() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "cover",
                TEST_IMAGE_NAME,
                "image/jpeg",
                IMAGE_BYTES
        );

        mockMvc.perform(multipart(COVER_URL)
                        .file(image)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNoContent());

        verify(vacancyService).uploadCover(eq(VACANCY_ID), any(MultipartFile.class));
    }

    @Test
    @DisplayName("Загрузка обложки - отсутствует файл")
    public void uploadCover_WhenMissingFile_ReturnsBadRequest() throws Exception {
        mockMvc.perform(multipart(COVER_URL)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Удаление обложки - успешный запрос")
    public void deleteCover_WhenValidRequest_ReturnsOk() throws Exception {
        mockMvc.perform(delete(COVER_URL))
                .andExpect(status().isNoContent());

        verify(vacancyService).deleteCover(VACANCY_ID);
    }

    @Test
    @DisplayName("Получение обложки - успешный запрос")
    public void givenValidVacancyId_WhenGetUserAvatar_ThenSuccessRequest() throws Exception {
        Resource resource = new ByteArrayResource(new byte[]{1, 2, 3});

        when(vacancyService.getCover(VACANCY_ID)).thenReturn(resource);

        mockMvc.perform(get("/vacancies/{vacancyId}/cover", VACANCY_ID))
                .andExpect(status().isOk());
    }
}
