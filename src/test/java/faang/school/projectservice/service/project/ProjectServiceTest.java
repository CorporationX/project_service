package faang.school.projectservice.service.project;

import faang.school.projectservice.exception.FileTooLargeException;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.dto.ProjectDto;
import faang.school.projectservice.model.entity.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.S3Service;
import faang.school.projectservice.validator.project.ProjectValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private ProjectValidator projectValidator;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private MultipartFile coverImage;

    private long maxFileSize;
    private Long projectId;
    private String imageName;
    private Project project;
    private byte[] imageData;
    private String contentType;
    private String coverImageKey;


    @BeforeEach
    void setUp() throws IOException {
        maxFileSize = 5242880;
        projectId = 1L;
        imageName = "test.png";
        project = new Project();
        project.setId(projectId);
        projectService.setMaxFileSize(maxFileSize);
        project.setCoverImageId("image-id");
        contentType = "image/png";
        coverImageKey = "unique-key";
        imageData = new byte[]{1, 2, 3};
        projectService = new ProjectService(projectRepository, s3Service, projectMapper, projectValidator, 1080, 566, 1080, maxFileSize);
    }


    @Test
    void testUploadCoverImage_FileTooLarge() {
        when(coverImage.getSize()).thenReturn(maxFileSize + 1);

        assertThrows(FileTooLargeException.class, () -> projectService.uploadCoverImage(projectId, coverImage));
    }

    @Test
    void testDownloadCoverImage_Success() {
        when(projectRepository.findById(projectId)).thenReturn(project);
        when(s3Service.downloadCoverImage(project.getCoverImageId())).thenReturn(new ByteArrayInputStream(new byte[]{1, 2, 3}));

        InputStream inputStream = projectService.downloadCoverImage(projectId);
        assertNotNull(inputStream);
        verify(s3Service).downloadCoverImage(project.getCoverImageId());
    }

    @Test
    void testDownloadCoverImage_NoCoverImage() {
        project.setCoverImageId(null);

        when(projectRepository.findById(projectId)).thenReturn(project);

        assertThrows(EntityNotFoundException.class, () -> projectService.downloadCoverImage(projectId));
    }

    @Test
    void testDeleteCoverImage_Success() {
        when(projectRepository.findById(projectId)).thenReturn(project);
        projectService.deleteCoverImage(projectId);
        verify(s3Service).deleteCoverImage("image-id");
        verify(projectRepository).save(project);
        assertNull(project.getCoverImageId());
    }

    @Test
    void testDeleteCoverImage_NoCoverImage() {
        project.setCoverImageId(null);
        when(projectRepository.findById(projectId)).thenReturn(project);
        ProjectDto projectDto = new ProjectDto();
        when(projectMapper.toDto(project)).thenReturn(projectDto);
        ProjectDto result = projectService.deleteCoverImage(projectId);
        assertNotNull(result);
        assertNull(result.getCoverImageId());
        verify(s3Service, never()).deleteCoverImage(anyString());
    }
}