package faang.school.projectservice.controller.resource;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.project.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.DoesNothing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class ProjectCoverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    private ProjectDto projectDto;
    private MockMultipartFile mockFile;

    @BeforeEach
    public void setUp() {
        projectDto = new ProjectDto();
        projectDto.setId(1L);
        projectDto.setName("Test Project");
        projectDto.setCoverImageId("project-covers/123_test.jpg");

        mockFile = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "test content".getBytes());
    }

    @Test
    public void testAddProjectCoverSuccess() throws Exception {
        when(projectService.addCoverImage(eq(1L), any(MockMultipartFile.class)))
                .thenReturn(projectDto);

        mockMvc.perform(multipart("/projects/1/cover")
                        .file(mockFile))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Project"))
                .andExpect(jsonPath("$.coverImageId").value("project-covers/123_test.jpg"));

        verify(projectService, times(1))
                .addCoverImage(eq(1L), any(MockMultipartFile.class));
    }

    @Test
    public void testAddProjectCoverProjectNotFoundThrowsException() throws Exception {
        when(projectService.addCoverImage(eq(1L), any(MockMultipartFile.class)))
                .thenThrow(new IllegalArgumentException("Project not found: 1"));

        mockMvc.perform(multipart("/projects/1/cover")
                        .file(mockFile))
                .andDo(result -> System.out.println("Response: " + result.getResponse().getContentAsString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("Project not found: 1"));

        verify(projectService, times(1))
                .addCoverImage(eq(1L), any(MockMultipartFile.class));
    }

    @Test
    public void testUpdateProjectCoverSuccess() throws Exception {
        Mockito.when(projectService.updateCoverImage(eq(1L), any(MockMultipartFile.class)))
                .thenReturn(projectDto);

        mockMvc.perform(multipart("/projects/1/cover")
                        .file(mockFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andDo(result -> System.out.println("Response: " + result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Project"))
                .andExpect(jsonPath("$.coverImageId").value("project-covers/123_test.jpg"));

        Mockito.verify(projectService, times(1))
                .updateCoverImage(eq(1L), any(MockMultipartFile.class));
    }

    @Test
    public void testUpdateProjectCoverNoCoverImageThrowsException() throws Exception {
        Mockito.when(projectService.updateCoverImage(eq(1L), any(MockMultipartFile.class)))
                .thenThrow(new IllegalStateException("Project has no cover image to update. Use add instead."));

        mockMvc.perform(multipart("/projects/1/cover")
                        .file(mockFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Project has no cover image to update. Use add instead."));

        Mockito.verify(projectService, times(1))
                .updateCoverImage(eq(1L), any(MockMultipartFile.class));
    }

    @Test
    public void testSoftDeleteProjectCoverSuccess() throws Exception {
        projectDto.setCoverImageId(null);
        Mockito.when(projectService.softDeleteCoverImage(1L)).thenReturn(projectDto);

        mockMvc.perform(delete("/projects/1/cover/soft"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Project"))
                .andExpect(jsonPath("$.coverImageId").isEmpty());

        Mockito.verify(projectService, times(1)).softDeleteCoverImage(1L);
    }

    @Test
    public void testSoftDeleteProjectCoverProjectNotFoundThrowsException() throws Exception {
        Mockito.when(projectService.softDeleteCoverImage(1L))
                .thenThrow(new IllegalArgumentException("Project not found: 1"));

        mockMvc.perform(delete("/projects/1/cover/soft"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("Project not found: 1"));

        Mockito.verify(projectService, times(1)).softDeleteCoverImage(1L);
    }

    @Test
    public void testHardDeleteProjectCoverSuccess() throws Exception {
        projectDto.setCoverImageId(null);
        doNothing().when(projectService).hardDeleteCoverImage(1L);

        mockMvc.perform(delete("/projects/1/cover/hard"))
                .andExpect(status().isNoContent());

        Mockito.verify(projectService, times(1)).hardDeleteCoverImage(1L);
    }

    @Test
    public void testHardDeleteProjectCoverProjectNotFoundThrowsException() throws Exception {
        doThrow(new IllegalArgumentException("Project not found: 1"))
                .when(projectService)
                .hardDeleteCoverImage(1L);

        mockMvc.perform(delete("/projects/1/cover/hard"))
                .andExpect(status().isNoContent());

        Mockito.verify(projectService, times(1)).hardDeleteCoverImage(1L);
    }
}