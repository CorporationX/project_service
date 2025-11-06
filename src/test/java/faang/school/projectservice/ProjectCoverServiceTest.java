package faang.school.projectservice;

import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectCover.ProjectCoverService;
import faang.school.projectservice.service.s3.S3Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-test.yml")
public class ProjectCoverServiceTest {

    @Mock
    MultipartFile file;

    @Mock
    ProjectRepository projectRepository;

    @Mock
    S3Service s3Service;

    @InjectMocks
    ProjectCoverService projectCoverService;

    @Test
    public void testNoAllowedAdd() {
        assertThrows(ForbiddenException.class, () ->
                projectCoverService.addCover(2L, 1L, file));
    }

    @Test
    public void testBigSizeAdd() {
        when(file.getSize()).thenReturn(100000000000000L);
        assertThrows(IllegalArgumentException.class, () ->
                projectCoverService.addCover(1L, 1L, file));
    }

    @Test
    public void addCover() {
        long projectId = 1L;
        long userId = 1L;
        byte[] bytes = new byte[0];
        Project project = new Project();
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        projectCoverService.addCover(projectId, userId, file);

        verify(s3Service, times(1)).uploadFile(anyString(), bytes, anyString());
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    public void downloadCover() {
        String coverImageKey = "test";

        Project project = new Project();
        project.setCoverImageId(coverImageKey);
        byte[] expected = "test".getBytes();

        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(s3Service.downloadFile(anyString())).thenReturn(expected);

        byte[] result = projectCoverService.downloadCover(1L);

        assertNotNull(result);
        assertEquals(expected, result);

        verify(projectRepository, times(1)).findById(1L);
        verify(s3Service, times(1)).downloadFile(coverImageKey);
    }

    @Test
    public void updateCover() {
        long projectId = 1L;
        long userId = 1L;
        byte[] bytes = new byte[0];
        Project project = new Project();
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        projectCoverService.updateCover(projectId, userId, file);

        verify(s3Service, times(1)).uploadFile(anyString(), bytes, anyString());
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    public void deleteCover() {
        long projectId = 1L;
        long userId = 1L;
        Project project = new Project();
        project.setOwnerId(userId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        projectCoverService.deleteCover(projectId, userId);

        verify(s3Service, times(1)).deleteFile(anyString());
    }
}