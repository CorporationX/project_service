package faang.school.projectservice.exception;

import faang.school.projectservice.controller.ProjectController;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.util.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class ExceptionApiHandlerTest {
    private static final long PROJECT_ID = 100;

    private final Utils utils = new Utils();
    @Mock
    private ProjectService projectService;
    @InjectMocks
    private ProjectController projectController;
    @InjectMocks
    private ExceptionApiHandler exceptionApiHandler;

    @Test
    public void testProjectNotFound() {
        ProjectNotFound exception = new ProjectNotFound(
                utils.format(ProjectService.PROJECT_NOT_FOUND, PROJECT_ID));
        doThrow(exception).when(projectService).deleteCoverImage(PROJECT_ID);

        MockMvcWebTestClient.bindToController(projectController)
                .controllerAdvice(exceptionApiHandler)
                .build()
                .delete()
                .uri(utils.format("/projects/{}/cover-image", PROJECT_ID))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testProjectImageCoverException() throws IOException {
        // Создаем реальное изображение
        BufferedImage image = new BufferedImage(500, 300, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        byte[] imageBytes = baos.toByteArray();

        ProjectImageCoverException exception = new ProjectImageCoverException(
                ProjectService.ERROR_UPLOAD_PROJECT_IMAGE_COVER);
        doThrow(exception).when(projectService).addCoverImage(eq(PROJECT_ID), any(MultipartFile.class));

        MockMvcWebTestClient.bindToController(projectController)
                .controllerAdvice(exceptionApiHandler)
                .build()
                .post()
                .uri(utils.format("/projects/{}/cover-image", PROJECT_ID))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("image",
                        new ByteArrayResource(imageBytes) {
                            @Override
                            public String getFilename() {
                                return "test-image.jpg";
                            }
                        }))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testMethodArgumentTypeMismatchException() {
        MockMvcWebTestClient.bindToController(projectController)
                .controllerAdvice(exceptionApiHandler)
                .build()
                .delete()
                .uri("/projects/wrongValue/cover-image")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED)
                .expectBody()
                .jsonPath("$.errorDate").exists()
                .jsonPath("$.errorMessage").exists()
                .jsonPath("$.detail").doesNotExist();
    }

    @Test
    public void testRuntimeException() {
        MockMvcWebTestClient.bindToController(projectController)
                .controllerAdvice(exceptionApiHandler)
                .build()
                .post()
                .uri(utils.format("/projects/{}/cover-image", 0))
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED)
                .expectBody()
                .jsonPath("$.errorDate").exists()
                .jsonPath("$.errorMessage").exists()
                .jsonPath("$.errorMessage").isEqualTo(ExceptionApiHandler.RUNTIME_ERROR)
                .jsonPath("$.detail").doesNotExist();
    }
}