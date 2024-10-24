package faang.school.projectservice.controller.resource;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.controller.ResourceController;
import faang.school.projectservice.model.dto.ResourceDto;
import faang.school.projectservice.service.impl.ResourceServiceImpl;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.ByteArrayInputStream;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class ResourceControllerTest {
    @Mock
    private ResourceServiceImpl resourceService;

    @InjectMocks
    private ResourceController resourceController;

    private MockMvc mockMvc;
    private ResourceDto resultDto;
    private MockMultipartFile file;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(resourceController).build();
        file = new MockMultipartFile(
                "file",
                "testFile.txt",
                "text/plain",
                "something".getBytes()
        );

        resultDto = new ResourceDto();
        resultDto.setId(11L);
        resultDto.setName(file.getOriginalFilename());
        resultDto.setName(file.getOriginalFilename());
    }

    @Test
    void testUploadFile() throws Exception {
        Long projectId = 25L;
        when(resourceService.addResource(projectId, file))
                .thenReturn(resultDto);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/resource/{projectId}", projectId)
                        .file(file)
                        .header("x-user-id", 5L)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(resultDto.getId()))
                .andExpect(jsonPath("$.name").value(resultDto.getName()));

        verify(resourceService, times(1)).addResource(projectId, file);
    }

    @Test
    void testUploadFileNotFound() throws Exception {
        Long projectId = 25L;
        MockMultipartFile file = new MockMultipartFile("testName", new byte[34]);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/resurce/{projectId}", projectId)
                        .file(file)
                        .header("x-user-id", 5L)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUploadFile404() throws Exception {
        Long projectId = 25L;
        MockMultipartFile file = new MockMultipartFile("testName", new byte[34]);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/resource/{projectId}", projectId)
                        .file(file)
                        .header("x-user-id", 5L)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateFile() throws Exception {
        Long resourceId = 25L;
        when(resourceService.updateResource(resourceId, file))
                .thenReturn(resultDto);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/resource/{resourceId}", resourceId)
                        .file(file)
                        .header("x-user-id", 5L)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(resultDto.getId()))
                .andExpect(jsonPath("$.name").value(resultDto.getName()));

        verify(resourceService, times(1)).updateResource(resourceId, file);
    }

    @Test
    void testDeleteFile() throws Exception {
        Long resourceId = 15L;
        doNothing().when(resourceService).deleteResource(resourceId);

        mockMvc.perform(delete("/resource/{resourceId}", resourceId)
                        .header("x-user-id", 5L))
                .andExpect(status().isOk());

        verify(resourceService, times(1)).deleteResource(eq(resourceId));
    }

    @Test
    void testDownloadFile() throws Exception {
        Long resourceId = 15L;
        String content = "some words";
        String fileName = "testFile.mp3";
        S3Object s3Object = new S3Object();
        s3Object.setBucketName(fileName);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("audio/mpeg");
        s3Object.setObjectMetadata(objectMetadata);
        s3Object.setObjectContent(new ByteArrayInputStream(content.getBytes()));

        when(resourceService.getResource(resourceId)).thenReturn(s3Object);

        mockMvc.perform(MockMvcRequestBuilders.get("/resource/{resourceId}", resourceId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\""))
                .andExpect(content().contentType(new MediaType("audio", "mpeg")))
                .andExpect(content().string(content));
    }
}