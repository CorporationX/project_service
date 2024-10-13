package faang.school.projectservice.service.image;

import faang.school.projectservice.exception.ImageProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    private static final int MAX_WIDTH = 640;
    private static final int MAX_HEIGHT = 480;
    private static final int LARGE_WIDTH = 2000;
    private static final int LARGE_HEIGHT = 1500;
    private static final int SMALL_WIDTH = 500;
    private static final int SMALL_HEIGHT = 500;

    private ImageService imageService;

    @BeforeEach
    void setUp() {
        imageService = new ImageService();

        ReflectionTestUtils.setField(imageService, "maxHeight", 1024L);
        ReflectionTestUtils.setField(imageService, "maxWidth", 640L);
    }

    @Nested
    @DisplayName("When resizing an image larger than the max allowed dimensions")
    class ResizeLargeImage {

        @Test
        @DisplayName("then it should resize the image to the max allowed size")
        void whenResizeLargeImageThenResizeToMaxDimensions() throws IOException {
            MultipartFile imageFile = mock(MultipartFile.class);
            BufferedImage largeImage = new BufferedImage(LARGE_WIDTH, LARGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(largeImage, "png", baos);
            when(imageFile.getInputStream()).thenReturn(new ByteArrayInputStream(baos.toByteArray()));

            byte[] resizedImage = imageService.resizeImage(imageFile);

            BufferedImage resultImage = ImageIO.read(new ByteArrayInputStream(resizedImage));
            assertNotNull(resultImage);
            assertEquals(MAX_WIDTH, resultImage.getWidth());
            assertEquals(MAX_HEIGHT, resultImage.getHeight());
        }
    }

    @Nested
    @DisplayName("When resizing an image smaller than the max allowed dimensions")
    class ResizeSmallImage {

        @Test
        @DisplayName("then it should retain the original size")
        void whenResizeSmallImageThenRetainOriginalSize() throws IOException {
            MultipartFile imageFile = mock(MultipartFile.class);
            BufferedImage smallImage = new BufferedImage(SMALL_WIDTH, SMALL_HEIGHT, BufferedImage.TYPE_INT_RGB);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(smallImage, "png", baos);
            when(imageFile.getInputStream()).thenReturn(new ByteArrayInputStream(baos.toByteArray()));

            byte[] resizedImage = imageService.resizeImage(imageFile);

            BufferedImage resultImage = ImageIO.read(new ByteArrayInputStream(resizedImage));
            assertNotNull(resultImage);
            assertEquals(SMALL_WIDTH, resultImage.getWidth());
            assertEquals(SMALL_HEIGHT, resultImage.getHeight());
        }
    }

    @Nested
    @DisplayName("When image resizing fails")
    class ResizeImageFailure {

        @Test
        @DisplayName("then it should throw an IllegalArgumentException")
        void whenResizeFailsThenThrowIllegalArgumentException() throws IOException {
            MultipartFile imageFile = mock(MultipartFile.class);
            when(imageFile.getInputStream()).thenThrow(new IOException("Invalid image"));

            Exception exception = assertThrows(ImageProcessingException.class, () -> imageService.resizeImage(imageFile));

            assertTrue(exception.getMessage().contains("Failed to resize image"));
        }
    }
}
