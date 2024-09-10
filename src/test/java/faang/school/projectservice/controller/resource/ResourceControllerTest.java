package faang.school.projectservice.controller.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resource.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ResourceControllerTest {
    @InjectMocks
    private ResourceController resourceController;
    @Mock
    private ResourceService resourceService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(resourceController).build();
    }

    @Test
    void addResourceTest() throws Exception {
        Long projectId = 1L;
        MockMultipartFile file = new MockMultipartFile("file", "test.txt",
                "text/plain", "Test content".getBytes());

        ResourceDto resourceDto = new ResourceDto();
        when(resourceService.addResource(eq(projectId), any())).thenReturn(resourceDto);

        mockMvc.perform(multipart("/resources/{projectId}", projectId)
                        .file(file))
                .andExpect(status().isOk());

        verify(resourceService, times(1)).addResource(eq(projectId), any());
    }

    @Test
    void downloadResourceTest() throws Exception {
        Long resourceId = 1L;
        byte[] fileBytes = "Test file content".getBytes();
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        InputStream inputStream = new ByteArrayInputStream(fileBytes);

        Pair<MediaType, InputStream> pair = Pair.of(mediaType, inputStream);
        when(resourceService.downloadResource(resourceId)).thenReturn(pair);

        mockMvc.perform(get("/resources/{resourceId}", resourceId)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(content().bytes(fileBytes));

        verify(resourceService, times(1)).downloadResource(resourceId);
    }

    @Test
    void updateResourceTest() throws Exception {
        Long resourceId = 1L;
        MockMultipartFile file = new MockMultipartFile("file", "update.txt",
                "text/plain", "Updated content".getBytes());
        ResourceDto resourceDto = new ResourceDto();

        when(resourceService.updateResource(resourceId, file)).thenReturn(resourceDto);

        mockMvc.perform(multipart("/resources/{resourceId}", resourceId)
                        .file(file)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk());

        verify(resourceService, times(1)).updateResource(eq(resourceId), any());
    }

    @Test
    void deleteResourceTest() throws Exception {
        Long resourceId = 1L;

        mockMvc.perform(delete("/resources/{resourceId}", resourceId))
                .andExpect(status().isOk())
                .andExpect(content().string("Resource deleted successfully."));

        verify(resourceService, times(1)).deleteResource(resourceId);
    }
}
