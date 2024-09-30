package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import faang.school.projectservice.dto.image.FileData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    private static final String TEST_FILE_KEY = "test-file";
    private static final String TEST_FILE_NAME = "test.png";
    private static final String CONTENT_TYPE = "image/png";
    private static final String FILE_CONTENT = "file content";
    private static final int FILE_SIZE = 1024;

    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private S3Service s3Service;

    private InputStream fileContentStream;

    @BeforeEach
    void setUp() {
        fileContentStream = new ByteArrayInputStream(FILE_CONTENT.getBytes());
    }

    @Nested
    @DisplayName("Fetching an object from S3")
    class GetObjectByKey {

        @Test
        @DisplayName("should return file data when object is found")
        void whenObjectIsFoundThenReturnFileData() {
            S3Object s3Object = mock(S3Object.class);
            S3ObjectInputStream s3InputStream = new S3ObjectInputStream(new ByteArrayInputStream(FILE_CONTENT.getBytes()), null);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(CONTENT_TYPE);

            when(amazonS3.getObject(any(GetObjectRequest.class))).thenReturn(s3Object);
            when(s3Object.getObjectContent()).thenReturn(s3InputStream);
            when(s3Object.getObjectMetadata()).thenReturn(metadata);

            FileData result = s3Service.getObjectByKey(TEST_FILE_KEY);

            assertNotNull(result);
            assertEquals(FILE_CONTENT, new String(result.getData()));
            assertEquals(CONTENT_TYPE, result.getContentType());
        }

        @Test
        @DisplayName("should return null when an exception occurs")
        void whenObjectFetchFailsThenReturnNull() {
            when(amazonS3.getObject(any(GetObjectRequest.class))).thenThrow(new RuntimeException("S3 error"));

            FileData result = s3Service.getObjectByKey(TEST_FILE_KEY);

            assertNull(result);
            verify(amazonS3).getObject(any(GetObjectRequest.class));
        }
    }

    @Nested
    @DisplayName("Uploading an object to S3")
    class UploadObject {

        @Test
        @DisplayName("should upload successfully and return the file key")
        void whenObjectIsUploadedThenReturnFileKey() {
            String uniqueFileKey = s3Service.uploadObject(TEST_FILE_NAME, CONTENT_TYPE, FILE_SIZE, fileContentStream);

            assertNotNull(uniqueFileKey);
            assertTrue(uniqueFileKey.startsWith(String.valueOf(System.currentTimeMillis()).substring(0, 5)));

            verify(amazonS3).putObject(any(), eq(uniqueFileKey), eq(fileContentStream), any());
        }

        @Test
        @DisplayName("should return null when upload fails")
        void whenUploadFailsThenReturnNull() {
            doThrow(new RuntimeException("S3 error")).when(amazonS3).putObject(anyString(), anyString(), any(InputStream.class), any(ObjectMetadata.class));

            String result = s3Service.uploadObject(TEST_FILE_NAME, CONTENT_TYPE, FILE_SIZE, fileContentStream);

            assertNull(result);
            verify(amazonS3).putObject(any(), anyString(), any(InputStream.class), any(ObjectMetadata.class));
        }
    }

    @Nested
    @DisplayName("Removing an object from S3")
    class RemoveObjectByKey {

        @Test
        @DisplayName("should remove object successfully")
        void whenObjectIsRemovedThenRemoveObjectSuccessfully() {
            s3Service.removeObjectByKey(TEST_FILE_KEY);

            verify(amazonS3).deleteObject(any(), eq(TEST_FILE_KEY));
        }

        @Test
        @DisplayName("should log error when removing fails")
        void whenRemoveFailsThenLogError() {
            doThrow(new RuntimeException("S3 error")).when(amazonS3).deleteObject(anyString(), eq(TEST_FILE_KEY));

            s3Service.removeObjectByKey(TEST_FILE_KEY);

            verify(amazonS3).deleteObject(any(), eq(TEST_FILE_KEY));
        }
    }
}
