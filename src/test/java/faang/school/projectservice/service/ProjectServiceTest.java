package faang.school.projectservice.service;

import faang.school.projectservice.config.cover.ProjectCoverConfiguration;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.utils.ImageResizer;
import faang.school.projectservice.validation.CoverValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private S3Service s3Service;
    @Mock
    private CoverValidator coverValidator;
    @Mock
    private ImageResizer<ProjectCoverConfiguration> imageResizer;
    @Mock
    private MultipartFile image;
    @Mock
    private ProjectCoverConfiguration config;

    @InjectMocks
    private ProjectService projectService;

    private static final String KEY = "key";
    private static final String OLD_KEY = "oldKey";
    private final Project project = new Project();
    private final Long projectId = 1L;
    private String folder;

    @BeforeEach
    public void setUp() {

        project.setId(projectId);
        folder = String.format("projects/%d/cover", projectId);
    }

    @DisplayName("Успешная загрузка новой обложки проекта")
    @Test
    public void uploadCover_WhenValidImage_ThenUploadsAndUpdatesProject() {
        when(coverValidator.isImageOversize(image, config))
                .thenReturn(false);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(s3Service.uploadImage(folder, image))
                .thenReturn(KEY);

        projectService.uploadCover(projectId, image);

        verify(coverValidator, times(1)).validateBasics(image, config);
        verify(coverValidator, times(1)).isImageOversize(image, config);
        verify(projectRepository, times(1)).findById(projectId);
        verify(s3Service, times(1)).uploadImage(folder, image);
    }

    @DisplayName("Сжатие и загрузка слишком большой обложки")
    @Test
    public void uploadCover_WhenOversizeImage_ThenResizesAndUploads() {
        when(coverValidator.isImageOversize(image, config))
                .thenReturn(true);
        when(imageResizer.resizeImage(image, config)).thenReturn(image);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(s3Service.uploadImage(folder, image))
                .thenReturn(KEY);

        projectService.uploadCover(projectId, image);

        verify(coverValidator, times(1)).validateBasics(image, config);
        verify(coverValidator, times(1)).isImageOversize(image, config);
        verify(imageResizer, times(1)).resizeImage(image, config);
        verify(projectRepository, times(1)).findById(projectId);
        verify(s3Service, times(1)).uploadImage(folder, image);
    }

    @DisplayName("Удаление старой обложки при загрузке новой")
    @Test
    public void uploadCover_WhenExistingCover_ThenDeletesOldImage() {
        project.setCoverImageId(OLD_KEY);

        when(coverValidator.isImageOversize(image, config))
                .thenReturn(false);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(s3Service.uploadImage(folder, image))
                .thenReturn(KEY);

        projectService.uploadCover(projectId, image);

        verify(coverValidator, times(1)).validateBasics(image, config);
        verify(coverValidator, times(1)).isImageOversize(image, config);
        verify(projectRepository, times(1)).findById(projectId);
        verify(s3Service, times(1)).uploadImage(folder, image);
        verify(s3Service, times(1)).deleteImage(OLD_KEY);
    }

    @DisplayName("Ошибка при загрузке: проект не найден")
    @Test
    public void uploadCover_WhenProjectNotFound_ThenThrowsException() {
        when(coverValidator.isImageOversize(image, config))
                .thenReturn(false);
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> projectService.uploadCover(projectId, image));
        assertEquals("Project not found", exception.getMessage());

        verify(coverValidator, times(1)).validateBasics(image, config);
        verify(coverValidator, times(1)).isImageOversize(image, config);
        verify(projectRepository, times(1)).findById(projectId);
    }

    @DisplayName("Успешное удаление обложки")
    @Test
    public void deleteCover_WhenKeyExists_DeletesImageAndUpdatesProject() {
        project.setCoverImageId(OLD_KEY);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        projectService.deleteCover(projectId);

        verify(projectRepository, times(1)).findById(projectId);
        verify(s3Service, times(1)).deleteImage(OLD_KEY);
    }

    @DisplayName("Ошибка при удалении: проект не найден")
    @Test
    public void deleteCover_WhenProjectNotFound_ThrowsException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> projectService.deleteCover(projectId));
        assertEquals("Project not found", exception.getMessage());

        verify(projectRepository, times(1)).findById(projectId);
    }

    @DisplayName("Ошибка при удалении: обложка не найдена")
    @Test
    public void deleteCover_WhenKeyIsNull_ThrowsException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        Exception exception = assertThrows(DataValidationException.class,
                () -> projectService.deleteCover(projectId));
        assertEquals("Cover image id is null", exception.getMessage());

        verify(projectRepository, times(1)).findById(projectId);
    }
}
