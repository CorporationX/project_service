package faang.school.projectservice.service.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.exception.IntegrationException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AmazonS3ServiceTest {
    private static final String FOLDER_NAME = "folder";

    @Mock
    private AmazonS3Client s3Client;

    @InjectMocks
    private AmazonS3Service amazonS3Service;

    @Mock
    private MultipartFile multipartFile;

    private ByteArrayInputStream inputStream;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(amazonS3Service, "bucketName", "testbucket");
        inputStream = new ByteArrayInputStream(new byte[]{});
    }

    @Test
    void testSuccessUploadFile() throws IOException {
        when(multipartFile.getInputStream()).thenReturn(inputStream);

        String resultKey = amazonS3Service.uploadFile(multipartFile, FOLDER_NAME);

        assertNotNull(resultKey);

        verify(s3Client).putObject(any());
    }

    @Test
    void testUploadFileWhenS3ClientThrowException() {
        doThrow(SdkClientException.class).when(s3Client).putObject(any(PutObjectRequest.class));

        assertThrows(IntegrationException.class, () -> amazonS3Service.uploadFile(multipartFile, FOLDER_NAME));
    }

    @Test
    void testUploadFileWhenFileInputStreamThrowException() throws IOException {
        doThrow(IOException.class).when(multipartFile).getInputStream();

        assertThrows(IntegrationException.class, () -> amazonS3Service.uploadFile(multipartFile, FOLDER_NAME));
    }

    @Test
    void testSuccessDeleteFile() {
        String key = "key";
        when(s3Client.doesObjectExist(anyString(), anyString())).thenReturn(true);

        amazonS3Service.deleteFile(key);

        verify(s3Client).deleteObject(anyString(), anyString());
    }

    @Test
    void testDeleteFileWhenFileDoesNotExist() {
        String key = "non-existent-key";
        when(s3Client.doesObjectExist(anyString(), anyString())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> amazonS3Service.deleteFile(key));
        verify(s3Client, times(0)).deleteObject(anyString(), anyString());
    }

    @Test
    void testDeleteFileWhenS3ClientThrowException() {
        String key = "key";
        when(s3Client.doesObjectExist(anyString(), anyString())).thenReturn(true);
        doThrow(SdkClientException.class).when(s3Client).deleteObject(anyString(), anyString());

        assertThrows(IntegrationException.class, () -> amazonS3Service.deleteFile(key));
        verify(s3Client).doesObjectExist(anyString(), anyString());
    }
}