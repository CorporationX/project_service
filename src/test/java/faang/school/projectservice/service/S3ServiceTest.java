package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {
    @InjectMocks
    private S3Service s3Service;
    @Mock
    private AmazonS3 s3client;
    @Mock
    private S3Object s3Object;
    @Mock
    private S3ObjectInputStream inputStream;
    String key;

    @BeforeEach
    void setUp() {
        key = "Key";
        ReflectionTestUtils.setField(s3Service, "bucketName", "bucketNameValue");
    }

    @Test
    void testUploadFile() {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());

        s3Service.uploadFile(file, "folder");
        verify(s3client).putObject(any(PutObjectRequest.class));
    }

    @Test
    void testDeleteFile() {
        s3Service.deleteFile(key);
        verify(s3client).deleteObject("bucketNameValue", key);
    }

    @Test
    void testDownloadFile() {
        when(s3Object.getObjectContent()).thenReturn(inputStream);
        when(s3client.getObject(eq("bucketNameValue"), eq(key))).thenReturn(s3Object);

        s3Service.downloadFile(key);
        verify(s3client).getObject("bucketNameValue", key);
    }

}
