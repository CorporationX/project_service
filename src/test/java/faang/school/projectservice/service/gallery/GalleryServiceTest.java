package faang.school.projectservice.service.gallery;

import faang.school.projectservice.dto.gallery.GalleryResponseDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.properties.GalleryProperties;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GalleryServiceTest {

    @InjectMocks
    private GalleryServiceImpl galleryService;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private S3Service s3Client;
    @Mock
    private GalleryProperties galleryProperties;

    @BeforeEach
    public void setUp() {
        when(galleryProperties.getMaxFileSizeGalleryInBytes()).thenReturn(1024L);
        when(galleryProperties.getMaxImages()).thenReturn(10);
        galleryService.init();
    }

    @Test
    public void testUploadFilesValid() {
        long projectId = 1L;
        List<MultipartFile> files = new ArrayList<>();
        Project project = new Project();
        project.setGalleryFileKeys(new ArrayList<>());

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(s3Client.uploadFiles(files, projectId)).thenReturn(Arrays.asList("file1", "file2"));

        GalleryResponseDto response = galleryService.uploadFiles(projectId, files);

        assertEquals(projectId, response.getProjectId());
        assertEquals(2, response.getKeys().size());
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    public void testUploadFilesProjectNotFound() {
        long projectId = 1L;
        List<MultipartFile> files = new ArrayList<>();

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> galleryService.uploadFiles(projectId, files));
    }

    @Test
    public void testDeleteFiles() {
        List<String> keys = Arrays.asList("key1", "key2");

        galleryService.deleteFiles(keys);

        verify(projectRepository, times(1)).deleteGalleryByKeys(keys);
        verify(s3Client, times(1)).deleteFile("key1");
        verify(s3Client, times(1)).deleteFile("key2");
    }

}