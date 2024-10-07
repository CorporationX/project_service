package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.dto.response.ResourceResponseObject;
import faang.school.projectservice.exception.ResourceHandlingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {

    @InjectMocks
    private S3ServiceImpl s3Service;

    @Mock
    private AmazonS3 s3Client;

    private MultipartFile multipartFile;
    private S3Object s3Object;
    private String key;
    private String bucketName;
    private S3ObjectInputStream inputStream;
    private ObjectMetadata objectMetadata;

    @BeforeEach
    public void setUp() {
        inputStream = new S3ObjectInputStream(InputStream.nullInputStream(), null);
        bucketName = "corpbucket";
        ReflectionTestUtils.setField(s3Service,"bucketName", bucketName);
        key = "key";
        s3Object = mock(S3Object.class);
        multipartFile = mock(MultipartFile.class);
        objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("application/octet-stream");
    }

    @Test
    @DisplayName("Загрузка файла")
    public void testUploadFile() {
        s3Service.uploadFile(multipartFile, key);
        verify(s3Client).putObject(any(PutObjectRequest.class));
    }

    @Test
    @DisplayName("Удаление файла")
    public void testDeleteFile() {
        s3Service.deleteFile(key);
        verify(s3Client).deleteObject(bucketName, key);
    }

    @Test
    @DisplayName("Скачивание файла")
    public void testDownloadFile() {
        when(s3Client.getObject(bucketName, key)).thenReturn(s3Object);
        when(s3Object.getObjectMetadata()).thenReturn(objectMetadata);
        when(s3Object.getObjectContent()).thenReturn(inputStream);

        ResourceResponseObject result = s3Service.downloadFile(key);

        assertEquals(result.inputStream(), inputStream);
        assertEquals(result.contentType(), "application/octet-stream");

        verify(s3Client).getObject(bucketName, key);
        verify(s3Object).getObjectMetadata();
        verify(s3Object).getObjectContent();
    }

    @Test
    @DisplayName("Скачивание файла по пустому ключу")
    public void testDownloadFileWithNullKey() {
        when(s3Client.getObject(bucketName, null)).thenReturn(null);
        assertThrows(ResourceHandlingException.class, () -> s3Service.downloadFile(null));
        verify(s3Client).getObject(bucketName, null);
    }

    @Test
    @DisplayName("Скачивание пустого файла по ключу")
    public void testDownloadNullFileWithKey() {
        when(s3Client.getObject(bucketName, key)).thenReturn(null);
        assertThrows(ResourceHandlingException.class, () -> s3Service.downloadFile(key));
        verify(s3Client).getObject(bucketName, key);
    }
}
