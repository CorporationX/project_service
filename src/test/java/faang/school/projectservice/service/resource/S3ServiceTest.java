package faang.school.projectservice.service.resource;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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
        String content = "some content";
        MultipartFile file = new MockMultipartFile("fileName", "filename",
                "IMAGE", content.getBytes());
        Resource resource = new Resource();
        resource.setName(file.getOriginalFilename());
        resource.setSize(BigInteger.valueOf(content.length()));
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());

        Resource result = s3Service.uploadFile(file, "someFolder");

        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class));
        assertAll(
                () -> assertEquals(resource.getName(), result.getName()),
                () -> assertEquals(resource.getSize(), result.getSize()),
                () -> assertThat(result.getKey(), not(emptyOrNullString())),
                () -> assertEquals(resource.getStatus(), result.getStatus())
        );
    }

    @Test
    void testDeleteFileSuccess() {
        ReflectionTestUtils.setField(s3Service, "bucketName", bucket);
        doNothing().when(s3Client).deleteObject(bucket, key);

        s3Service.deleteFile(key);

        verify(s3Client, times(1)).deleteObject(bucket, key);
        assertDoesNotThrow(() -> s3Service.deleteFile(key));
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