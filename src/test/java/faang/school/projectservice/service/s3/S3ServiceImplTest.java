package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class S3ServiceImplTest {

    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private S3ServiceImpl service;

    @Value("${services.s3.bucketName}")
    private String bucket;

    private final String key = "key";

    @Test
    void testUploadFile() {
        MultipartFile file = new MockMultipartFile("someFile", "original",
                "TEXT", new byte[10000]);

        when(s3Client.putObject(any(PutObjectRequest.class))).thenReturn(new PutObjectResult());

        service.uploadFile(file, "package");

        verify(s3Client).putObject(any(PutObjectRequest.class));
    }

    @Test
    void tesGetFile() {
        InputStream mockInputStream = new ByteArrayInputStream("Mock file content".getBytes());
        S3ObjectInputStream s3ObjectInputStream = new S3ObjectInputStream(mockInputStream, null);

        S3Object s3Object = new S3Object();
        s3Object.setObjectContent(s3ObjectInputStream);

        when(s3Client.getObject(bucket, key)).thenReturn(s3Object);

        service.getFile(key);

        verify(s3Client).getObject(bucket, key);
    }

    @Test
    void testDeleteFile() {
        service.deleteFile(key);

        verify(s3Client).deleteObject(bucket, key);
    }
}
