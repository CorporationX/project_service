package faang.school.projectservice.controller.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private ProjectController projectController;

    @Test
    void testUploadCoverOk() {
        MultipartFile file =
                new MockMultipartFile("someFile", "original", "text", new byte[10000]);

        when(userContext.getUserId()).thenReturn(1L);

        projectController.uploadCover(1L, file);
    }
}
