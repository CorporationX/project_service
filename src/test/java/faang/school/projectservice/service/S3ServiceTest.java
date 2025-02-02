package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.service.resource.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

    @BeforeEach
    void setUp() {
        file = mock(MultipartFile.class);
        when(file.getSize()).thenReturn(1000L);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getOriginalFilename()).thenReturn("test-image.png");
        try {
            when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[1000]));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ReflectionTestUtils.setField(s3Service, "bucketName", "test-bucket");
    }

    @Test
    void testUploadFile_Success() {
        String folder = "test-folder";

        Resource resource = s3Service.uploadFile(file, folder);

        assertNotNull(resource);
        assertEquals(BigInteger.valueOf(1000), resource.getSize());
        assertEquals(ResourceType.IMAGE, resource.getType());
        assertEquals(ResourceStatus.ACTIVE, resource.getStatus());
        assertNotNull(resource.getCreatedAt());
        assertNotNull(resource.getUpdatedAt());

        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(captor.capture());
        assertEquals(bucketName, captor.getValue().getBucketName());
        assertEquals(resource.getKey(), captor.getValue().getKey());
        try {
            assertEquals(file.getInputStream(), captor.getValue().getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testUploadFile_ThrowsFileException_WhenUploadFails() {
        String folder = "test-folder";
        doThrow(new RuntimeException("Upload failed")).when(s3Client)
                .putObject(any(PutObjectRequest.class));

        assertThrows(FileException.class, () -> s3Service.uploadFile(file, folder));
    }
}