package faang.school.projectservice.service.user;

import faang.school.projectservice.config.ProjectProperties;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ImageResizer;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.google.GoogleCalendarService;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.validator.FileValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private GoogleCalendarService googleCalendarService;

    @Mock
    private S3Service s3Service;

    @Mock
    private FileValidator fileValidator;

    @Mock
    private ImageResizer imageResizer;

    @Mock
    private MultipartFile file;

    @Spy
    private ProjectProperties projectProperties;

    @InjectMocks
    private ProjectService projectService;

    private Project project;

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
        when(googleCalendarService.createCalendar(any())).thenReturn(new com.google.api.services.calendar.model.Calendar());

        Project result = projectService.createProject(project, project.getOwnerId());

        assertNotNull(result);
        assertEquals("Test Project", result.getName());
        verify(projectRepository, times(1)).save(project);
        verify(googleCalendarService, times(1)).createCalendar(any());
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
        byte[] mockImageBytes = new byte[]{1, 2, 3};
        byte[] mockResizedImageBytes = new byte[]{4, 5, 6};

        when(file.getBytes()).thenReturn(mockImageBytes);
        when(projectProperties.getTargetWidth()).thenReturn(1080);
        when(projectProperties.getTargetHeight()).thenReturn(566);
        when(imageResizer.resizeImage(mockImageBytes, 1080, 566))
                .thenReturn(mockResizedImageBytes);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(s3Service.uploadFile(any(), anyString())).thenReturn(new Resource());

        projectService.uploadProjectCover(1L, file);

        verify(fileValidator).validateFile(file);
        verify(imageResizer).resizeImage(mockImageBytes, 1080, 566);
        verify(s3Service).uploadFile(any(), anyString());
        verify(projectRepository).save(project);
    }

    @Test
    void testUploadProjectCover_ProjectNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> projectService.uploadProjectCover(1L, file));
        verify(fileValidator, never()).validateFile(file);
        verify(s3Service, never()).uploadFile(any(), anyString());
        verify(projectRepository, never()).save(any());
    }

    @Test
    void testUploadProjectCover_FileReadError() throws IOException {
        when(file.getBytes()).thenThrow(new IOException("Failed to read file"));

        assertThrows(RuntimeException.class, () -> projectService.uploadProjectCover(1L, file));
        verify(fileValidator).validateFile(file);
        verify(s3Service, never()).uploadFile(any(), anyString());
        verify(projectRepository, never()).save(any());
    }

    @Test
    void testDeleteCover_Success() {
        project.setCoverImageId("covers/1_cover.jpg");
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        projectService.deleteCover(1L);

        verify(s3Service).deleteFile("covers/1_cover.jpg");
        verify(projectRepository).save(project);
        assertNull(project.getCoverImageId());
    }

    @Test
    void testDeleteCover_ProjectNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> projectService.deleteCover(1L));
        verify(s3Service, never()).deleteFile(anyString());
        verify(projectRepository, never()).save(any());
    }

    @Test
    void testDeleteCover_NoCoverImage() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        projectService.deleteCover(1L);

        verify(s3Service, never()).deleteFile(anyString());
        verify(projectRepository, never()).save(project);
        assertNull(project.getCoverImageId());
    }
}