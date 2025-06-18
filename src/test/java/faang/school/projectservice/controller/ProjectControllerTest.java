package faang.school.projectservice.controller;

import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.util.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {
    private static final long PROJECT_ID = 1;

    private final Utils utils = new Utils();
    private MockMvc mockMvc;
    @Mock
    private ProjectService projectService;
    @InjectMocks
    private ProjectController projectController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();
    }

    @Test
    public void testAddCoverImageSuccess() throws Exception {
        String uri = utils.format("/projects/{}/cover-image", PROJECT_ID);
        // Создаем реальное изображение
        BufferedImage image = new BufferedImage(500, 300, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        byte[] imageBytes = baos.toByteArray();

        // Создаем MockMultipartFile с данными изображения
        MockMultipartFile file = new MockMultipartFile(
                "image", // имя параметра как в контроллере (@RequestParam("file"))
                "test-image.jpg",
                "image/jpeg",
                imageBytes
        );

        // Выполняем запрос
        mockMvc.perform(multipart(uri)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().is(HttpStatus.CREATED.value()));
    }

    @Test
    public void testDeleteCoverImage() throws Exception {
        String uri = utils.format("/projects/{}/cover-image", PROJECT_ID);

        doNothing().when(projectService).deleteCoverImage(PROJECT_ID);

        mockMvc.perform(delete(uri))
                .andExpect(status().is(HttpStatus.NO_CONTENT.value()))
                .andExpect(content().string(""));
    }
}