package faang.school.projectservice.service.s3;

import faang.school.projectservice.config.s3.S3Properties;
import faang.school.projectservice.exeption.S3DownloadException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {

    private S3ServiceImpl s3Service;

    @Mock
    private S3Client s3Client;

    @Captor
    private ArgumentCaptor<PutObjectRequest> putRequestCaptor;

    @Captor
    private ArgumentCaptor<DeleteObjectRequest> deleteRequestCaptor;

    private static S3Properties s3Properties;

    private static final String BUCKET = "test-bucket";
    private static final String KEY = "test-key";
    private static final String CONTENT_TYPE = "application/pdf";

    @BeforeAll
    static void beforeAll() {
        s3Properties = new S3Properties(
                "http://localhost:9000",
                "user",
                "pass",
                BUCKET,
                false,
                "region",
                "presentation"
        );
    }

    @BeforeEach
    void setUp() {
        s3Service = new S3ServiceImpl(s3Client, s3Properties);
    }

    @Test
    @DisplayName("Should upload PDF to S3 successfully with content type")
    void shouldUploadPdfSuccessfully() {
        InputStream inputStream = new ByteArrayInputStream("pdf content".getBytes());
        long contentLength = 11;

        s3Service.upload(inputStream, KEY, contentLength, CONTENT_TYPE);

        verify(s3Client).putObject(putRequestCaptor.capture(), any(RequestBody.class));
        PutObjectRequest actual = putRequestCaptor.getValue();

        assertEquals(BUCKET, actual.bucket());
        assertEquals(KEY, actual.key());
        assertEquals(CONTENT_TYPE, actual.contentType());
        assertEquals(contentLength, actual.contentLength());
    }

    @Test
    @DisplayName("Should delete file from S3 by key successfully")
    void shouldDeletePdfByKey() {
        s3Service.delete(KEY);

        verify(s3Client).deleteObject(deleteRequestCaptor.capture());
        DeleteObjectRequest actual = deleteRequestCaptor.getValue();

        assertEquals(BUCKET, actual.bucket());
        assertEquals(KEY, actual.key());
    }

    @Test
    @DisplayName("Should download file from S3 successfully")
    void shouldDownloadFileSuccessfully() {
        GetObjectRequest expectedRequest = GetObjectRequest.builder()
                .bucket(BUCKET)
                .key(KEY)
                .build();

        GetObjectResponse getObjectResponse = GetObjectResponse.builder().build();
        InputStream inputStream = new ByteArrayInputStream("pdf content".getBytes());
        ResponseInputStream<GetObjectResponse> responseInputStream =
                new ResponseInputStream<>(getObjectResponse, inputStream);

        when(s3Client.getObject(expectedRequest)).thenReturn(responseInputStream);

        InputStream result = s3Service.download(KEY);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Should throw exception when S3 download fails")
    void shouldThrowWhenDownloadFails() {
        when(s3Client.getObject(any(GetObjectRequest.class)))
                .thenThrow(S3Exception.builder().message("error").build());

        S3DownloadException exception = assertThrows(S3DownloadException.class,
                () -> s3Service.download(KEY));

        assertTrue(exception.getMessage().contains("Failed to download"));
    }
}
