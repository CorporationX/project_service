package faang.school.projectservice.service.resource;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

import static faang.school.projectservice.util.project.ProjectFabric.buildMultiPartFile;
import static faang.school.projectservice.util.project.ProjectFabric.buildProjectName;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {
    private static final long PROJECT_ID = 1L;
    private static final String PROJECT_NAME = "Project name";
    private static final String NAME = "Name";
    private static final long FILE_SIZE = 1;
    private static final String CONTENT_TYPE = "image/jpeg";
    private static final InputStream INPUT_STREAM = mock(InputStream.class);

    @Mock
    private AmazonS3 client;

    @InjectMocks
    private S3Service s3Service;

    @Test
    @DisplayName("Given ")
    void testUploadProjectCoverImage() {
        Project project = buildProjectName(PROJECT_ID, PROJECT_NAME);
        MultipartFile multipartFile = buildMultiPartFile(NAME, FILE_SIZE, CONTENT_TYPE, INPUT_STREAM);
        s3Service.uploadProjectCoverImage(multipartFile, project);

        verify(client).putObject(any(PutObjectRequest.class));
    }
}