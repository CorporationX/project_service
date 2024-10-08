package faang.school.projectservice.service.image;

import faang.school.projectservice.validator.util.image.MultipartImage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class ImageServiceTest {
    private static final String CONTENT_TYPE = "image/jpeg";
    private static final String FILE_NAME = "file";
    private static final int MAX_WIDTH = 1080;
    private static final int MAX_HEIGHT = 1080;

    private static final ClassPathResource SQUARE_IMAGE_RESOURCE =
            new ClassPathResource("test-project-cover-images/square-image-2000x2000.jpg");

    private final ImageService imageService = new ImageService();

    @Test
    @DisplayName("Get image from MultipartFile success")
    void testGetImageSuccess() throws IOException {
        byte[] imageBytes = Files.readAllBytes(SQUARE_IMAGE_RESOURCE.getFile().toPath());
        MultipartFile multipartFile =
                new MockMultipartFile(FILE_NAME, SQUARE_IMAGE_RESOURCE.getFilename(), CONTENT_TYPE, imageBytes);

        assertThat(imageService.getImage(multipartFile)).isInstanceOf(BufferedImage.class);
    }

    @Test
    @DisplayName("Resize image successful")
    void testResizeImageSuccessful() throws IOException {
        byte[] imageBytes = Files.readAllBytes(SQUARE_IMAGE_RESOURCE.getFile().toPath());
        MultipartFile multipartFile =
                new MockMultipartFile(FILE_NAME, SQUARE_IMAGE_RESOURCE.getFilename(), CONTENT_TYPE, imageBytes);
        BufferedImage image = imageService.getImage(multipartFile);
        BufferedImage result = imageService.resizeImage(image, MAX_WIDTH, MAX_HEIGHT);

        assertThat(result.getWidth()).isEqualTo(MAX_WIDTH);
        assertThat(result.getHeight()).isEqualTo(MAX_HEIGHT);
    }

    @Test
    @DisplayName("To MultipartImage successful")
    void testToMultipartImageSuccessful() throws IOException {
        byte[] imageBytes = Files.readAllBytes(SQUARE_IMAGE_RESOURCE.getFile().toPath());
        MultipartFile multipartFile =
                new MockMultipartFile(FILE_NAME, SQUARE_IMAGE_RESOURCE.getFilename(), CONTENT_TYPE, imageBytes);
        BufferedImage image = imageService.getImage(multipartFile);

        assertThat(imageService.toMultipartImage(image, multipartFile)).isInstanceOf(MultipartImage.class);
    }
}