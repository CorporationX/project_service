package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.resource.ResourceDto;
//import faang.school.projectservice.service.FileUploadService;
import org.junit.jupiter.api.BeforeEach;
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

//@ExtendWith(MockitoExtension.class)
//public class FileControllerTest {
//    @InjectMocks
//    private FileController fileController;
//
//    @Mock
//    private FileUploadService fileUploadService;
//    @Mock
//    private UserContext userContext;
//
//    private long userId;
//    private long projectId;
//    private long resourceId;
//
//    @BeforeEach
//    public void setUp() {
//        long userId = 1L;
//        long projectId = 1L;
//        long resourceId = 1L;
//    }
//
//    @Test
//    public void testAddFileWhenValid() {
//        MultipartFile file = mock(MultipartFile.class);
//        when(userContext.getUserId()).thenReturn(userId);
//        when(fileUploadService.createFile(file, projectId, userId)).thenReturn(new ResourceDto());
//
//        fileController.addFile(file, projectId);
//
//        verify(fileUploadService, times(1)).createFile(file, projectId, userId);
//    }
//
//    @Test
//    public void testDeleteFileWhenValid() {
//        when(userContext.getUserId()).thenReturn(userId);
//
//        fileController.deleteFile(resourceId);
//
//        verify(fileUploadService, times(1)).deleteFile(resourceId, userId);
//    }
//}
