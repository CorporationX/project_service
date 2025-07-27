package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.config.s3.S3Properties;
import faang.school.projectservice.exeption.S3DownloadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {

    @InjectMocks
    private S3ServiceImpl s3Service;

    @Mock
    private AmazonS3 amazonS3;

    @Mock
    private S3Object s3Object;

    @Mock
    private S3ObjectInputStream s3ObjectInputStream;

    private static final String BUCKET_NAME = "test-bucket";
    private static final String FILE_KEY = "test-key";
    private static final byte[] FILE_CONTENT = "test content".getBytes();

    @BeforeEach
    void setUp() {
        S3Properties props = new S3Properties(
                "http://localhost:9000",
                "accessKey",
                "secretKey",
                BUCKET_NAME,
                false,
                "presentations/"
        );
        s3Service = new S3ServiceImpl(amazonS3, props);
    }

    @Test
    @DisplayName("Should upload file to S3 successfully")
    void shouldUploadSuccessfully() {
        InputStream inputStream = new ByteArrayInputStream(FILE_CONTENT);

        s3Service.upload(inputStream, FILE_KEY, FILE_CONTENT.length);

        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(amazonS3).putObject(captor.capture());


        PutObjectRequest request = captor.getValue();
        assertEquals(BUCKET_NAME, request.getBucketName());
        assertEquals(FILE_KEY, request.getKey());
        assertEquals(FILE_CONTENT.length, request.getMetadata().getContentLength());
        assertEquals("application/pdf", request.getMetadata().getContentType());
    }

    @Test
    @DisplayName("Should return input stream when download succeeds")
    void shouldDownloadSuccessfully() {
        when(amazonS3.getObject(BUCKET_NAME, FILE_KEY)).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(s3ObjectInputStream);

        InputStream result = s3Service.download(FILE_KEY);

        assertNotNull(result);
        assertEquals(s3ObjectInputStream, result);
        verify(amazonS3).getObject(BUCKET_NAME, FILE_KEY);
    }

    @Test
    @DisplayName("Should throw exception when download fails")
    void shouldThrowWhenDownloadFails() {
        when(amazonS3.getObject(BUCKET_NAME, FILE_KEY)).thenThrow(new RuntimeException("S3 error"));

        S3DownloadException exception = assertThrows(S3DownloadException.class,
                () -> s3Service.download(FILE_KEY));

        assertTrue(exception.getMessage().contains("Failed to download file"));
        verify(amazonS3).getObject(BUCKET_NAME, FILE_KEY);
    }

    @Test
    @DisplayName("Should delete object from S3")
    void shouldDeleteFile() {
        s3Service.delete(FILE_KEY);
        verify(amazonS3).deleteObject(BUCKET_NAME, FILE_KEY);
    }
}
