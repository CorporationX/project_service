package faang.school.projectservice.controller.resource;


import faang.school.projectservice.config.context.UserHeaderFilter;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resource.ResourceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResourceController.class)
class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResourceService resourceService;

    @MockBean
    private UserHeaderFilter userHeaderFilter;

    @MockBean
    private JpaMetamodelMappingContext context;

    @Test
    public void testUploadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "testfile.txt", "text/plain", "Test file content".getBytes()
        );
        ResourceDto mockResponse = new ResourceDto();
        mockResponse.setId(123L);
        mockResponse.setName("testfile.txt");

        when(resourceService.uploadFile(anyLong(), any(MultipartFile.class))).thenReturn(mockResponse);

        mockMvc.perform(multipart("/api/v1/resources")
                        .file(file)
                        .param("projectId", "1")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    @Test
    public void testDownloadFile() throws Exception {
        when(resourceService.downloadFile("key")).thenReturn(InputStream.nullInputStream());

        mockMvc.perform(get("/api/v1/resources/key"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteFile() throws Exception {
        doNothing().when(resourceService).deleteFile("key");

        mockMvc.perform(delete("/api/v1/resources/key"))
                .andExpect(status().isOk());
    }
}