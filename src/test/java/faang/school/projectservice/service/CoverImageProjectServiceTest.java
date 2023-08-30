package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.CoverImageFromAwsDto;
import faang.school.projectservice.dto.project.ProjectCoverImageDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.util.FileStorageService;
import faang.school.projectservice.util.ImageService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoverImageProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private ImageService imageService;
    @InjectMocks
    private CoverImageProjectService coverImageProjectService;

    @Test
    void TestUpload_CoverNotFound() {
        MultipartFile file = new MockMultipartFile("Name", new byte[1]);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            coverImageProjectService.upload(file, 1L);
        });

        assertEquals("Project with id 1 is not found", exception.getMessage());
    }

    @Test
    void testUpload() {
        byte[] byteFile = {1, 2, 3, 4, 5};
        MultipartFile file = new MockMultipartFile("Name", byteFile);
        Project project = new Project();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(imageService.resizeImage(file)).thenReturn(byteFile);
        when(imageService.resizeImage(file)).thenReturn(byteFile);
        when(fileStorageService.uploadFile(byteFile, file, 1L)).thenReturn("picture");

        ProjectCoverImageDto result = coverImageProjectService.upload(file, 1L);

        assertNotNull(result);
        assertEquals("picture", result.getFileId());
        assertEquals(1L, result.getProjectId());
    }

    @Test
    void testGetByProject() throws IOException {
        Project project = new Project();
        project.setId(1L);
        project.setCoverImageId("1");

        byte[] array = {1};

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(fileStorageService.receiveFile(anyString())).thenReturn(new CoverImageFromAwsDto(array, "jpg"));

        CoverImageFromAwsDto result = coverImageProjectService.getByProjectId(1L);

        verify(fileStorageService).uploadDefaultPicture();
        assertNotNull(result);
        assertEquals("jpg", result.getContentType());
        assertEquals(array, result.getFile());
    }

    @Test
    void deleteByUserIdTest() {
        Project project = new Project();
        project.setCoverImageId("1");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        coverImageProjectService.deleteByProjectId(1L);

        assertNull(project.getCoverImageId());
    }
}

