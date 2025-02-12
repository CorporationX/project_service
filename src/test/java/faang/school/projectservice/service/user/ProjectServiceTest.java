package faang.school.projectservice.service.user;

import com.amazonaws.services.s3.AmazonS3;
import faang.school.projectservice.config.S3.S3Config;
import faang.school.projectservice.exception.ImageResizeException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ImageResizer;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project project;

    @Mock
    private AmazonS3 amazonS3Client;

    @Mock
    private ImageResizer imageResizer;

    @Mock
    private S3Config s3Config;

    @Mock
    private MultipartFile multipartFile;

    private static final Long PROJECT_ID = 1L;
    private static final String OBJECT_NAME = "project-1-cover.jpg";

    @BeforeEach
    void setUp() {
        project = Project.builder()
                .id(1L)
                .name("Test Project")
                .description("Test Description")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createProject_ShouldSaveAndReturnProject() {
        when(projectRepository.existsByOwnerIdAndName(project.getOwnerId(), project.getName())).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        Project result = projectService.createProject(project, project.getOwnerId());

        assertNotNull(result);
        assertEquals("Test Project", result.getName());
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void createProject_ShouldThrowExceptionIfProjectExists() {
        when(projectRepository.existsByOwnerIdAndName(project.getOwnerId(), project.getName())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> projectService.createProject(project, project.getOwnerId()));

        assertEquals("Project with the same name already exists", exception.getMessage());
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void updateProject_ShouldUpdateAndReturnProject() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        Project updatedProject = projectService.updateProject(project);

        assertNotNull(updatedProject);
        assertEquals("Test Project", updatedProject.getName());
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void updateProject_ShouldThrowExceptionIfNotFound() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> projectService.updateProject(project));

        assertEquals("Project not found", exception.getMessage());
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void getProjectById_ShouldReturnProject() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

        Project result = projectService.getProjectById(project.getId());

        assertNotNull(result);
        assertEquals("Test Project", result.getName());
        verify(projectRepository, times(1)).findById(project.getId());
    }

    @Test
    void getProjectById_ShouldThrowExceptionIfNotFound() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> projectService.getProjectById(project.getId()));

        assertEquals("Project not found", exception.getMessage());
    }

    @Test
    void testUploadProjectCover_Success() throws IOException {

        byte[] imageBytes = new byte[]{1, 2, 3};
        byte[] resizedImageBytes = new byte[]{4, 5, 6};
        Project project = new Project();
        project.setId(PROJECT_ID);

        when(s3Config.amazonS3Client()).thenReturn(amazonS3Client);
        when(multipartFile.getSize()).thenReturn(4L * 1024 * 1024);
        when(multipartFile.getBytes()).thenReturn(imageBytes);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(imageResizer.resizeImage(imageBytes, 1080, 566)).thenReturn(resizedImageBytes);
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));

        projectService.uploadProjectCover(PROJECT_ID, multipartFile);

        verify(imageResizer).resizeImage(imageBytes, 1080, 566);
        verify(projectRepository).save(project);
        assertEquals(OBJECT_NAME, project.getCoverImageId());
    }

    @Test
    void testUploadProjectCover_FileSizeExceeded() {
        when(multipartFile.getSize()).thenReturn(6L * 1024 * 1024);

        assertThrows(MaxUploadSizeExceededException.class, () -> {
            projectService.uploadProjectCover(PROJECT_ID, multipartFile);
        });
    }

    @Test
    void testUploadProjectCover_ResizeFailed() throws IOException {
        byte[] imageBytes = new byte[]{1, 2, 3};
        when(multipartFile.getSize()).thenReturn(4L * 1024 * 1024);
        when(multipartFile.getBytes()).thenReturn(imageBytes);
        when(imageResizer.resizeImage(imageBytes, 1080, 566)).thenThrow(new IOException("Resize failed"));

        assertThrows(ImageResizeException.class, () -> {
            projectService.uploadProjectCover(PROJECT_ID, multipartFile);
        });
    }

    @Test
    void testDeleteCover_Success() {
        Project project = new Project();
        project.setId(PROJECT_ID);
        project.setCoverImageId(OBJECT_NAME);

        when(s3Config.amazonS3Client()).thenReturn(amazonS3Client);
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));

        projectService.deleteCover(PROJECT_ID);

        verify(projectRepository).save(project);
        assertNull(project.getCoverImageId());
    }

    @Test
    void testDeleteCover_CoverNotFound() {
        Project project = new Project();
        project.setId(PROJECT_ID);
        project.setCoverImageId(null);

        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));

        assertThrows(IllegalArgumentException.class, () -> {
            projectService.deleteCover(PROJECT_ID);
        });
    }
}