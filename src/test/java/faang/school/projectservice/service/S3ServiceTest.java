package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.config.properties.S3Properties;
import faang.school.projectservice.exception.ExceptionMessage;
import faang.school.projectservice.exception.FileProcessingException;
import faang.school.projectservice.model.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
public class S3ServiceTest {

    @Mock
    AmazonS3 amazonS3;

    @Mock
    S3Properties s3Properties;

    @InjectMocks
    S3Service s3Service;

    ArgumentCaptor<PutObjectRequest> putObjectRequestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);

    private byte[] fileBytes;
    private String folder;
    private String fileName;
    private String contentType;
    private String bucketName;

    @BeforeEach
    void setUp() {
        fileBytes = "S3Service test".getBytes();
        folder = "cover";
        fileName = "test";
        contentType = "text/plain";
        bucketName = "test-bucket";
    }

    @Test
    void testUploadFromBytesNegative() {
        when(s3Properties.bucketName()).thenReturn(bucketName);
        Resource resource = s3Service.uploadFromBytes(fileBytes, folder, fileName, contentType);

        verify(amazonS3, times(1)).putObject(putObjectRequestCaptor.capture());
        PutObjectRequest putObjectRequest = putObjectRequestCaptor.getValue();

        assertEquals(putObjectRequest.getBucketName(), bucketName);
        assertEquals(resource.getName(), fileName);
    }

    @Test
    void testUploadFromBytesSuccess() {
        when(s3Properties.bucketName()).thenReturn(bucketName);
        when(amazonS3.putObject(any(PutObjectRequest.class))).thenThrow(RuntimeException.class);

        var exception = assertThrows(
                FileProcessingException.class,
                () -> s3Service.uploadFromBytes(fileBytes, folder, fileName, contentType)
        );

        assertEquals(exception.getMessage(), ExceptionMessage.S3_UPLOAD.getMessage());
    }

}
