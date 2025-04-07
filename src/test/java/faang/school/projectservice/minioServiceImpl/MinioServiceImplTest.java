package faang.school.projectservice.minioServiceImpl;

import faang.school.projectservice.config.MinioConfig;
import faang.school.projectservice.exceptions.FileStorageException;
import faang.school.projectservice.service.MinioServiceImpl;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static faang.school.projectservice.constants.Constants.DELETE_FAIL;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MinioServiceImplTest {
    @Mock
    private MinioClient minioClient;

    @Mock
    private MinioConfig minioConfig;

    @InjectMocks
    private MinioServiceImpl minioServiceImpl;

    private final String bucketName = "test-bucket";
    private final String objectKey = "project-1/file.txt";
    private final String contentType = "text/plain";

    @BeforeEach
    void setUp() {
        MinioConfig.Bucket bucket = new MinioConfig.Bucket();
        bucket.setName(bucketName);

        when(minioConfig.getBucket()).thenReturn(bucket);
    }

    @Test
    void testUploadFile_success() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        InputStream stream = new ByteArrayInputStream("test data".getBytes());

        when(file.getInputStream()).thenReturn(stream);
        when(file.getSize()).thenReturn(9L);
        when(file.getContentType()).thenReturn(contentType);

        assertDoesNotThrow(() -> minioServiceImpl.uploadFile(objectKey, file));

        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    void testUploadFile_minioThrowsException_throwsRuntimeException() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        InputStream stream = new ByteArrayInputStream("test data".getBytes());

        when(file.getInputStream()).thenReturn(stream);
        when(file.getSize()).thenReturn(9L);
        when(file.getContentType()).thenReturn(contentType);

        doThrow(new RuntimeException("Minio error")).when(minioClient).putObject(any(PutObjectArgs.class));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                minioServiceImpl.uploadFile(objectKey, file));

        assertTrue(ex.getMessage().contains("Failed to upload file to MinIO"));
    }

    @Test
    void testDeleteFile_success() throws Exception {
        doNothing().when(minioClient).removeObject(any(RemoveObjectArgs.class));

        minioServiceImpl.deleteFile(objectKey);

        verify(minioClient).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    void testDeleteFile_minioThrowsException_throwsRuntimeException() throws Exception {
        doThrow(new ErrorResponseException(null, null, null)).when(minioClient)
                .removeObject(any(RemoveObjectArgs.class));

        RuntimeException ex = assertThrows(FileStorageException.class, () ->
                minioServiceImpl.deleteFile(objectKey));

        assertTrue(ex.getMessage().contains(DELETE_FAIL));
    }

    @Test
    void testProvideBuckets_bucketDoesNotExist_shouldCreateBucket() throws Exception {
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(false);

        minioServiceImpl.provideBuckets();

        verify(minioClient).makeBucket(any(MakeBucketArgs.class));
    }

    @Test
    void testProvideBuckets_bucketExists_shouldNotCreateBucket() throws Exception {
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);

        minioServiceImpl.provideBuckets();

        verify(minioClient, never()).makeBucket(any(MakeBucketArgs.class));
    }

    @Test
    void testProvideBuckets_exceptionThrown_shouldThrowRuntimeException() throws Exception {
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenThrow(new RuntimeException("Minio error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                minioServiceImpl.provideBuckets());

        assertTrue(exception.getMessage().contains("Failed to created/check bucket"));
    }
}

