package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.service.s3.S3Service;
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
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {

    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private S3Service s3Service;

    private final String bucketName = "test-bucket";

    private MultipartFile file;
    private String folder;

    @BeforeEach
    void setUp() {
        folder = "test-folder";
        file = mock(MultipartFile.class);
        ReflectionTestUtils.setField(s3Service, "bucketName", "test-bucket");
    }

    @Test
    void testUploadFile_Success() throws IOException {
        String contentType = "image";
        long fileSize = 500L;
        String fileName = "test.png";
        when(file.getContentType()).thenReturn(contentType);
        when(file.getSize()).thenReturn(fileSize);
        when(file.getOriginalFilename()).thenReturn(fileName);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));

        Resource result = s3Service.uploadFile(file, folder);

        assertNotNull(result);
        assertEquals(BigInteger.valueOf(fileSize), result.getSize());
        assertEquals(ResourceType.IMAGE, result.getType());
        assertEquals(ResourceStatus.ACTIVE, result.getStatus());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        verify(s3Client).putObject(any(PutObjectRequest.class));
    }

    @Test
    void testUploadFile_Throws_WhenAmazonS3Exception() throws IOException {
        when(file.getInputStream()).thenThrow(new AmazonS3Exception("S3 error"));

        assertThrows(FileException.class, () -> s3Service.uploadFile(file, folder));

        verify(s3Client, never()).putObject(any(PutObjectRequest.class));
    }

    @Test
    void testUploadFile_Throws_WhenIOException() throws IOException {
        when(file.getInputStream()).thenThrow(new IOException("IO error"));

        assertThrows(FileException.class, () -> s3Service.uploadFile(file, folder));

        verify(s3Client, never()).putObject(any(PutObjectRequest.class));
    }

    @Test
    void testDeleteFile_Success() {
        String key = "test-key";

        s3Service.deleteFile(key);

        verify(s3Client).deleteObject(bucketName, key);
    }

    @Test
    void testDeleteFile_Throws_WhenKeyIsNull() {
        assertThrows(FileException.class, () -> s3Service.deleteFile(null));

        verify(s3Client, never()).deleteObject(anyString(), anyString());
    }

    @Test
    void testDeleteFile_Throws_WhenKeyIsBlank() {
        assertThrows(FileException.class, () -> s3Service.deleteFile(" "));

        verify(s3Client, never()).deleteObject(anyString(), anyString());
    }

    @Test
    void testDeleteFile_Throws_WhenAmazonS3Exception() {
        String key = "test-key";
        doThrow(new AmazonS3Exception("S3 error")).when(s3Client).deleteObject(bucketName, key);

        assertThrows(FileException.class, () -> s3Service.deleteFile(key));

        verify(s3Client).deleteObject(bucketName, key);
    }
}