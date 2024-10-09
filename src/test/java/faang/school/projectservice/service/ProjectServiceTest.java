package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.s3.S3Service;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    private final ProjectRepository projectRepository = mock(ProjectRepository.class);

    private final S3Service s3Service = mock(S3Service.class);

    @InjectMocks
    private ProjectService projectService;

    @Test
    @DisplayName("Upload cover: check project exist")
    public void testUploadCoverCheckProjectExist() {
        Long projectId = 1L;
        MultipartFile file = mock(MultipartFile.class);
        when(projectRepository.existsById(projectId)).thenThrow();

        assertThrows(RuntimeException.class, () -> projectService.uploadCover(projectId, file));
    }

    @Test
    @DisplayName("Upload cover: check file size")
    public void testUploadCoverCheckFileSize() {
        MultipartFile file = mock(MultipartFile.class);

        when(projectRepository.existsById(1L)).thenReturn(true);
        when(file.getSize()).thenThrow();

        assertThrows(RuntimeException.class, () -> projectService.uploadCover(1L, file));
    }

    @Test
    @DisplayName("Upload cover: check image file")
    public void testUploadCoverCheckImageFile() {
        long MAX_FILE_SIZE = 4 * 1024 * 1024;
        MultipartFile file = mock(MultipartFile.class);

        when(projectRepository.existsById(1L)).thenReturn(true);
        when(file.getSize()).thenReturn(MAX_FILE_SIZE);
        when(file.getContentType()).thenReturn("notImage/jpeg");

        assertThrows(RuntimeException.class, () -> projectService.uploadCover(1L, file));
    }

    @Test
    @DisplayName("Upload cover: check execution")
    public void testUploadCover() {
        Long projectId = 1L;
        when(projectRepository.existsById(1L)).thenReturn(true);
        MultipartFile file = mock(MultipartFile.class);
        when(file.getSize()).thenReturn(1024L);
        when(file.getContentType()).thenReturn("image/jpeg");

        Project project = new Project();
        project.setId(projectId);
        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        String coverImageId = projectService.uploadCover(projectId, file);

        Assertions.assertNotNull(coverImageId);
        verify(s3Service).uploadFile(eq(file), anyString());
        verify(projectRepository).save(project);
    }

    @Test
    @DisplayName("Delete cover: check execution")
    public void testDeleteCover() {
        Long projectId = 1L;
        String coverImageId = "0d3a722f-da9e-4a1c-b8b4-8140db373a99";

        when(projectRepository.existsById(1L)).thenReturn(true);
        Project project = new Project();
        project.setId(projectId);
        project.setCoverImageId(coverImageId);
        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        projectService.removeCover(projectId);

        verify(s3Service).deleteFile(coverImageId);
        verify(projectRepository).save(project);
        Assertions.assertNull(project.getCoverImageId());
    }
}
