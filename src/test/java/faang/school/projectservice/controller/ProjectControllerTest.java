package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = ProjectController.class)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProjectService projectService;
    @MockBean
    private UserContext userContext;

    private static final Long PROJECT_ID = 1L;
    private static final String COVER_URL = "/projects/" + PROJECT_ID + "/cover";
    private static final String TEST_IMAGE_NAME = "test.jpg";
    private static final byte[] IMAGE_BYTES = "test image content".getBytes();

    @Test
    @DisplayName("Загрузка обложки - успешный запрос")
    void uploadCover_WhenValidRequest_ReturnsOk() throws Exception {
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

        verify(projectService).uploadCover(eq(PROJECT_ID), any(MultipartFile.class));
    }

    @Test
    @DisplayName("Загрузка обложки - отсутствует файл")
    void uploadCover_WhenMissingFile_ReturnsBadRequest() throws Exception {
        mockMvc.perform(multipart(COVER_URL)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Удаление обложки - успешный запрос")
    void deleteCover_WhenValidRequest_ReturnsOk() throws Exception {
        mockMvc.perform(delete(COVER_URL))
                .andExpect(status().isNoContent());

        verify(projectService).deleteCover(PROJECT_ID);
    }
}