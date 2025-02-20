package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.service.s3.S3ServiceCover;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class S3ServiceCoverTest {
    @Mock
    private AmazonS3 amazonS3;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private S3ServiceCover s3ServiceCover;

    private final String bucketName = "test-bucket";
    private final String folder = "test-folder";
    private final String fileName = "test-file.txt";
    private final String contentType = "text/plain";
    private final long fileSize = 1024L;

    @BeforeEach
    void setUp() {
        try {
            Field bucketNameField = S3ServiceCover.class.getDeclaredField("bucketName");
            bucketNameField.setAccessible(true);
            bucketNameField.set(s3ServiceCover, bucketName);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set bucketName field using reflection", e);
        }
    }

    @Test
    void testUploadFile() throws IOException {
        when(multipartFile.getSize()).thenReturn(fileSize);
        when(multipartFile.getContentType()).thenReturn(contentType);
        when(multipartFile.getOriginalFilename()).thenReturn(fileName);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));

        Resource resource = s3ServiceCover.uploadFile(multipartFile, folder);

        assertNotNull(resource);
        assertEquals(fileName, resource.getName());
        assertEquals(BigInteger.valueOf(fileSize), resource.getSize());
        assertEquals(ResourceStatus.ACTIVE, resource.getStatus());
        assertEquals(ResourceType.getResourceType(contentType), resource.getType());

        ArgumentCaptor<PutObjectRequest> putObjectRequestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(amazonS3).putObject(putObjectRequestCaptor.capture());

        PutObjectRequest putObjectRequest = putObjectRequestCaptor.getValue();
        assertEquals(bucketName, putObjectRequest.getBucketName());
        assertTrue(putObjectRequest.getKey().startsWith(folder));
        assertEquals(fileSize, putObjectRequest.getMetadata().getContentLength());
        assertEquals(contentType, putObjectRequest.getMetadata().getContentType());
    }

    @Test
    void testUploadFileThrowsException() throws IOException {
        when(multipartFile.getSize()).thenReturn(fileSize);
        when(multipartFile.getContentType()).thenReturn(contentType);
        when(multipartFile.getOriginalFilename()).thenReturn(fileName);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        doThrow(new RuntimeException("S3 error")).when(amazonS3).putObject(any(PutObjectRequest.class));

        assertThrows(BusinessException.class, () -> s3ServiceCover.uploadFile(multipartFile, folder));
    }

    @Test
    void testDeleteResource() {
        Resource resource = new Resource();
        resource.setKey("test-key");

        s3ServiceCover.deleteResource(resource);

        verify(amazonS3).deleteObject(bucketName, resource.getKey());
    }

    @Test
    void testDeleteResourceThrowsException() {
        Resource resource = new Resource();
        resource.setKey("test-key");
        doThrow(new RuntimeException("S3 error")).when(amazonS3).deleteObject(bucketName, resource.getKey());

        assertThrows(BusinessException.class, () -> s3ServiceCover.deleteResource(resource));
    }

    @Test
    void testGetCoverImage() {
        Resource resource = new Resource();
        resource.setKey("test-key");
        S3Object s3Object = mock(S3Object.class);
        S3ObjectInputStream inputStream = new S3ObjectInputStream(new ByteArrayInputStream(new byte[0]), null);
        when(s3Object.getObjectContent()).thenReturn(inputStream);
        when(amazonS3.getObject(bucketName, resource.getKey())).thenReturn(s3Object);

        InputStream result = s3ServiceCover.getCoverImage(resource);

        assertNotNull(result);
        verify(amazonS3).getObject(bucketName, resource.getKey());
    }

    @Test
    void testGetCoverImageThrowsException() {
        Resource resource = new Resource();
        resource.setKey("test-key");
        when(amazonS3.getObject(bucketName, resource.getKey())).thenThrow(new RuntimeException("S3 error"));

        assertThrows(BusinessException.class, () -> s3ServiceCover.getCoverImage(resource));
    }
}

