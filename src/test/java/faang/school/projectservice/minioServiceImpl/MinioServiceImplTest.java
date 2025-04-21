package faang.school.projectservice.minioServiceImpl;

import faang.school.projectservice.exception.FileStorageException;
import faang.school.projectservice.service.MinioServiceImpl;
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
import static faang.school.projectservice.constants.Constants.UPLOAD_FAIL;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MinioServiceImplTest {
    @Mock
    private MinioClient minioClient;

    private final String bucketName = "test-bucket";

    @InjectMocks
    private MinioServiceImpl minioServiceImpl;

    private final String objectKey = "project-1/file.txt";
    private final String contentType = "text/plain";

    @BeforeEach
    void setUp() {
        minioServiceImpl = new MinioServiceImpl(minioClient, bucketName);
    }

    @Test
    void testUploadFile_success() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        InputStream stream = new ByteArrayInputStream("test data".getBytes());

        when(file.getInputStream()).thenReturn(stream);
        when(file.getSize()).thenReturn(9L);
        when(file.getContentType()).thenReturn(contentType);

        assertDoesNotThrow(() -> minioServiceImpl.uploadFile(objectKey, file));

        verify(minioClient).putObject(argThat(args -> args.bucket().equals(bucketName)
                && args.object().equals(objectKey)));
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

        assertTrue(ex.getMessage().contains(UPLOAD_FAIL));
    }

    @Test
    void testDeleteFile_success() throws Exception {
        doNothing().when(minioClient).removeObject(any(RemoveObjectArgs.class));

        minioServiceImpl.deleteFile(objectKey);

        verify(minioClient).removeObject(argThat(args -> args.bucket().equals(bucketName)
                && args.object().equals(objectKey)));
    }

    @Test
    void testDeleteFile_minioThrowsException_throwsRuntimeException() throws Exception {
        doThrow(new ErrorResponseException(null, null, null)).when(minioClient)
                .removeObject(any(RemoveObjectArgs.class));

        RuntimeException ex = assertThrows(FileStorageException.class, () ->
                minioServiceImpl.deleteFile(objectKey));

        assertTrue(ex.getMessage().contains(DELETE_FAIL));
    }
}

