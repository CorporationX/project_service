package faang.school.projectservice.service.projectresource;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.config.resource.AmazonS3Properties;
import faang.school.projectservice.service.tika.TikaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceImplTest {

    @Mock
    private AmazonS3 amazonS3;

    @Mock
    private TikaService tikaService;

    @Spy
    private AmazonS3Properties properties;

    @InjectMocks
    private S3ServiceImpl s3Service;

    private MultipartFile file;

    @BeforeEach
    void setUp() {
        file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Test content".getBytes()
        );
    }

    @Test
    void testUploadFileSuccess() throws IOException {
        when(tikaService.detectMimeType(any())).thenReturn("text/plain");

        s3Service.uploadFile(file, "test-key");

        verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
    }

    @Test
    void testDeleteFileSuccess() {
        String bucketName = "test-bucket";
        String fileKey = "test-key";

        AmazonS3Properties mockProperties = new AmazonS3Properties();
        mockProperties.setBucketName(bucketName);
        S3ServiceImpl s3ServiceWithProperties = new S3ServiceImpl(amazonS3, tikaService, mockProperties);
        s3ServiceWithProperties.deleteFile(fileKey);

        verify(amazonS3, times(1)).deleteObject(bucketName, fileKey);
    }

    @Test
    void testDownloadFileSuccess() {
        String bucketName = "test-bucket";
        String fileKey = "test-key";

        AmazonS3Properties mockProperties = mock(AmazonS3Properties.class);
        when(mockProperties.getBucketName()).thenReturn(bucketName);

        S3ServiceImpl s3ServiceWithMockedProperties = new S3ServiceImpl(amazonS3, tikaService, mockProperties);
        S3Object mockS3Object = new S3Object();
        mockS3Object.setObjectContent(new ByteArrayInputStream("Test".getBytes()));
        when(amazonS3.getObject(eq(bucketName), eq(fileKey))).thenReturn(mockS3Object);

        S3Object result = s3ServiceWithMockedProperties.downloadFile(fileKey);

        assertNotNull(result);
        verify(amazonS3, times(1)).getObject(bucketName, fileKey);
    }
}