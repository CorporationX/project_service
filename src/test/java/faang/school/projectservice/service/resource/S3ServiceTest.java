package faang.school.projectservice.service.resource;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.S3Object;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {
    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private S3Service s3Service;
    private String key;
    private String bucket;

    @BeforeEach
    void setUp() {
        key = "someKey";
        bucket = "someBucket";
    }


    @Test
    void testUploadFile() {
    }

    @Test
    void testDeleteFile() {
    }

    @Test
    void testGetFileSuccess() {
        S3Object s3Object = new S3Object();
        ReflectionTestUtils.setField(s3Service, "bucketName", bucket);
        when(s3Service.getFile(key)).thenReturn(s3Object);

        S3Object result = s3Service.getFile(key);

        verify(s3Client, times(1)).getObject(bucket, key);
        assertEquals(s3Object, result);
        assertDoesNotThrow(() -> s3Service.getFile(key));
    }

    @Test
    void testGetFileUnSuccess() {
        ReflectionTestUtils.setField(s3Service, "bucketName", bucket);
        doThrow(AmazonS3Exception.class).when(s3Client).getObject(bucket, key);

        assertThrows(AmazonS3Exception.class, () -> s3Service.getFile(key));
    }
}