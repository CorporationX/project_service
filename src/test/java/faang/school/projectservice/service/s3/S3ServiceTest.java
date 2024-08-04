package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.project.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {
    @Mock
    private AmazonS3 s3client;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectValidator validator;
    @InjectMocks
    private S3Service s3Service;
    private final Long projectId = 1L;
    private final String projectName = "Project name";
    private final String fileName = "File name";
    private final String originalFileName = "filename.txt";
    private final String contentType = "JPEG";
    private byte[] imageByte = "data".getBytes();
    private Project project = Project.builder()
            .id(projectId)
            .name(projectName)
            .build();
    private MultipartFile file;

    @BeforeEach
    void setUp() {
        file = new MockMultipartFile(fileName, originalFileName, contentType, imageByte);

        ReflectionTestUtils.setField(s3Service, "bucketName", "project-bucket");
    }

    @Test
    public void testAddCover() {
        when(validator.getProjectAfterValidateId(projectId)).thenReturn(project);

        String result = s3Service.addCover(projectId, file);

        verify(s3client).putObject(any());
        verify(projectRepository).save(project);
        assertTrue(project.getCoverImageId().contains(originalFileName));
        assertEquals("Cover Image added to project " + projectId, result);
    }
}
