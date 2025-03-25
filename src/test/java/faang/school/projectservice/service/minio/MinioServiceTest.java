package faang.school.projectservice.service.minio;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
public class MinioServiceTest {

    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private MinioService minioService;

    private String bucketName = "test-bucket";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        ReflectionTestUtils.setField(minioService, "bucketName", bucketName);
    }

    @Test
    public void testCompressImageIfNeededSmallImageNoCompression() throws IOException {
        BufferedImage smallImage = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
        File tempFile = File.createTempFile("small-", ".png");
        ImageIO.write(smallImage, "png", tempFile);
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "small.png", "image/png", Files.readAllBytes(tempFile.toPath()));

        MinioService.CompressResult result = minioService.compressImageIfNeeded(mockFile);

        assertNotNull(result);
        assertEquals("image/png", result.getContentType());
        assertEquals(tempFile.length(), result.getSize());
        assertTrue(result.getFile().getName().startsWith("original-"));
    }

    @Test
    public void testCompressImageIfNeededLargeImageCompression() throws IOException {
        BufferedImage largeImage = new BufferedImage(2000, 1000, BufferedImage.TYPE_INT_RGB);
        File tempFile = File.createTempFile("large-", ".jpg");
        ImageIO.write(largeImage, "jpg", tempFile);
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "large.jpg", "image/jpeg", Files.readAllBytes(tempFile.toPath()));

        MinioService.CompressResult result = minioService.compressImageIfNeeded(mockFile);

        assertNotNull(result);
        assertEquals("image/jpeg", result.getContentType());
        assertTrue(result.getFile().getName().startsWith("compressed-"));
        assertTrue(result.getSize() < tempFile.length());
        BufferedImage compressedImage = ImageIO.read(result.getFile());
        assertEquals(1080, compressedImage.getWidth());
    }

    @Test
    public void testCompressImageIfNeededInvalidFormatThrowsException() {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", "test content".getBytes());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            minioService.compressImageIfNeeded(mockFile);
        });

        assertEquals("File must be an image", exception.getMessage());
    }

    @Test
    public void testCompressImageIfNeededUnsupportedFormatThrowsException() {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test.tiff", "image/tiff", "test content".getBytes());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            minioService.compressImageIfNeeded(mockFile);
        });
        assertTrue(exception.getMessage().contains("Unsupported image format: tiff"));
    }

    @Test
    public void testCompressImageIfNeededTooLargeFileThrowsException() throws IOException {
        byte[] largeContent = new byte[(int) (5 * 1024 * 1024 + 1)];
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "large.png", "image/png", largeContent);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            minioService.compressImageIfNeeded(mockFile);
        });
        assertEquals("File size exceeds 5 MB limit", exception.getMessage());
    }

    @Test
    public void testUploadFile_Success() throws IOException {
        File tempFile = File.createTempFile("test-", ".jpg");
        Files.write(tempFile.toPath(), "test content".getBytes());
        when(s3Client.putObject(any(PutObjectRequest.class))).thenReturn(null);

        String key = minioService.uploadFile(tempFile, "image/jpeg");

        assertNotNull(key);
        assertTrue(key.startsWith(bucketName + "/"));
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class));
    }

    @Test
    public void testUploadFileFailureThrowsException() throws IOException {
        File tempFile = File.createTempFile("test-", ".jpg");
        Files.write(tempFile.toPath(), "test content".getBytes());
        doThrow(new RuntimeException("S3 error")).when(s3Client).putObject(any(PutObjectRequest.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            minioService.uploadFile(tempFile, "image/jpeg");
        });
        assertEquals("Upload failed", exception.getMessage());
    }

    @Test
    public void testDeleteFileSuccess() {
        String key = "test-bucket/123_test.jpg";
        doNothing().when(s3Client).deleteObject(bucketName, key);

        minioService.deleteFile(key);

        verify(s3Client, times(1)).deleteObject(bucketName, key);
    }

    @Test
    public void testDeleteFileFailureThrowsException() {
        String key = "test-bucket/123_test.jpg";
        doThrow(new RuntimeException("S3 error")).when(s3Client).deleteObject(bucketName, key);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            minioService.deleteFile(key);
        });
        assertEquals("Delete failed", exception.getMessage());
    }
}