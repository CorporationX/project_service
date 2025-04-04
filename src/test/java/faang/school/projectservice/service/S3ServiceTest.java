package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.config.S3Properties;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.service.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@EnableConfigurationProperties(S3Properties.class)
@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {
    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private S3Service s3Service;

    @MockBean
    private S3Properties properties;

    @Captor
    private ArgumentCaptor<PutObjectRequest> argumentCaptor;
    @Captor
    private ArgumentCaptor<DeleteObjectRequest> argumentCaptor1;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        properties = new S3Properties("test-access-key", "test-secret-key",
                "test-bucket", "https://s3.test.amazonaws.com");
        s3Service = new S3Service(s3Client, properties);
    }

    @Test
    public void testPositiveUpload() {
        MockMultipartFile file = new MockMultipartFile("file",
                "test.txt", "text/plain", "Hello World".getBytes());

        Resource resource = s3Service.uploadFile(file, "test30");
        verify(s3Client, times(1)).putObject(argumentCaptor.capture());
        PutObjectRequest putObjectRequest = argumentCaptor.getValue();
        assertEquals(putObjectRequest.getMetadata().getContentType(), file.getContentType());
        assertNotNull(resource);
    }

    @Test
    public void testPositiveDeleteFile() {
        String key = "test";
        s3Service.deleteFile("test");
        verify(s3Client, times(1)).deleteObject(argumentCaptor1.capture());
        assertEquals(argumentCaptor1.getValue().getKey(), key);
        assertEquals(argumentCaptor1.getValue().getBucketName(), "test-bucket");
    }
}
