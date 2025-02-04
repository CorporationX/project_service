package faang.school.projectservice.service.impl;

import faang.school.projectservice.mapper.ResourceMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ResourceServiceImplTest {
    @Mock
    private ProjectService projectServiceMock;
    @Mock
    private ResourceValidator resourceValidatorMock;
    @Spy
    private ResourceMapperImpl resourceMapper;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private S3ServiceImpl s3Service;
    @InjectMocks
    private ResourceServiceImpl resourceService;
    private MultipartFile file;
    private Resource resource;

    @BeforeEach
    void setUp() {

        Project project = Project.builder()
                .id(222L)
                .build();

        resource = Resource.builder()
                .key("some_key")
                .name("some_name")
                .id(10000L)
                .project(project)
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Test adding resource")
    void addResource() {
        Long userId = 1L;
        Long projectId = 222L;
        Mockito.when(s3Service.uploadFile(file, "project_" + projectId)).thenReturn(resource);
        resourceService.addResource(userId, projectId, file);
        Mockito.verify(s3Service, Mockito.times(1))
                .uploadFile(file, "project_" + projectId);
        Mockito.verify(resourceRepository, Mockito.times(1))
                .save(resource);
    }

    @Test
    @DisplayName("Test downloading resource")
    void downloadResource() {
        Long userId = 1L;
        Long resourceId = 10000L;

        Mockito.when(resourceRepository.findById(resourceId)).thenReturn(Optional.ofNullable(resource));
        resourceService.downloadResource(userId, resourceId);
        Mockito.verify(resourceRepository, Mockito.times(1))
                .findById(resourceId);
        Mockito.verify(s3Service, Mockito.times(1))
                .downloadFile(Mockito.anyString());
    }

    @Test
    @DisplayName("Test deleting resource")
    void deleteResource() {
        Long userId = 1L;
        Long resourceId = 10000L;

        Mockito.when(resourceRepository.findById(resourceId)).thenReturn(Optional.ofNullable(resource));
        resourceService.deleteResource(userId, resourceId);
        Mockito.verify(resourceRepository, Mockito.times(1))
                .deleteById(resourceId);
        Mockito.verify(s3Service, Mockito.times(1))
                .deleteFile(Mockito.anyString());
    }
}