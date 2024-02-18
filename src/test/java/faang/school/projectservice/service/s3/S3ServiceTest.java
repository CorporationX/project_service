package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {
    @Mock
    private AmazonS3 s3Client;
    @Mock
    private CoverHandler coverHandler;

    @InjectMocks
    private S3Service s3Service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3Service, "bucketName", "your-bucket-name");
    }

    @Test
    void uploadFile() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());
        String folder = "testFolder";
        InputStream resizedFile = mock(InputStream.class);
        when(coverHandler.checkCoverAndResize(file)).thenReturn(resizedFile);

        s3Service.uploadFile(file, folder);

        verify(s3Client).putObject(any(PutObjectRequest.class));
    }

    @Test
    void downloadFile() {
        String key = "testFolder/test.txt";
        S3ObjectInputStream inputStream = mock(S3ObjectInputStream.class);
        S3Object s3Object = mock(S3Object.class);

        when(s3Client.getObject(Mockito.any(GetObjectRequest.class))).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(inputStream);

        s3Service.downloadFile("key");

        verify(s3Client, times(1)).getObject(any());
        verify(s3Object, times(1)).getObjectContent();
    }
}