package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.minio.MinioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private MinioService minioService;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectService projectService;

    private Project project;
    private ProjectDto projectDto;
    private MockMultipartFile mockFile;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        project = new Project();
        project.setId(1L);
        project.setStorageSize(BigInteger.valueOf(1000));
        project.setMaxStorageSize(BigInteger.valueOf(5000));

        projectDto = new ProjectDto();
        projectDto.setId(1L);

        mockFile = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "test content".getBytes());
    }

    @Test
    public void testAddCoverImageSuccess() throws IOException {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMapper.toDto(any(Project.class))).thenReturn(projectDto);

        MinioService.CompressResult compressResult = new MinioService.CompressResult(
                new File("test.jpg"), 500, "image/jpeg");
        when(minioService.compressImageIfNeeded(mockFile)).thenReturn(compressResult);

        when(minioService.uploadFile(compressResult.getFile(), compressResult.getContentType()))
                .thenReturn("project-covers/123_test.jpg");

        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(resourceRepository.save(any(Resource.class))).thenReturn(new Resource());

        ProjectDto result = projectService.addCoverImage(1L, mockFile);

        assertNotNull(result);
        assertEquals(projectDto, result);
        assertEquals("project-covers/123_test.jpg", project.getCoverImageId());
        assertEquals(BigInteger.valueOf(1500), project.getStorageSize());
        verify(projectRepository, times(1)).save(project);
        verify(resourceRepository, times(1)).save(any(Resource.class));
    }

    @Test
    public void testAddCoverImageProjectNotFoundThrowsException() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            projectService.addCoverImage(1L, mockFile);
        });
        assertEquals("Project not found: 1", exception.getMessage());
    }

    @Test
    public void testAddCoverImageAlreadyHasCoverThrowsException() {
        project.setCoverImageId("old-cover.jpg");
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            projectService.addCoverImage(1L, mockFile);
        });
        assertEquals("Project already has a cover image. Use update instead.", exception.getMessage());
    }

    @Test
    public void testUpdateCoverImageSuccess() throws IOException {
        project.setCoverImageId("old-cover.jpg");
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Resource oldResource = new Resource();
        oldResource.setKey("old-cover.jpg");
        when(resourceRepository.findByKey("old-cover.jpg")).thenReturn(Optional.of(oldResource));

        MinioService.CompressResult compressResult = new MinioService.CompressResult(
                new File("test.jpg"), 500, "image/jpeg");
        when(minioService.compressImageIfNeeded(mockFile)).thenReturn(compressResult);

        when(minioService.uploadFile(compressResult.getFile(), compressResult.getContentType()))
                .thenReturn("project-covers/123_test.jpg");

        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(resourceRepository.save(any(Resource.class))).thenReturn(new Resource());
        when(projectMapper.toDto(any(Project.class))).thenReturn(projectDto);

        ProjectDto result = projectService.updateCoverImage(1L, mockFile);

        assertNotNull(result);
        assertEquals(projectDto, result);
        assertEquals("project-covers/123_test.jpg", project.getCoverImageId());
        assertEquals(BigInteger.valueOf(1500), project.getStorageSize());
        assertEquals(ResourceStatus.INACTIVE, oldResource.getStatus());
        verify(resourceRepository, times(1)).save(oldResource);
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    public void testUpdateCoverImageNoCoverImageThrowsException() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            projectService.updateCoverImage(1L, mockFile);
        });
        assertEquals("Project has no cover image to update. Use add instead.", exception.getMessage());
    }

    @Test
    public void testSoftDeleteCoverImageSuccess() {
        project.setCoverImageId("cover.jpg");
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Resource resource = new Resource();
        resource.setKey("cover.jpg");
        when(resourceRepository.findByKey("cover.jpg")).thenReturn(Optional.of(resource));

        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toDto(any(Project.class))).thenReturn(projectDto);

        ProjectDto result = projectService.softDeleteCoverImage(1L);

        assertNotNull(result);
        assertNull(project.getCoverImageId());
        assertEquals(ResourceStatus.INACTIVE, resource.getStatus());
        verify(resourceRepository, times(1)).save(resource);
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    public void testSoftDeleteCoverImageNoCoverImageReturnsUnchanged() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMapper.toDto(any(Project.class))).thenReturn(projectDto);

        ProjectDto result = projectService.softDeleteCoverImage(1L);

        assertNotNull(result);
        assertEquals(projectDto, result);
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    public void testHardDeleteCoverImageSuccess() {
        project.setCoverImageId("cover.jpg");
        project.setStorageSize(BigInteger.valueOf(1000));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Resource resource = new Resource();
        resource.setKey("cover.jpg");
        resource.setSize(BigInteger.valueOf(500));
        when(resourceRepository.findByKey("cover.jpg")).thenReturn(Optional.of(resource));

        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toDto(any(Project.class))).thenReturn(projectDto);

        ProjectDto result = projectService.hardDeleteCoverImage(1L);

        assertNotNull(result);
        assertNull(project.getCoverImageId());
        assertEquals(BigInteger.valueOf(500), project.getStorageSize());
        verify(minioService, times(1)).deleteFile("cover.jpg");
        verify(resourceRepository, times(1)).delete(resource);
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    public void testHardDeleteCoverImageNoCoverImage_ReturnsUnchanged() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMapper.toDto(any(Project.class))).thenReturn(projectDto);

        ProjectDto result = projectService.hardDeleteCoverImage(1L);

        assertNotNull(result);
        assertEquals(projectDto, result);
        verify(minioService, never()).deleteFile(anyString());
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    public void testSetCoverImageStorageLimitExceededThrowsException() throws IOException {
        project.setMaxStorageSize(BigInteger.valueOf(1000));
        MinioService.CompressResult compressResult = new MinioService.CompressResult(
                new File("test.jpg"), 500, "image/jpeg");
        when(minioService.compressImageIfNeeded(mockFile)).thenReturn(compressResult);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            projectService.setCoverImage(project, mockFile);
        });
        assertTrue(exception.getMessage().contains("Compressed file size exceeds project storage limit"));
    }
}