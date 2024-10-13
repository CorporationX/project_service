package faang.school.projectservice.service.resource;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.exception.MinioUploadException;
import faang.school.projectservice.model.entity.Resource;
import faang.school.projectservice.model.enums.ResourceStatus;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@Slf4j
@ExtendWith(MockitoExtension.class)
class S3ServiceTest {
    @Mock
    private AmazonS3 s3Client;
    @Mock
    S3ObjectInputStream s3ObjectInputStream;
    @Mock
    S3Object s3Object;
    @InjectMocks
    private S3Service s3Service;
    private String key;
    private String bucket;
    private String contentType;
    private long contentLength;
    private InputStream inputStream;
    @Setter
    @Value("${services.s3.bucketName}")
    private String bucketName;

    @BeforeEach
    void setUp() {
        key = "someKey";
        bucket = "someBucket";
        bucketName = "test-bucket";
        key = "test-file.txt";
        contentType = "text/plain";
        contentLength = 10L;
        inputStream = new ByteArrayInputStream("test content".getBytes());
        s3Service = new S3Service(s3Client);
        s3Service.setBucketName(bucketName);
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

    @Test
    public void testUploadCoverImage_Success() throws MinioUploadException {
        String result = s3Service.uploadCoverImage(key, inputStream, contentType, contentLength);
        assertEquals(key, result);
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class));
    }

    @Test
    public void testUploadCoverImage_AmazonServiceException() {
        doThrow(new AmazonServiceException("Error")).when(s3Client).putObject(any(PutObjectRequest.class));
        MinioUploadException exception = assertThrows(MinioUploadException.class, () -> {
            s3Service.uploadCoverImage(key, inputStream, contentType, contentLength);
        });
        assertEquals("Ошибка при загрузке файла в Minio", exception.getMessage());
    }

    @Test
    public void testDownloadCoverImage_Success() {
        when(s3Object.getObjectContent()).thenReturn(s3ObjectInputStream);
        when(s3Client.getObject(bucketName, key)).thenReturn(s3Object);
        s3Object.setBucketName(bucketName);
        s3Object.setKey(key);
        InputStream result = s3Service.downloadCoverImage(key);
        assertNotNull(result);
        assertSame(s3ObjectInputStream, result);
        verify(s3Client, times(1)).getObject(bucketName, key);
    }

    @Test
    public void testDownloadCoverImage_Exception() {
        when(s3Client.getObject(bucketName, key)).thenThrow(new RuntimeException("Error"));
        Exception exception = assertThrows(RuntimeException.class, () -> {
            s3Service.downloadCoverImage(key);
        });
        assertEquals("Ошибка при загрузке файла из Minio", exception.getMessage());
    }

    @Test
    public void testDeleteCoverImage_Success() {
        s3Object.setBucketName(bucketName);
        s3Service.deleteFile(key);
        verify(s3Client, times(1)).deleteObject(bucketName, key);
    }

    @Test
    public void testDeleteCoverImage_Exception() {
        doThrow(new RuntimeException("Error")).when(s3Client).deleteObject(bucketName, key);
        Exception exception = assertThrows(RuntimeException.class, () -> {
            s3Service.deleteCoverImage(key);
        });
        assertEquals("Ошибка при удалении файла из Minio", exception.getMessage());
    }
}