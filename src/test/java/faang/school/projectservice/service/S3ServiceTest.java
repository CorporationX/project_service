package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {

    @Mock
    private AmazonS3 s3client;

    @InjectMocks
    private S3Service s3Service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3Service, "bucketName", "your-bucket-name");
    }

    @Test
    void testUploadFile() {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());
        String folder = "testFolder";

        s3Service.uploadFile(file, folder);

        verify(s3client).putObject(any(PutObjectRequest.class));
    }

    @Test
    void testDeleteFile() {
        String key = "testFolder/test.txt";

        s3Service.deleteFile(key);

        verify(s3client).deleteObject("your-bucket-name", key);
    }

    @Test
    void testDownloadFile() {
        String key = "testFolder/test.txt";
        S3Object s3Object = mock(S3Object.class);
        S3ObjectInputStream inputStream = mock(S3ObjectInputStream.class);
        when(s3Object.getObjectContent()).thenReturn(inputStream);
        when(s3client.getObject(eq("your-bucket-name"), eq(key))).thenReturn(s3Object);

        s3Service.downloadFile(key);

        verify(s3client).getObject("your-bucket-name", key);
    }
}
