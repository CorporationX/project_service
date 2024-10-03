package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.mapper.ResourceMapperImpl;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.service.project.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ProjectControllerTest {

    private MockMvc mockMvc;

    private final ProjectService projectService = mock(ProjectService.class);
    private final ResourceMapper resourceMapper = new ResourceMapperImpl();

    private final ProjectController projectController = new ProjectController(projectService, resourceMapper);


    private ResourceDto resultDto;
    private MockMultipartFile mockFile;
    private Resource resource;

    @BeforeEach
    void setUp() throws IOException {
        mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();
        File file = ResourceUtils.getFile("src/test/utils/TEST.jpg");
        FileInputStream inputStream = new FileInputStream(String.valueOf(file));
        mockFile = new MockMultipartFile(
                "file",
                file.getName(),
                MediaType.IMAGE_JPEG_VALUE,
                inputStream.readAllBytes()
        );

        resource = Resource.builder()
                .key(file.getPath())
                .name(mockFile.getOriginalFilename())
                .size(BigInteger.valueOf(mockFile.getSize()))
                .type(mockFile.getContentType())
                .status(ResourceStatus.ACTIVE)
                .build();
        System.out.println(mockFile);
        resultDto = resourceMapper.toResourceDto(resource);
    }

    @Test
    public void testUploadFileToProject() throws Exception {
        when(projectService.uploadFileToProject(1L, 100L, mockFile)).thenReturn(resource);
        System.out.println(resource);
        MvcResult result = mockMvc.perform(multipart("/1/files")
                        .file(mockFile)
                        .with(request -> {
                            request.setMethod("POST");
                            request.setParameter("user-id", String.valueOf(100L));
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(resource.getName()))
                .andExpect(jsonPath("$.key").value(resource.getKey()))
                .andExpect(jsonPath("$.size").value(resource.getSize().intValue()))
                .andExpect(jsonPath("$.type").value(resource.getType()))
                .andExpect(jsonPath("$.status").value(resource.getStatus().name()))
                .andReturn();
        assertTrue(result.getResponse().getContentAsString().contains(resultDto.getName()));
    }

}
