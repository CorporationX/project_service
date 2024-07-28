package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.resource.ResourceDto;
import faang.school.projectservice.service.FileServiceUpload;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestFileController {
    @InjectMocks
    private FileController fileController;

    @Mock
    private FileServiceUpload serviceUpload;
    @Mock
    private UserContext userContext;

    @Test
    public void testAddFileWhenValid() {
        long userId = 1L;
        long projectId = 1L;
        MultipartFile file = mock(MultipartFile.class);
        when(userContext.getUserId()).thenReturn(userId);
        when(serviceUpload.createFile(file, projectId, userId)).thenReturn(new ResourceDto());

        fileController.addFile(file, projectId);

        verify(serviceUpload, times(1)).createFile(file, projectId, userId);
    }

    @Test
    public void testDeleteFileWhenValid() {
        long resourceId = 1L;
        long userId = 1L;
        when(userContext.getUserId()).thenReturn(userId);

        fileController.deleteFile(resourceId);

        verify(serviceUpload, times(1)).deleteFile(resourceId, userId);
    }
}
