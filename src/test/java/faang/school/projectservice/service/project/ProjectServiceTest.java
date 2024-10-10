package faang.school.projectservice.service.project;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.FileTooLargeException;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.entity.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.AmazonS3Service;
import faang.school.projectservice.validator.project.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private AmazonS3Service amazonS3Service;

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
        projectService.maxFileSize = maxFileSize;
        project.setCoverImageId("image-id");
        contentType = "image/png";
        coverImageKey = "unique-key";
        imageData = new byte[]{1, 2, 3};
        projectService = new ProjectService(projectRepository, amazonS3Service, projectMapper, projectValidator, 1080, 566, 1080, maxFileSize);
    }


    @Test
    void testUploadCoverImage_FileTooLarge() {
        when(coverImage.getSize()).thenReturn(maxFileSize + 1);

        assertThrows(FileTooLargeException.class, () -> projectService.uploadCoverImage(projectId, coverImage));
    }

    @Test
    void testDownloadCoverImage_Success() {
        when(projectRepository.findById(projectId)).thenReturn(project);
        when(amazonS3Service.downloadFile(project.getCoverImageId())).thenReturn(new ByteArrayInputStream(new byte[]{1, 2, 3}));

        InputStream inputStream = projectService.downloadCoverImage(projectId);
        assertNotNull(inputStream);
        verify(amazonS3Service).downloadFile(project.getCoverImageId());
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
        verify(amazonS3Service).deleteFile("image-id");
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
        verify(amazonS3Service, never()).deleteFile(anyString());
    }
}