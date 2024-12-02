package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.service.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ResourceControllerTest {
    private MockMvc mockMvc;

    @Mock
    private ResourceService resourceService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private ResourceController resourceController;

    long projectId;
    long userId;
    long resourceId;
    ResourceDto resourceDto;
    MockMultipartFile file;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(resourceController).build();
        projectId = 1L;
        userId = 1L;
        resourceId = 1L;

        file = new MockMultipartFile(
                "file",
                "test.txt"
                , "text/plain"
                , "content".getBytes()
        );

        resourceDto = new ResourceDto(
                1L,
                userId,
                userId,
                projectId,
                ResourceType.TEXT,
                ResourceStatus.ACTIVE,
                BigInteger.valueOf(6L)
        );
    }

    @Test
    void testUploadResource() throws Exception {
        when(userContext.getUserId()).thenReturn(userId);
        when(resourceService.uploadResource(projectId, userId, file)).thenReturn(resourceDto);

        mockMvc.perform(multipart("/project/{projectId}/resource", projectId)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.creatorId").value(userId))
                .andExpect(jsonPath("$.updaterId").value(userId))
                .andExpect(jsonPath("$.projectId").value(projectId))
                .andExpect(jsonPath("$.type").value("TEXT"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.size").value(6));

        verify(resourceService).uploadResource(projectId, userId, file);
    }

    @Test
    void testDeleteResource() throws Exception {
        when(userContext.getUserId()).thenReturn(userId);
        when(resourceService.deleteResource(resourceId, userId)).thenReturn(resourceDto);

        mockMvc.perform(delete("/resource/{resourceId}", resourceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(resourceId))
                .andExpect(jsonPath("$.creatorId").value(userId))
                .andExpect(jsonPath("$.updaterId").value(userId))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.size").value(6));

        verify(resourceService).deleteResource(resourceId, userId);
    }

    @Test
    void testUpdateResource() throws Exception {
        when(userContext.getUserId()).thenReturn(userId);
        when(resourceService.updateResource(resourceId, userId, file)).thenReturn(resourceDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/resource/{resourceId}", resourceId)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(resourceId))
                .andExpect(jsonPath("$.creatorId").value(userId))
                .andExpect(jsonPath("$.updaterId").value(userId))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.size").value(6));

        verify(resourceService).updateResource(resourceId, userId, file);
    }

    @Test
    void testInvalidUserId() {
        long projectId = 1L;
        long invalidUserId = -1L;

        when(userContext.getUserId()).thenReturn(invalidUserId);

        assertThrows(IllegalArgumentException.class,
                () -> resourceController.uploadResource(projectId, file));
    }
}
