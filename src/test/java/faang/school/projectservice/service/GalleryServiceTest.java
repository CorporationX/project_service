package faang.school.projectservice.service;

import faang.school.projectservice.exception.CoverMaxSizeException;
import faang.school.projectservice.exception.ExceptionMessage;
import faang.school.projectservice.exception.FileLimitException;
import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectGallery;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectGalleryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GalleryServiceTest {
    @Mock
    private ProjectGalleryRepository projectGalleryRepository;

    @Mock
    private MinioService minioService;

    @Mock
    private ProjectService projectService;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private GalleryService galleryService;

    private final Long projectId = 1L;
    private final String fileKey = "image.jpg_123-uuid";

    @Test
    void positive_uploadImage_shouldUploadImage() {
        when(multipartFile.getSize()).thenReturn(1024L); // < MAX_FILE_SIZE
        when(multipartFile.getOriginalFilename()).thenReturn("image.jpg");
        when(projectGalleryRepository.countByProjectId(projectId)).thenReturn(0);
        Project mockProject = new Project();
        when(projectService.findById(projectId)).thenReturn(Optional.of(mockProject));

        ArgumentCaptor<ProjectGallery> captor = ArgumentCaptor.forClass(ProjectGallery.class);
        when(projectGalleryRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        String result = galleryService.uploadImage(projectId, multipartFile);

        verify(minioService).uploadFile(eq(multipartFile), anyString());
        verify(projectGalleryRepository).save(captor.capture());
        assertNotNull(result);
        assertEquals(captor.getValue().getProject(), mockProject);
    }

    @Test
    void positive_deleteImage_shouldDelete() {
        ProjectGallery gallery = ProjectGallery.builder().fileKey(fileKey).build();
        when(projectGalleryRepository.findByProjectId(projectId)).thenReturn(List.of(gallery));

        galleryService.deleteImage(projectId, fileKey);

        verify(minioService).deleteFile(fileKey);
        verify(projectGalleryRepository).delete(gallery);
    }

    @Test
    void positive_getKeyListByProjectId_shouldReturnKeys() {
        Project project = new Project();
        project.setVisibility(ProjectVisibility.PUBLIC);
        when(projectService.findById(projectId)).thenReturn(Optional.of(project));

        ProjectGallery g1 = ProjectGallery.builder().fileKey("file1").build();
        ProjectGallery g2 = ProjectGallery.builder().fileKey("file2").build();
        when(projectGalleryRepository.findByProjectId(projectId)).thenReturn(List.of(g1, g2));

        List<String> result = galleryService.getKeyListByProjectId(projectId);

        assertEquals(List.of("file1", "file2"), result);
    }

    @Test
    void negative_uploadImage_shouldThrowExceptionFileIsTooLarge() {
        when(multipartFile.getSize()).thenReturn(10L * 1024 * 1024L); // > MAX_FILE_SIZE

        CoverMaxSizeException exception = assertThrows(CoverMaxSizeException.class, () ->
                galleryService.uploadImage(projectId, multipartFile));
        assertEquals(String.format(ExceptionMessage.FILE_IS_LARGE.getMessage(), 5),
                exception.getMessage(), exception.getMessage());
    }

    @Test
    void negative_uploadImage_shouldThrowExceptionFileLimitExceeded() {
        when(multipartFile.getSize()).thenReturn(1024L);
        when(projectGalleryRepository.countByProjectId(projectId)).thenReturn(100);

        FileLimitException exception = assertThrows(FileLimitException.class, () ->
                galleryService.uploadImage(projectId, multipartFile));
        assertEquals(String.format(ExceptionMessage.FILE_COUNT_LIMIT_EXCEEDED.getMessage(),50),
                exception.getMessage());
    }

    @Test
    void negative_deleteImage_shouldThrowIfNotFound() {
        when(projectGalleryRepository.findByProjectId(projectId)).thenReturn(List.of());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                galleryService.deleteImage(projectId, fileKey));
        assertTrue(exception.getMessage().contains("File with name"));
    }

    @Test
    void negative_getKeyListByProjectId_shouldThrowProjectNotFound() {
        when(projectService.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () ->
                galleryService.getKeyListByProjectId(projectId));
    }
}
