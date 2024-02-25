package faang.school.projectservice.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.service.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class ResourceControllerTest {
    @InjectMocks
    private ResourceController resourceController;
    @Mock
    private ResourceService resourceService;
    @Mock
    private UserContext userContext;
    MultipartFile multipartFile;
    @BeforeEach
    void setUp() {
        multipartFile = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());
    }

    @Test
    void testAddResource() {
        resourceController.addResource(1L, multipartFile);
        verify(resourceService, times(1)).addResource(1L, multipartFile, userContext.getUserId());
    }

    @Test
    void testUpdateResource() {
        resourceController.updateResource(1L, multipartFile);
        verify(resourceService, times(1)).updateResource(1L, multipartFile, userContext.getUserId());
    }

    @Test
    void testDeleteResource() {
        resourceController.deleteResource(1L);
        verify(resourceService, times(1)).deleteResource(1L, userContext.getUserId());
    }

    @Test
    void testDownloadResource() {
        resourceController.downloadResource(1L);
        verify(resourceService, times(1)).downloadResource(1L);
    }




}
