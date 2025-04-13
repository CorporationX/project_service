package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.controller.resource.ResourceController;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.service.resources.interfaces.ResourceService;
import faang.school.projectservice.service.s3.interfaces.S3Service;
import faang.school.projectservice.validation.project.ProjectValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ResourceController.class,
        excludeAutoConfiguration = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class}
)
class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResourceService resourceService;

    @MockBean
    private UserContext userContext;

    @MockBean
    private ProjectValidator projectValidator;

    @MockBean
    private S3Service s3Service;

    @MockBean(name = "jpaMappingContext")
    JpaMetamodelMappingContext jpaMappingContext;

    private final ResourceDto resourceDto = new ResourceDto(
            1L,
            "test.pdf",
            "project-folder/test.pdf",
            BigInteger.valueOf(1234),
            ResourceType.PDF,
            ResourceStatus.ACTIVE,
            LocalDateTime.now(),
            "user1"
    );

    @Test
    void uploadFile_success_returnsCreated() throws Exception {
        Long projectId = 1L;
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "test content".getBytes());

        when(resourceService.addResource(eq(projectId), any())).thenReturn(resourceDto);

        mockMvc.perform(multipart("/api/resources/{projectId}", projectId)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(resourceDto.id()))
                .andExpect(jsonPath("$.name").value(resourceDto.name()))
                .andExpect(jsonPath("$.key").value(resourceDto.key()));

        verify(resourceService).addResource(eq(projectId), any());
    }

    @Test
    void getProjectResources_success_returnsList() throws Exception {
        when(resourceService.getResources(1L)).thenReturn(List.of(resourceDto));

        mockMvc.perform(get("/api/resources/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(resourceDto.id()))
                .andExpect(jsonPath("$[0].name").value(resourceDto.name()));
    }

    @Test
    void deleteFile_success_returnsNoContent() throws Exception {
        Long projectId = 1L;
        Long resourceId = 2L;

        mockMvc.perform(delete("/api/resources/{projectId}/{resourceId}", projectId, resourceId))
                .andExpect(status().isNoContent());

        verify(resourceService).deleteResource(resourceId, projectId);
    }

    @Test
    void updateFile_success_returnsUpdatedResource() throws Exception {
        Long projectId = 1L;
        Long resourceId = 2L;
        MockMultipartFile file = new MockMultipartFile("file", "updated.pdf", "application/pdf", "updated content".getBytes());

        when(resourceService.updateResource(eq(resourceId), eq(projectId), any())).thenReturn(resourceDto);

        mockMvc.perform(multipart("/api/resources/{projectId}/{resourceId}", projectId, resourceId)
                        .file(file)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(resourceDto.id()))
                .andExpect(jsonPath("$.name").value(resourceDto.name()));

        verify(resourceService).updateResource(eq(resourceId), eq(projectId), any());
    }
}
