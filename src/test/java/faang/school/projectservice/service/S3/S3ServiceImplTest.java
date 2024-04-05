package faang.school.projectservice.service.S3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ServiceImplTest {
    @Mock
    AmazonS3 s3Client;
    @InjectMocks
    S3ServiceImpl s3Service;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    Project firstProject;
    byte[] content;
    MockMultipartFile file;
    String folder;
    Resource firstResource;
    S3Object s3Object;

    @BeforeEach
    void setUp() {
        firstProject = Project.builder()
                .id(1L)
                .name("firstProject")
                .description("string")
                .storageSize(BigInteger.valueOf(121212))
                .maxStorageSize(BigInteger.valueOf(2000000000))
                .ownerId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        content = "Mock file content".getBytes();
        file = new MockMultipartFile("file", "test.txt", "text/plain", content);

        folder = firstProject.getId() + firstProject.getName();
        firstResource = Resource.builder()
                .id(1L)
                .name(file.getOriginalFilename())
                .key(folder)
                .type(ResourceType.TEXT)
                .status(ResourceStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .project(firstProject)
                .size(BigInteger.valueOf(file.getSize()))
                .build();
        s3Object = new S3Object();
        try {
            s3Object.setObjectContent(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testUploadFile_IOE() {
        Resource resource = s3Service.uploadFile(file, folder);
        assertEquals(resource.getName(), firstResource.getName());
        assertEquals(resource.getStatus(), firstResource.getStatus());
        assertEquals(resource.getSize(), firstResource.getSize());

        verify(s3Client, times(1)).putObject(Mockito.any(PutObjectRequest.class));
    }

    @Test
    public void testDeleteFile() {
        s3Service.deleteFile(firstResource.getKey());
        verify(s3Client, times(1)).deleteObject(bucketName, firstResource.getKey());
    }

    @Test
    public void testDownloadFile_FileNotFound() {
        when(s3Client.getObject(bucketName, firstResource.getKey())).thenReturn(null);
        assertThrows(FileException.class, () -> s3Service.downloadFile(firstResource.getKey()));
    }


    @Test
    public void testDownloadFile() {
        when(s3Client.getObject(bucketName, firstResource.getKey())).thenReturn(s3Object);
        assertEquals(s3Object.getObjectContent(), s3Service.downloadFile(firstResource.getKey()));
        verify(s3Client, times(1)).getObject(bucketName, firstResource.getKey());
    }


}