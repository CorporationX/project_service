package faang.school.projectservice.service.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import faang.school.projectservice.dto.image.FileData;
import faang.school.projectservice.exception.S3Exception;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3Service2Test {

    private static final String TEST_FILE_KEY = "test-file";
    private static final String TEST_FILE_NAME = "test.png";
    private static final String CONTENT_TYPE = "image/png";
    private static final String FILE_CONTENT = "file content";
    private static final int FILE_SIZE = 1024;
    private static final String RESOURCE_KEY = "123";

    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private S3Service2 s3Service2;

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

            FileData result = s3Service2.getFileById(TEST_FILE_KEY);

            assertNotNull(result);
            assertEquals(FILE_CONTENT, new String(result.getData()));
            assertEquals(CONTENT_TYPE, result.getContentType());
        }

        @Test
        @DisplayName("should throw S3Exception when an AmazonServiceException occurs")
        void whenAmazonServiceExceptionThenThrowS3Exception() {
            when(amazonS3.getObject(any(GetObjectRequest.class))).thenThrow(new AmazonServiceException("S3 error"));

            S3Exception exception = assertThrows(S3Exception.class, () -> {
                s3Service2.getFileById(TEST_FILE_KEY);
            });

            assertEquals("Amazon S3 service error while fetching file for key: test-file", exception.getMessage());
        }

        @Test
        @DisplayName("should throw S3Exception when an SdkClientException occurs")
        void whenSdkClientExceptionThenThrowS3Exception() {
            when(amazonS3.getObject(any(GetObjectRequest.class))).thenThrow(new SdkClientException("S3 error"));

            S3Exception exception = assertThrows(S3Exception.class, () -> {
                s3Service2.getFileById(TEST_FILE_KEY);
            });

            assertEquals("SDK client error while fetching file for key: test-file", exception.getMessage());
        }

        @Test
        @DisplayName("should throw S3Exception when IOException occurs during stream reading")
        void whenIOExceptionThenThrowS3Exception() throws Exception {
            S3Object s3Object = mock(S3Object.class);
            S3ObjectInputStream s3InputStream = mock(S3ObjectInputStream.class);

            when(amazonS3.getObject(any(GetObjectRequest.class))).thenReturn(s3Object);
            when(s3Object.getObjectContent()).thenReturn(s3InputStream);
            when(s3InputStream.readAllBytes()).thenThrow(new IOException("IO error"));

            S3Exception exception = assertThrows(S3Exception.class, () -> {
                s3Service2.getFileById(TEST_FILE_KEY);
            });

            assertEquals("Error reading file content from S3 for key: test-file", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Uploading an object to S3")
    class UploadObject {

        @Test
        @DisplayName("should upload successfully and return the file key")
        void whenObjectIsUploadedThenReturnFileKey() {
            s3Service2.uploadFile(RESOURCE_KEY, TEST_FILE_NAME, CONTENT_TYPE, FILE_SIZE, fileContentStream);

            verify(amazonS3).putObject(any(), eq(RESOURCE_KEY), eq(fileContentStream), any());
        }

        @Test
        @DisplayName("should throw S3Exception when AmazonServiceException occurs during upload")
        void whenAmazonServiceExceptionDuringUploadThenThrowS3Exception() {
            doThrow(new AmazonServiceException("S3 error")).when(amazonS3).putObject(any(), any(), any(), any());

            S3Exception exception = assertThrows(S3Exception.class, () -> {
                s3Service2.uploadFile(RESOURCE_KEY, TEST_FILE_NAME, CONTENT_TYPE, FILE_SIZE, fileContentStream);
            });

            assertEquals("Amazon S3 service error while uploading file: " + TEST_FILE_NAME, exception.getMessage());
            verify(amazonS3).putObject(any(), any(), any(), any());
        }

        @Test
        @DisplayName("should throw S3Exception when SdkClientException occurs during upload")
        void whenSdkClientExceptionDuringUploadThenThrowS3Exception() {
            doThrow(new SdkClientException("S3 error")).when(amazonS3).putObject(any(), any(), any(), any());

            S3Exception exception = assertThrows(S3Exception.class, () -> {
                s3Service2.uploadFile(RESOURCE_KEY, TEST_FILE_NAME, CONTENT_TYPE, FILE_SIZE, fileContentStream);
            });

            assertEquals("SDK client error while uploading file: " + TEST_FILE_NAME, exception.getMessage());
            verify(amazonS3).putObject(any(), any(), any(), any());
        }
    }

    @Nested
    @DisplayName("Removing an object from S3")
    class RemoveObjectByKey {

        @Test
        @DisplayName("should remove object successfully")
        void whenObjectIsRemovedThenRemoveObjectSuccessfully() {
            s3Service2.removeFileById(RESOURCE_KEY);

            verify(amazonS3).deleteObject(any(), eq(RESOURCE_KEY));
        }

        @Test
        @DisplayName("should throw S3Exception when AmazonServiceException occurs during remove")
        void whenAmazonServiceExceptionDuringRemoveThenThrowS3Exception() {
            doThrow(new AmazonServiceException("S3 error")).when(amazonS3).deleteObject(any(), any());

            S3Exception exception = assertThrows(S3Exception.class, () -> {
                s3Service2.removeFileById(RESOURCE_KEY);
            });

            assertEquals("Amazon S3 service error while removing file for id: " + RESOURCE_KEY, exception.getMessage());
        }

        @Test
        @DisplayName("should throw S3Exception when SdkClientException occurs during remove")
        void whenSdkClientExceptionDuringRemoveThenThrowS3Exception() {
            doThrow(new SdkClientException("S3 error")).when(amazonS3).deleteObject(any(), any());

            S3Exception exception = assertThrows(S3Exception.class, () -> {
                s3Service2.removeFileById(RESOURCE_KEY);
            });

            assertEquals("SDK client error while removing file for id: " + RESOURCE_KEY, exception.getMessage());
        }
    }
}
