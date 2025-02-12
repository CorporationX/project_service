package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.dto.resource.S3FileDto;
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
    private String key;

    @BeforeEach
    void setUp() {
        folder = "test-folder";
        file = mock(MultipartFile.class);
        key = "test-key";
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
        s3Service.deleteFile(key);

        verify(s3Client).deleteObject(bucketName, key);
    }

    @Test
    void testDeleteFile_Throws_WhenAmazonS3Exception() {
        doThrow(new AmazonS3Exception("S3 error")).when(s3Client).deleteObject(bucketName, key);

        assertThrows(FileException.class, () -> s3Service.deleteFile(key));

        verify(s3Client).deleteObject(bucketName, key);
    }

    @Test
    void testDownloadFile_success() {
        S3Object s3Object = mock(S3Object.class);
        ObjectMetadata metadata = mock(ObjectMetadata.class);
        S3ObjectInputStream inputStream = mock(S3ObjectInputStream.class);

        when(s3Client.getObject(bucketName, key)).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(inputStream);
        when(s3Object.getObjectMetadata()).thenReturn(metadata);
        when(metadata.getUserMetaDataOf("fileName")).thenReturn("file.txt");
        when(metadata.getContentType()).thenReturn("text/plain");
        when(metadata.getContentLength()).thenReturn(10L);

        S3FileDto result = s3Service.downloadFile(key);

        assertNotNull(result);
        assertEquals("file.txt", result.getFileName());
        assertEquals("text/plain", result.getContentType());
        assertEquals(10L, result.getContentLength());
        assertNotNull(result.getInputStreamResource());
    }

    @Test
    void testDownloadFile_s3Exception() {
        when(s3Client.getObject(bucketName, key))
                .thenThrow(new AmazonS3Exception("S3 error"));

        FileException exception = assertThrows(FileException.class,
                () -> s3Service.downloadFile(key));
        assertEquals("Failed to download file", exception.getMessage());
    }

    @Test
    void testDownloadFile_unexpectedException() {
        when(s3Client.getObject(bucketName, key))
                .thenThrow(new RuntimeException("Unexpected error"));

        FileException exception = assertThrows(FileException.class,
                () -> s3Service.downloadFile(key));
        assertEquals("Failed to download file", exception.getMessage());
    }


}