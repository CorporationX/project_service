package faang.school.projectservice.utils;

import faang.school.projectservice.exception.ImageProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageProcessorTest {
    @InjectMocks
    private ImageProcessor imageProcessor;

    @Mock
    private MultipartFile file;

    private static final String ORIGINAL_FILE_NAME = "original.jpg";
    private static final String CONTENT_TYPE = "image/jpeg";
    private static final String FILE_EXTENSION = "jpeg";
    private static final int WIDTH_IMAGE = 100;
    private static final int HEIGHT_IMAGE = 100;
    private final BufferedImage bufferedImage = new BufferedImage(WIDTH_IMAGE, HEIGHT_IMAGE, BufferedImage.TYPE_INT_RGB);

    @DisplayName("Получение расширения для JPEG изображения")
    @Test
    public void getFileExtension_WhenJpegContentType_ThenReturnsJpg() {
        String extension = imageProcessor.getFileExtension(CONTENT_TYPE);
        assertEquals("jpeg", extension);
    }

    @DisplayName("Ошибка при получении расширения: передается значение null")
    @Test
    public void getFileExtension_WhenNullContentType_ThenThrowsNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () ->
                imageProcessor.getFileExtension(null));
    }

    @DisplayName("Передан валидный JPEG файл, возвращается BufferedImage")
    @Test
    public void readImage_WhenValidJpegFileProvided_ReturnsBufferedImage() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, FILE_EXTENSION, outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        MultipartFile file = new MockMultipartFile(
                ORIGINAL_FILE_NAME,
                ORIGINAL_FILE_NAME,
                CONTENT_TYPE,
                imageBytes);

        assertNotNull(imageProcessor.readImage(file));
    }

    @DisplayName("Передан поврежденный файл, выбрасывается исключение IllegalArgumentException")
    @Test
    public void readImage_WhenCorruptedFileProvided_ThrowsIllegalArgumentException() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, FILE_EXTENSION, outputStream);
        byte[] corruptedImageBytes = new byte[]{0x00, 0x01, 0x02};

        MultipartFile file = new MockMultipartFile(
                ORIGINAL_FILE_NAME,
                ORIGINAL_FILE_NAME,
                CONTENT_TYPE,
                corruptedImageBytes);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> imageProcessor.readImage(file));
        assertEquals(String.format(
                        "Corrupted image content. File: %s, declared type: %s",
                        ORIGINAL_FILE_NAME,
                        CONTENT_TYPE),
                exception.getMessage());
    }

    @DisplayName("Ошибка чтения файла,выбрасывается ImageProcessingException")
    @Test
    public void readImage_WhenIOExceptionOccurs_ThrowsImageProcessingException() throws IOException {
        when(file.getInputStream()).thenThrow(new IOException("Error reading file"));
        Exception exception = assertThrows(ImageProcessingException.class,
                () -> imageProcessor.readImage(file));
        assertEquals("Failed to read image content.", exception.getMessage());
    }
}
