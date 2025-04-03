package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import faang.school.projectservice.exception.ImageProcessingException;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.utils.ImageProcessor;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {
    @Mock
    private AmazonS3 s3Client;
    @Mock
    private ImageProcessor imageProcessor;
    @Mock
    private MultipartFile file;

    @InjectMocks
    private S3Service s3Service;

    private static final String FOLDER = "cover";
    private static final String KEY = "key";
    private static final String ORIGINAL_FILE_NAME = "original.jpg";
    private static final String CONTENT_TYPE = "image/jpeg";
    private static final String FILE_EXTENSION = "jpeg";
    private static final int WIDTH_IMAGE = 100;
    private static final int HEIGHT_IMAGE = 100;
    private final BufferedImage bufferedImage = new BufferedImage(WIDTH_IMAGE, HEIGHT_IMAGE, BufferedImage.TYPE_INT_RGB);

    @Test
    @DisplayName("Загрузка изображения - успешная загрузка валидного файла")
    public void uploadImage_WhenValidImage_ReturnsGeneratedKey() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, FILE_EXTENSION, outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        MultipartFile file = new MockMultipartFile(
                ORIGINAL_FILE_NAME,
                ORIGINAL_FILE_NAME,
                CONTENT_TYPE,
                imageBytes);

        assertNotNull(s3Service.uploadImage(FOLDER, file));
    }

    @Test
    @DisplayName("Загрузка изображения - ошибка при чтении файла")
    public void uploadImage_WhenInputStreamFails_ThrowsImageProcessingException() throws IOException {
        when(file.getContentType()).thenReturn(CONTENT_TYPE);
        when(imageProcessor.getFileExtension(CONTENT_TYPE)).thenReturn(FILE_EXTENSION);
        when(file.getInputStream()).thenThrow(new IOException(""));

        Exception exception = assertThrows(ImageProcessingException.class,
                () -> s3Service.uploadImage(FOLDER, file));
        assertEquals("S3 upload operation failed", exception.getMessage());
    }

    @Test
    @DisplayName("Удаление изображения - успешное удаление по ключу")
    public void deleteImage_WithValidKey_ExecutesWithoutException() {
        assertDoesNotThrow(() -> s3Service.deleteImage(KEY));
    }

    @Test
    @DisplayName("Удаление изображения - обработка ошибки S3 клиента")
    public void deleteImage_WhenS3ClientFails_ThrowsImageProcessingException() {
        doThrow(new RuntimeException("S3 error")).when(s3Client).deleteObject(anyString(), anyString());

        Exception exception = assertThrows(ImageProcessingException.class,
                () -> s3Service.deleteImage(KEY));
        assertEquals("Error deleting file from S3", exception.getMessage());
    }
}
