package faang.school.projectservice.service.project.cover.impl;

import faang.school.projectservice.config.ResourceConfig;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.cover.ImageProcessor;
import faang.school.projectservice.service.s3.S3Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectCoverServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private ImageProcessor imageProcessor;

    @Mock
    private ResourceConfig resourceConfig;

    @InjectMocks
    private ProjectCoverServiceImpl service;

    @Test
    public void testUploadProjectCover_Success() {
        Long projectId = 1L;
        MultipartFile file = mock(MultipartFile.class);
        when(file.getSize()).thenReturn(1024L);
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getOriginalFilename()).thenReturn("test.jpg");

        when(resourceConfig.getMaxSize()).thenReturn(5242880L); // 5 MB

        Project project = new Project();
        project.setId(projectId);
        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        String coverImageId = service.uploadProjectCover(projectId, file);

        assertNotNull(coverImageId);
        verify(s3Service).uploadFile(eq(file), anyString());
        verify(projectRepository).save(project);
    }

    @Test
    public void testDeleteProjectCover_Success() {
        Long projectId = 1L;
        String coverImageId = "unique-file-id.jpg";

        Project project = new Project();
        project.setId(projectId);
        project.setCoverImageId(coverImageId);
        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        service.deleteProjectCover(projectId);

        verify(s3Service).deleteFile(coverImageId);
        verify(projectRepository).save(project);
        assertNull(project.getCoverImageId());
    }

    @Test
    public void testDeleteProjectCover_ProjectNotFound() {
        Long projectId = 1L;

        when(projectRepository.getProjectById(projectId))
                .thenThrow(new EntityNotFoundException("Project not found by id: " + projectId));

        faang.school.projectservice.exception.EntityNotFoundException exception = assertThrows(
                faang.school.projectservice.exception.EntityNotFoundException.class,
                () -> service.deleteProjectCover(projectId)
        );

        assertEquals("Project not found by id: " + projectId, exception.getMessage());
        verify(s3Service, never()).deleteFile(anyString());
        verify(projectRepository, never()).save(any());
    }
}