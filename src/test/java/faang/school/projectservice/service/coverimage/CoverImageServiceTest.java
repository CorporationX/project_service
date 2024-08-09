package faang.school.projectservice.service.coverimage;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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
    private S3Service s3Service;

    private MultipartFileImpl multipartFile;

    private Project project;

    @BeforeEach
    void setUp() {
        multipartFile = new MultipartFileImpl(
                "image/jpeg", "test.jpg", "file", new byte[]{1, 2, 3, 4, 5}, 5);
        project = new Project();
    }

    @Test
    void testCreate() {
        when(coverImageSizeHandler.validateSizeAndResolution(multipartFile)).thenReturn(multipartFile);
        when(projectRepository.getProjectById(1L)).thenReturn(project);
        when(s3Service.putIntoBucket(multipartFile)).thenReturn("Key");
        when(projectRepository.save(project)).thenReturn(project);

        coverImageService.create(1L, multipartFile);

        verify(coverImageSizeHandler, times(1)).validateSizeAndResolution(multipartFile);
        verify(projectRepository, times(1)).getProjectById(1L);
        verify(s3Service, times(1)).putIntoBucket(multipartFile);
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void testDelete() {
        project.setCoverImageId("Key");

        when(projectRepository.getProjectById(1L)).thenReturn(project);
        doNothing().when(s3Service).deleteFromBucket(any(String.class));
        when(projectRepository.save(project)).thenReturn(project);

        coverImageService.delete(1L);

        verify(projectRepository, times(1)).getProjectById(1L);
        verify(s3Service, times(1)).deleteFromBucket(any(String.class));
        verify(projectRepository, times(1)).save(project);
    }
}