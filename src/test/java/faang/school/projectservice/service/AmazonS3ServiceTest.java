package faang.school.projectservice.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.exception.MinioUploadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AmazonS3ServiceTest {

    @Mock
    private AmazonS3 amazonS3Client;
    @Mock
    S3ObjectInputStream s3ObjectInputStream;
    @Mock
    S3Object s3Object;

    @InjectMocks
    private AmazonS3Service amazonS3Service;

    private String bucketName;
    private String key;
    private String contentType;
    private long contentLength;
    private InputStream inputStream;

    @BeforeEach
    public void setUp() {
        bucketName = "test-bucket";
        key = "test-file.txt";
        contentType = "text/plain";
        contentLength = 10L;
        inputStream = new ByteArrayInputStream("test content".getBytes());
        amazonS3Service = new AmazonS3Service(amazonS3Client);
        amazonS3Service.setBucketName(bucketName);
    }

    @Test
    public void testUploadFile_Success() throws MinioUploadException {
        String result = amazonS3Service.uploadFile(key, inputStream, contentType, contentLength);
        assertEquals(key, result);
        verify(amazonS3Client, times(1)).putObject(any(PutObjectRequest.class));
    }

    @Test
    public void testUploadFile_AmazonServiceException() {
        doThrow(new AmazonServiceException("Error")).when(amazonS3Client).putObject(any(PutObjectRequest.class));
        MinioUploadException exception = assertThrows(MinioUploadException.class, () -> {
            amazonS3Service.uploadFile(key, inputStream, contentType, contentLength);
        });
        assertEquals("Ошибка при загрузке файла в Minio", exception.getMessage());
    }

    @Test
    public void testDownloadFile_Success() {
        when(s3Object.getObjectContent()).thenReturn(s3ObjectInputStream);
        when(amazonS3Client.getObject(bucketName, key)).thenReturn(s3Object);
        s3Object.setBucketName(bucketName);
        s3Object.setKey(key);
        InputStream result = amazonS3Service.downloadFile(key);
        assertNotNull(result);
        assertSame(s3ObjectInputStream, result);
        verify(amazonS3Client, times(1)).getObject(bucketName, key);
    }

    @Test
    public void testDownloadFile_Exception() {
        when(amazonS3Client.getObject(bucketName, key)).thenThrow(new RuntimeException("Error"));
        Exception exception = assertThrows(RuntimeException.class, () -> {
            amazonS3Service.downloadFile(key);
        });
        assertEquals("Ошибка при загрузке файла из Minio", exception.getMessage());
    }

    @Test
    public void testDeleteFile_Success() {
        s3Object.setBucketName(bucketName);
        amazonS3Service.deleteFile(key);
        verify(amazonS3Client, times(1)).deleteObject(bucketName, key);
    }

    @Test
    public void testDeleteFile_Exception() {
        doThrow(new RuntimeException("Error")).when(amazonS3Client).deleteObject(bucketName, key);
        Exception exception = assertThrows(RuntimeException.class, () -> {
            amazonS3Service.deleteFile(key);
        });
        assertEquals("Ошибка при удалении файла из Minio", exception.getMessage());
    }
}