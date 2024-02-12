package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

class S3ServiceTest {
    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private S3Service s3Service;

    private final String bucketName = "bucketName";

    @Test
    void uploadFile() {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("C:\\Users\\Pictures\\1.jpg");
            MultipartFile file = new MockMultipartFile("file.txt", "file.txt", "text/plain", inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void downloadFile() {
    }
}