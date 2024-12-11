package faang.school.projectservice.service.project;

import faang.school.projectservice.exception.project.StorageSizeExceededException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.s3.S3Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigInteger;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private S3Service s3Service;

    @MockBean
    @Qualifier("s3Client")
    private S3Service s3Client;

    @InjectMocks
    private ProjectService projectService;

    private static final List<Integer> IMAGE_DIMENSIONS = List.of(1080, 566);

    @Test
    public void testUploadImageWhenProjectIsNull() {
        when(projectRepository.getProjectById(anyLong())).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> projectService.uploadImage(anyLong(), null));
    }

    @Test
    public void testUploadImageWhenStorageSizeExceeded() {
        Project project = createProject();
        MockMultipartFile file = createFile();
        project.setStorageSize(BigInteger.valueOf(100));
        project.setMaxStorageSize(BigInteger.valueOf(1030));
        when(projectRepository.getProjectById(project.getId())).thenReturn(project);

        assertThrows(StorageSizeExceededException.class, () -> projectService.uploadImage(project.getId(), file));

        verify(projectRepository, times(1)).getProjectById(project.getId());
    }

    @Test
    public void testUploadImageWhenAllSuccess() {
        Project project = createProject();
        MockMultipartFile file = createFile();
        String folder = project.getId() + project.getName();

        when(projectRepository.getProjectById(project.getId())).thenReturn(project);
        when(s3Service.uploadCoverImage(file, folder)).thenReturn(anyString());

        String key = projectService.uploadImage(project.getId(), file);
        verify(projectRepository, times(1)).getProjectById(project.getId());

        assertNotNull(key);
        assertEquals(key, project.getCoverImageId());
    }

    private Project createProject() {
        return Project.builder()
                .id(1L)
                .name("Project")
                .ownerId(1L)
                .storageSize(BigInteger.valueOf(0))
                .maxStorageSize(BigInteger.valueOf(1000000)).build();
    }

    private MockMultipartFile createFile() {
        return new MockMultipartFile("file",
                "test.txt",
                "text/plain",
                new byte[1024]);
    }

}
