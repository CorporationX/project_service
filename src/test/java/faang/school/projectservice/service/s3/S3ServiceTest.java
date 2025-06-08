package faang.school.projectservice.service.s3;

import faang.school.projectservice.dto.resource.S3FileDto;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.exception.StorageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private S3Service s3Service;

    private static final String BUCKET_NAME = "corpbucket";
    private static final String FOLDER = "test-folder";
    private static final String FILE_NAME = "test.txt";
    private static final String CONTENT_TYPE = "text/plain";
    private static final byte[] FILE_CONTENT = "Hello, World!".getBytes();
    private static final String KEY = "test-key";
    private MultipartFile file;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3Service, "bucketName", BUCKET_NAME);
        file = new MockMultipartFile(
                FILE_NAME,
                FILE_NAME,
                CONTENT_TYPE,
                FILE_CONTENT
        );
    }

    @Test
    void testUploadFileSuccessful() {
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        String key = s3Service.uploadFile(FOLDER, file);

        assertNotNull(key);
        assertTrue(key.startsWith(FOLDER + "/"));
        assertTrue(key.endsWith(FILE_NAME));
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void testUploadFileThrowsFileException() {
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(new FileException("S3 error"));

        assertThrows(FileException.class, () -> s3Service.uploadFile(FOLDER, file));
    }

    @Test
    void testDownloadFileSuccessful() {
        GetObjectResponse mockResponse = GetObjectResponse.builder().build();
        ResponseInputStream<GetObjectResponse> stream = new ResponseInputStream<>(
                mockResponse,
                new ByteArrayInputStream(FILE_CONTENT)
        );

        HeadObjectResponse headResponse = HeadObjectResponse.builder()
                .contentType(CONTENT_TYPE)
                .contentLength((long) FILE_CONTENT.length)
                .metadata(Map.of("filename", FILE_NAME))
                .build();

        doReturn(stream).when(s3Client).getObject(GetObjectRequest.builder().bucket(BUCKET_NAME).key(KEY).build());
        doReturn(headResponse).when(s3Client).headObject(HeadObjectRequest.builder().bucket(BUCKET_NAME).key(KEY).build());

        S3FileDto result = s3Service.downloadFile(KEY);

        assertNotNull(result);
        assertEquals(FILE_NAME, result.getFileName());
        assertEquals(CONTENT_TYPE, result.getContentType());
        assertEquals(FILE_CONTENT.length, result.getContentLength());
        assertNotNull(result.getResource());
    }

    @Test
    void testDownloadFileThrowsStorageException() {
        when(s3Client.getObject(any(GetObjectRequest.class)))
                .thenThrow(new StorageException("S3 error"));

        assertThrows(StorageException.class, () -> s3Service.downloadFile(KEY));
        verify(s3Client).getObject(any(GetObjectRequest.class));
    }

    @Test
    void testDownloadFileThrowsStorageExceptionWithInvalidKey() {
        String invalidKey = null;

        assertThrows(StorageException.class, () -> s3Service.downloadFile(invalidKey));
    }

    @Test
    void testDeleteFileSuccessful() {
        doReturn(DeleteObjectResponse.builder().build())
                .when(s3Client).deleteObject(DeleteObjectRequest.builder().bucket(BUCKET_NAME).key(KEY).build());

        assertDoesNotThrow(() -> s3Service.deleteFile(KEY));
        verify(s3Client).deleteObject(DeleteObjectRequest.builder().bucket(BUCKET_NAME).key(KEY).build());
    }

    @Test
    void testDeleteFileThrowsStorageException() {
        doThrow(new StorageException("S3 error"))
                .when(s3Client).deleteObject(DeleteObjectRequest.builder().bucket(BUCKET_NAME).key(KEY).build());

        assertThrows(StorageException.class, () -> s3Service.deleteFile(KEY));
        verify(s3Client).deleteObject(DeleteObjectRequest.builder().bucket(BUCKET_NAME).key(KEY).build());
    }
}
