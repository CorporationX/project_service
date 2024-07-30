package faang.school.projectservice.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.resource.ResourceMock;
import faang.school.projectservice.service.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class S3ServiceTest {

    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private S3Service s3Service;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Value("${services.s3.endpoint}")
    private String endpointUrl;

    private final String key = "test-key";

    @BeforeEach
    void setUp() {
        s3Service = new S3Service(s3Client);
        s3Service.setBucketName(bucketName);
        s3Service.setEndpointUrl(endpointUrl);
    }

    @Test
    void uploadFile() throws IOException {
        // Arrange
        MockMultipartFile file = ResourceMock.generateMultipartFile();

        // Act
        String key = s3Service.uploadFile(file);

        // Assert
        assertNotNull(key);
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class));
    }

    @Test
    void uploadFileError() {
        // Arrange
        MockMultipartFile file = ResourceMock.generateMultipartFile();
        doThrow(new RuntimeException("S3 upload failed")).when(s3Client).putObject(any(PutObjectRequest.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> s3Service.uploadFile(file));
    }

    @Test
    void deleteFile() {
        // Arrange & Act
        s3Service.deleteFile(key);

        // Assert
        verify(s3Client, times(1)).deleteObject(bucketName, key);
    }

    @Test
    void isObjectExist() {
        // Arrange
        when(s3Client.doesObjectExist(bucketName, key)).thenReturn(true);

        // Act
        boolean result = s3Service.isObjectExist(key);

        // Assert
        assertTrue(result);
        verify(s3Client, times(1)).doesObjectExist("test-bucket", key);
    }

    @Test
    void getFileUrl() {
        // Arrange
        String expectedUrl = String.format("%s/%s/%s", endpointUrl, bucketName, key);

        // Act
        String fileUrl = s3Service.getFileUrl(key);

        // Assert
        assertEquals(expectedUrl, fileUrl);
    }
}