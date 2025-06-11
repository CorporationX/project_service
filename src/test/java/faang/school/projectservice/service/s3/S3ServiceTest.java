package faang.school.projectservice.service.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.exception.FileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("S3Service Tests")
class S3ServiceTest {
    @Mock
    private AmazonS3 s3Client;
    @InjectMocks
    private S3Service s3Service;

    private String bucketName;
    private String folder;
    private MultipartFile mockFile;
    private String testKey;
    private InputStream mockContent;
    private byte[] fileContent;

    @BeforeEach
    void setUp() {
        bucketName = "test-bucket";
        folder = "test-folder";
        testKey = "some-folder/my-file.jpg";

        ReflectionTestUtils.setField(s3Service, "bucketName", bucketName);

        mockFile = new MockMultipartFile(
                "avatar",
                "test-avatar.png",
                "image/png",
                new byte[1000]
        );

        mockContent = new ByteArrayInputStream("downloaded content".getBytes());
        fileContent = "test file content".getBytes();
    }

    @Test
    void testUploadFileSuccessful() {
        when(s3Client.putObject(any())).thenReturn(mock(PutObjectResult.class));
        ArgumentCaptor<PutObjectRequest> putObjectRequestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);

        String returnedKey = s3Service.uploadFile(mockFile, folder);

        verify(s3Client).putObject(putObjectRequestCaptor.capture());
        assertNotNull(returnedKey);
        assertTrue(returnedKey.startsWith(folder + "/"));
        assertTrue(returnedKey.endsWith(mockFile.getOriginalFilename()));
        PutObjectRequest capturedRequest = putObjectRequestCaptor.getValue();
        assertEquals(bucketName, capturedRequest.getBucketName());
        assertEquals(returnedKey, capturedRequest.getKey());
        assertNotNull(capturedRequest.getInputStream());
        assertEquals(mockFile.getSize(), capturedRequest.getMetadata().getContentLength());
        assertEquals(mockFile.getContentType(), capturedRequest.getMetadata().getContentType());
    }

    @Test
    void testUploadFileS3ClientFailed() {
        when(s3Client.putObject(any())).thenThrow(new SdkClientException("S3 upload failed"));

        assertThrows(FileException.class, () -> s3Service.uploadFile(mockFile, folder));
    }

    @Test
    void testDownloadFileSuccessful() {
        S3Object mockS3Object = mock(S3Object.class);
        S3ObjectInputStream s3ObjectInputStream = new S3ObjectInputStream(mockContent, null);
        when(s3Client.getObject(bucketName, testKey)).thenReturn(mockS3Object);
        when(mockS3Object.getObjectContent()).thenReturn(s3ObjectInputStream);

        InputStream result = s3Service.downloadFile(testKey);

        assertNotNull(result);
        assertEquals(s3ObjectInputStream, result);
        verify(s3Client, times(1)).getObject(bucketName, testKey);
        verify(mockS3Object, times(1)).getObjectContent();
        verifyNoMoreInteractions(s3Client, mockS3Object);
    }

    @Test
    @DisplayName("Should throw FileException when s3Client.getObject fails")
    void testDownloadFileS3ClientFails() {
        when(s3Client.getObject(bucketName, testKey)).thenThrow(new RuntimeException("S3 download failed"));

        FileException exception = assertThrows(FileException.class, () -> s3Service.downloadFile(testKey));

        assertEquals("S3 download failed", exception.getMessage());
        verify(s3Client, times(1)).getObject(bucketName, testKey);
        verifyNoMoreInteractions(s3Client);
    }


    @Test
    void testDeleteFileSuccessful() {
        doNothing().when(s3Client).deleteObject(bucketName, testKey);

        boolean result = s3Service.deleteFile(testKey);

        assertTrue(result);
        verify(s3Client, times(1)).deleteObject(bucketName, testKey);
        verifyNoMoreInteractions(s3Client);
    }

    @Test
    void testDeleteFileS3ClientFails() {
        doThrow(new RuntimeException("S3 delete failed")).when(s3Client).deleteObject(bucketName, testKey);

        FileException exception = assertThrows(FileException.class, () -> s3Service.deleteFile(testKey));

        assertEquals("S3 delete failed", exception.getMessage());
        verify(s3Client, times(1)).deleteObject(bucketName, testKey);
        verifyNoMoreInteractions(s3Client);
    }

}