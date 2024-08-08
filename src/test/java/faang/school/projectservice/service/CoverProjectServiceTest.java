package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CoverProjectServiceTest {
    private static final long VALID_ID = 1L;
    private static final long INVALID_ID = -1L;
    private Project project;
    private MockMultipartFile cover;
    @Mock
    private ImageService imageService;
    @Mock
    private AmazonS3 amazonS3;
    @Mock
    private ProjectRepository repository;
    @InjectMocks
    private CoverProjectService service;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(VALID_ID);
    }

    @Test
    public void testGetNewCoverAndSave() {
        Mockito.when(imageService.resizeImage(Mockito.any())).thenReturn(new byte[]{111});
                assertThrows(RuntimeException.class, () -> service.uploadProjectCover(1L, cover));
    }
}