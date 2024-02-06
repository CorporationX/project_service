package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.service.resource.ResourceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {
    @Mock
    private ResourceService resourceService;
    @Mock
    private UserContext userContext;
    @InjectMocks
    private ProjectController projectController;

    @Test
    void shouldAddCoverToProject() {
        MultipartFile cover = null;
        long projectId = 1L;
        projectController.addCover(projectId, cover);
        Mockito.verify(resourceService).addCoverToProject(projectId, userContext.getUserId(), cover);
    }

    @Test
    void shouldGetCover() throws IOException {
        byte[] result = new byte[]{1, 2, 3};
        long resourceId = 1L;
        InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.when(resourceService.downloadCover(resourceId)).thenReturn(inputStream);
        Mockito.when(inputStream.readAllBytes()).thenReturn(result);
        projectController.getCover(resourceId);
        Mockito.verify(resourceService).downloadCover(resourceId);
    }
}