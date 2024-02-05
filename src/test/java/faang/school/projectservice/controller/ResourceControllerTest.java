package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.controller.ResourceController;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.service.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ResourceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ResourceService resourceService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private ResourceController resourceController;

    Long resourceId = 1L;
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(resourceController).build();
    }

    @Test
    void testDownloadResourceFile() throws Exception {
        mockMvc.perform(get("/resources/{resourceId}", resourceId))
                .andExpect(status().isOk());

        verify(resourceService).downloadResourceFile(resourceId);
    }

    @Test
    void testAddResourceFile() throws Exception {
        Long projectId = 1L;
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "some xml".getBytes());
        ResourceDto resourceDto = new ResourceDto();
        when(resourceService.addResourceFile(eq(projectId), any(MultipartFile.class), anyLong())).thenReturn(resourceDto);

        mockMvc.perform(multipart("/resources/{projectId}/file/add", projectId)
                        .file(file))
                .andExpect(status().isOk());

        verify(resourceService).addResourceFile(eq(projectId), any(MultipartFile.class), anyLong());
    }

    @Test
    void testDeleteResourceFile() throws Exception {
        when(userContext.getUserId()).thenReturn(1L);
        doNothing().when(resourceService).deleteResourceFile(eq(resourceId), eq(1L));

        mockMvc.perform(delete("/resources/{resourceId}", resourceId))
                .andExpect(status().isOk());

        verify(resourceService).deleteResourceFile(resourceId, 1L);
    }
}
