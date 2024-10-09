package faang.school.projectservice.controller.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.model.dto.resource.ResourceDto;
import faang.school.projectservice.service.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ResourceControllerTest {

    @Mock
    private ResourceService resourceService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private ResourceController resourceController;

    private MockMultipartFile file;
    private final long userId = 1L;
    private final long projectId = 1L;
    private final long resourceId = 1L;
    private ResourceDto resourceDto;

    @BeforeEach
    void setup() {
        file = new MockMultipartFile("someFile", "original", "TEXT", new byte[10000]);

        resourceDto = ResourceDto.builder().build();
    }

    @Test
    void testAddResourcesOk() {
        when(userContext.getUserId()).thenReturn(userId);
        when(resourceService.saveResource(anyLong(), any(MultipartFile.class), anyLong())).thenReturn(resourceDto);

        resourceController.addResources(projectId, file);

        verify(resourceService).saveResource(projectId, file, userId);
        verify(userContext).getUserId();
    }

    @Test
    void testGetFileOk() throws IOException {
        when(userContext.getUserId()).thenReturn(userId);
        when(resourceService.getFile(anyLong(), anyLong(), anyLong())).thenReturn(new InputStreamResource(file.getInputStream()));

        resourceController.getFile(projectId, resourceId);

        verify(resourceService).getFile(projectId, userId, resourceId);
    }

    @Test
    void testDeleteFileOk() {
        when(userContext.getUserId()).thenReturn(userId);

        resourceController.deleteFile(projectId, resourceId);

        verify(resourceService).deleteResource(projectId, userId, resourceId);
    }
}
