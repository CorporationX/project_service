package faang.school.projectservice.service;

import faang.school.projectservice.util.ByteArrayMultipartFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ProjectImageServiceTest {

    @InjectMocks
    private ProjectImageService projectImageService;

    private MultipartFile mockImageFile;

    @BeforeEach
    void setUp() throws IOException {
        BufferedImage testImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream cover = new ByteArrayOutputStream();
        ImageIO.write(testImage, "jpg", cover);
        byte[] imageBytes = cover.toByteArray();

        mockImageFile = new ByteArrayMultipartFile(imageBytes, "test.jpg", "image/jpg");
    }

    @Test
    void testGetResizedCoverSuccess() {
        MultipartFile resizedCover = projectImageService.getResizedCover(mockImageFile);

        assertNotNull(resizedCover);
        assertEquals("test.jpg", resizedCover.getOriginalFilename());
        assertEquals("image/jpg", resizedCover.getContentType());
        assertTrue(resizedCover.getSize() > 0);
    }

    @Test
    void testGetResizedCoverInvalidFormat() {
        MultipartFile invalidFile = new ByteArrayMultipartFile("invalidData".getBytes(), "test", "contentType");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            projectImageService.getResizedCover(invalidFile);
        });
        assertFalse(exception.getMessage().contains("Ошибка форматирования изображения"));
    }

    @Test
    void testGetResizedCoverNullFile() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            projectImageService.getResizedCover(null);
        });
        assertNotNull(exception);
    }
}
