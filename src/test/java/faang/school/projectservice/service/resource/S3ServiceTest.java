package faang.school.projectservice.service.resource;

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

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.projectservice.exception.MinioUploadException;
import faang.school.projectservice.model.ResourceType;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@ExtendWith(MockitoExtension.class)
class S3ServiceTest {
    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private S3Service s3Service;
    private String key;
    private String bucket;

    @Setter
    @Value("${services.s3.bucketName}")
    private String bucketName;

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

    public Resource uploadFile(MultipartFile file, String folder) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        String uniqueID = UUID.randomUUID().toString();
        String key = String.format("%s/%s%s", folder, uniqueID, file.getOriginalFilename());
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (IOException e) {
            log.error("Something wrong: ", e);
            throw new RuntimeException(e);
        }
        Resource resource = new Resource();
        resource.setName(file.getOriginalFilename());
        resource.setSize(BigInteger.valueOf(file.getSize()));
        resource.setKey(key);
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());

        return resource;
    }

    public void deleteFile(String key) {
        s3Client.deleteObject(bucketName, key);
    }

    public S3Object getFile(String key) {
        return s3Client.getObject(bucketName, key);
    }

    public String uploadCoverImage(String key, InputStream inputStream, String contentType, long contentLength) throws MinioUploadException {
        log.info("Загрузка файла в Minio: key={}, contentType={}, contentLength={}", key, contentType, contentLength);
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(contentLength);
            metadata.setContentType(contentType);

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, metadata);
            putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
            s3Client.putObject(putObjectRequest);
            log.info("Файл успешно загружен в Minio: key={}", key);
            return key;
        } catch (AmazonServiceException e) {
            log.error("Ошибка при загрузке файла в Minio: {}, error={}", key, e.getMessage(), e);
            throw new MinioUploadException("Ошибка при загрузке файла в Minio", e);
        } catch (Exception e) {
            log.error("Неожиданная ошибка при загрузке файла в Minio: {}, error={}", key, e.getMessage(), e);
            throw new RuntimeException("Неожиданная ошибка при загрузке файла", e);
        }
    }

    public InputStream downloadCoverImage(String key) {
        log.info("Загрузка файла из Minio: key={}", key);
        try {
            S3Object s3Object = s3Client.getObject(bucketName, key);
            log.info("Файл успешно загружен из Minio: key={}", key);
            return s3Object.getObjectContent();
        } catch (Exception e) {
            log.error("Ошибка при загрузке файла из Minio: key={}, error={}", key, e.getMessage(), e);
            throw new RuntimeException("Ошибка при загрузке файла из Minio", e);
        }
    }

    public void deleteCoverImage(String key) {
        log.info("Удаление файла из Minio: key={}", key);
        try {
            s3Client.deleteObject(bucketName, key);
            log.info("Файл успешно удален из Minio: key={}", key);
        } catch (Exception e) {
            log.error("Ошибка при удалении файла из Minio: key={}, error={}", key, e.getMessage(), e);
            throw new RuntimeException("Ошибка при удалении файла из Minio", e);
        }
    }
}