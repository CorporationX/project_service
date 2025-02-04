package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.properties.S3Properties;
import jakarta.persistence.EntityNotFoundException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ServiceImplTest {

    @Mock
    private AmazonS3 s3Client;

    @Mock
    private S3Properties s3Properties;

    @InjectMocks
    private S3ServiceImpl s3Service;

    @Test
    public void testUploadFile() {
        when(s3Properties.getBucketName()).thenReturn("bucket");
        PutObjectResult putObjectResult = new PutObjectResult();
        when(s3Client.putObject(any())).thenReturn(putObjectResult);
        byte[] bytes = new byte[1024 * 10];
        MockMultipartFile file = new MockMultipartFile("data", "file1.txt", "text/plain", bytes);

        Resource resource = s3Service.uploadFile(file, "folder");

        assertNotNull(resource);
        assertEquals(ResourceStatus.ACTIVE, resource.getStatus());
    }

    @SneakyThrows
    @Test
    public void testDownloadFile() {
        S3Object mockS3Object = mock(S3Object.class);
        S3ObjectInputStream mockS3ObjectInputStream = mock(S3ObjectInputStream.class);
        when(s3Properties.getBucketName()).thenReturn("bucket");
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(mockS3Object);
        when(mockS3Object.getObjectContent()).thenReturn(mockS3ObjectInputStream);

        InputStream inputStream = s3Service.downloadFile("key");

        assertNotNull(inputStream);
    }

    @SneakyThrows
    @Test
    public void testDownloadFileWhenResourceNotFound() {
        when(s3Properties.getBucketName()).thenReturn("bucket");
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> s3Service.downloadFile("key"));
    }

    @SneakyThrows
    @Test
    public void testDeleteFile() {
        doNothing().when(s3Client).deleteObject(any(DeleteObjectRequest.class));

       s3Service.deleteFile("key");

       verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
    }
}