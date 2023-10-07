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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AmazonS3ServiceTest {

    @Mock
    private AmazonS3 amazonS3;
    @Mock
    private MultipartFile file;
    @Mock
    private S3Object s3Object;
    @Mock
    private S3ObjectInputStream inputStream;
    @InjectMocks
    private AmazonS3Service amazonS3Service;

    private byte[] bytes;
    private String folder;
    private String originalFileName;
    private String key;
    private long currentTime;
    private String bucketName;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(amazonS3Service, "bucketName", "corpbucket");
        bytes = new byte[]{0, 1, 0, 1};
        folder = "3" + "Faang School";
        originalFileName = "FaangSchoolFile";
        currentTime = System.currentTimeMillis();
        bucketName = "corpbucket";
        key = folder + currentTime + originalFileName;
    }

    @Test
    void uploadFileTest() {
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getOriginalFilename()).thenReturn(originalFileName);

        String result = amazonS3Service.uploadFile(bytes, file, folder);

        assertTrue(result.startsWith(folder));
        assertTrue(result.endsWith(originalFileName));

        verify(amazonS3).putObject(any(PutObjectRequest.class));
    }

    @Test
    void getFileTest() {
        when(amazonS3.getObject(bucketName, key)).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(inputStream);

        InputStream result = amazonS3Service.getFile(key);

        assertEquals(inputStream, result);

        verify(amazonS3).getObject(bucketName, key);
    }

    @Test
    void deleteFileTest() {
        amazonS3Service.deleteFile(key);

        verify(amazonS3).deleteObject(bucketName, key);
    }
}