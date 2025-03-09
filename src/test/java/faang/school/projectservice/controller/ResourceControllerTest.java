package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceResponseDto;
import faang.school.projectservice.service.ResourceService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ResourceControllerTest {
    @Mock
    private ResourceService resourceService;
    @InjectMocks
    private ResourceController resourceController;
    @Mock
    private UserContext userContext;
    private MultipartFile file;
    private ResourceResponseDto resourceResponseDto;

    byte[] content = new byte[]{(byte) 0xe0, 0x4f, (byte) 0xd0,
            0x20, (byte) 0xea, 0x3a, 0x69, 0x10, (byte) 0xa2, (byte) 0xd8, 0x08, 0x00, 0x2b,
            0x30, 0x30, (byte) 0x9d};

    @BeforeEach
    void setUp() {
        file = new MockMultipartFile("test", content);
        resourceResponseDto = ResourceResponseDto.builder()
                .id(123L)
                .key("test")
                .name("test_file")
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Test adding resource")
    void testAddResource() {
        Long userId = 1L;
        Long projectId = 111L;
        Mockito.when(resourceService.addResource(userId, projectId, file)).thenReturn(resourceResponseDto);
        Mockito.when(userContext.getUserId()).thenReturn(userId);
        resourceController.addResource(projectId, file);
        Mockito.verify(resourceService, Mockito.times(1))
                .addResource(userId, projectId, file);
    }

    @Test
    @DisplayName("Test deleting resource")
    void testDeleteResource() {
        Long userId = 1L;
        Long resourceId = 111L;
        resourceController.deleteResource(userId, resourceId);
        Mockito.verify(resourceService, Mockito.times(1))
                .deleteResource(userId, resourceId);
    }
}