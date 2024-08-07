package faang.school.projectservice.service.coverimage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoverImageServiceTest {

    @InjectMocks
    private CoverImageService coverImageService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private CoverImageSizeHandler coverImageSizeHandler;

    @Mock
    private AmazonS3 amazonS3;

    private MultipartFileImpl multipartFile;

    private Project project;

    @BeforeEach
    void setUp() {
        multipartFile = new MultipartFileImpl(
                "image/jpeg", "test.jpg", "file", new byte[]{1,2,3,4,5}, 5);
        project = new Project();
    }

    @Test
    void testCreate() {
        when(coverImageSizeHandler.validateSizeAndResolution(multipartFile)).thenReturn(multipartFile);
        when(projectRepository.getProjectById(1L)).thenReturn(project);
        when(amazonS3.putObject(any(PutObjectRequest.class))).thenReturn(new PutObjectResult());
        when(projectRepository.save(project)).thenReturn(project);

        coverImageService.create(1L, multipartFile);

        verify(coverImageSizeHandler, times(1)).validateSizeAndResolution(multipartFile);
        verify(projectRepository, times(1)).getProjectById(1L);
        verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void testCreateWithException() {
        multipartFile.setBytes(null);

        when(coverImageSizeHandler.validateSizeAndResolution(multipartFile)).thenReturn(multipartFile);
        when(projectRepository.getProjectById(1L)).thenReturn(project);

        assertThrows(RuntimeException.class, () -> coverImageService.create(1L, multipartFile));
    }
}