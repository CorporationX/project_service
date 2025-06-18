package faang.school.projectservice.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.projectservice.config.CoverImageConfig;
import faang.school.projectservice.dto.ImageConfig;
import faang.school.projectservice.exception.ProjectImageCoverException;
import faang.school.projectservice.exception.ProjectNotFound;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.util.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    private static final Long PROJECT_ID = 1L;
    private static final String IMAGE_KEY = "image-key";

    @Mock
    private AmazonS3Service amazonS3Service;
    @Mock
    private ProjectRepository projectRepository;
    private ImageConfig coverImageConfig;
    private final Utils utils = new Utils();

    private ProjectService projectService;

    @BeforeEach
    public void setUp() {
        setUpProjectCoverImageConfig();
        projectService = new ProjectService(amazonS3Service, projectRepository, coverImageConfig, utils);
    }

    @Test
    public void testAddCoverImageSuccess() throws IOException {
        // Создаем тестовое изображение
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
        ObjectMetadata metadata = new ObjectMetadata();

        // Создаем мок MultipartFile
        final MultipartFile mockMultipartFile = new MockMultipartFile(
                "image.jpg",
                "original-image.jpg",
                "image/jpeg",
                baos.toByteArray()
        );

        when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
        when(projectRepository.findCoverImageIdById(PROJECT_ID)).thenReturn(Optional.empty());
        when(amazonS3Service.uploadImage(mockMultipartFile, ProjectService.COVER_PREFIX, coverImageConfig))
                .thenReturn(IMAGE_KEY);
        doNothing().when(projectRepository).updateCoverImage(PROJECT_ID, IMAGE_KEY);

        projectService.addCoverImage(PROJECT_ID, mockMultipartFile);
        verify(projectRepository).updateCoverImage(PROJECT_ID, IMAGE_KEY);
    }

    @Test
    public void testAddCoverImage_ExceptionInGetImageKey() throws IOException {
        when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
        doThrow(IOException.class).when(amazonS3Service).uploadImage(any(), any(), any());

        ProjectImageCoverException resultException = assertThrows(
                ProjectImageCoverException.class,
                () -> projectService.addCoverImage(PROJECT_ID, any()));

        verify(projectRepository, times(0)).updateCoverImage(PROJECT_ID, IMAGE_KEY);
        String expectedError = ProjectService.ERROR_UPLOAD_PROJECT_IMAGE_COVER;
        assertEquals(expectedError, resultException.getMessage());
    }

    @Test
    public void testAddCoverImage_FailProjectIsMissing() throws IOException {
        when(projectRepository.existsById(PROJECT_ID)).thenReturn(false);

        ProjectNotFound resultException = assertThrows(
                ProjectNotFound.class,
                () -> projectService.addCoverImage(PROJECT_ID, any()));

        verify(projectRepository, times(0)).updateCoverImage(PROJECT_ID, IMAGE_KEY);
        String expectedError = utils.format(ProjectService.PROJECT_NOT_FOUND, PROJECT_ID);
        assertEquals(expectedError, resultException.getMessage());
    }

    @Test
    public void testDeleteCovertImageSuccess() {
        when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
        when(projectRepository.findCoverImageIdById(PROJECT_ID)).thenReturn(Optional.of(IMAGE_KEY));
        doNothing().when(amazonS3Service).deleteImage(IMAGE_KEY);
        doNothing().when(projectRepository).updateCoverImage(PROJECT_ID, null);

        projectService.deleteCoverImage(PROJECT_ID);

        verify(projectRepository).updateCoverImage(PROJECT_ID, null);
    }

    private void setUpProjectCoverImageConfig() {
        coverImageConfig = new CoverImageConfig();
        coverImageConfig.setFileSizeMb(1);
        coverImageConfig.setSquareSize(100);
        coverImageConfig.setRectWidthSize(100);
        coverImageConfig.setRectHeightSize(60);
    }
}