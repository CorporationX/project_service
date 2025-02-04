package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {
    private static final String FOLDER_NAME = "test-folder";
    private static final String FILE_NAME = "test-file.txt";
    private static final String CONTENT_TYPE = "image/jpeg";
    private static final long FILE_SIZE = 1024L;

    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private S3Service s3Service;

    private MockMultipartFile mockFile;

    LocalDateTime fixedTime = LocalDateTime.of(2023, 1, 1, 12, 0);

    @BeforeEach
    void setUp() {
        mockFile = new MockMultipartFile("file", FILE_NAME, CONTENT_TYPE, new byte[(int) FILE_SIZE]);
    }

    @Test
    void uploadFile_ShouldReturnResourceWhenUploadSuccessful() {
        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(fixedTime);

            Resource expectedResource = new Resource();
            expectedResource.setKey(String.format("%s/%s/%s", FOLDER_NAME, fixedTime, FILE_NAME));
            expectedResource.setName(FILE_NAME);
            expectedResource.setSize(BigInteger.valueOf(FILE_SIZE));
            expectedResource.setType(ResourceType.getResourceType(CONTENT_TYPE));
            expectedResource.setStatus(ResourceStatus.ACTIVE);
            expectedResource.setCreatedAt(fixedTime);
            expectedResource.setUpdatedAt(fixedTime);

            when(amazonS3.putObject(any(PutObjectRequest.class))).thenReturn(null);

            Resource result = s3Service.uploadFile(mockFile, FOLDER_NAME);

            assertEquals(expectedResource, result);
        }
    }

    @Test
    void uploadFile_ShouldThrowFileExceptionWhenIoExceptionOccurs() {
        MockMultipartFile brokenFile = mock(MockMultipartFile.class);
        try {
            when(brokenFile.getInputStream()).thenThrow(new FileException("Failed to upload file", null));
        } catch (IOException e) {
            System.out.println(e);
        }


        FileException exception = assertThrows(FileException.class, () ->
                s3Service.uploadFile(brokenFile, FOLDER_NAME));

        assertEquals("Failed to upload file", exception.getMessage());
    }

    @Test
    void deleteFile_ShouldCallAmazonS3WhenKeyIsValid() {
        String key = "test-folder/test-file.txt";

        s3Service.deleteFile(key);

        verify(amazonS3, times(1)).deleteObject(null, key);
    }

    @Test
    void getFileUrl_ShouldReturnSignedUrlWhenKeyIsValid() {
        String key = "test-folder/test-file.txt";
        URL url = null;
        try {
            url = new URL("https://example.com/test-file");
        } catch (MalformedURLException e) {
            System.out.println(e);
        }
        when(amazonS3.generatePresignedUrl(any(GeneratePresignedUrlRequest.class))).thenReturn(url);

        String fileUrl = s3Service.getFileUrl(key);

        assertEquals(url.toString(), fileUrl);
    }

    @Test
    void getFileUrl_ShouldThrowFileExceptionWhenAmazonS3ThrowsException() {
        String key = "test-folder/test-file.txt";
        when(amazonS3.generatePresignedUrl(any(GeneratePresignedUrlRequest.class)))
                .thenThrow(new RuntimeException("AWS error"));

        FileException exception = assertThrows(FileException.class, () ->
                s3Service.getFileUrl(key));

        assertEquals("Failed to download file", exception.getMessage());
    }
}