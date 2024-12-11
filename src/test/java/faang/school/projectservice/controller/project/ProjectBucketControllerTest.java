package faang.school.projectservice.controller.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.project.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.mockito.Mockito.when;
import java.math.BigInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = ProjectBucketController.class)
public class ProjectBucketControllerTest {
    private final static String POST_URL = "/projects/1/add-cover-image";
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Test
    public void testAddCoverImageWhenFail() throws Exception {
        Project project = createProject();
        MockMultipartFile file = createFile();

        when(projectService.uploadImage(project.getId(), file)).thenReturn(null);

        mockMvc.perform(multipart(POST_URL)
                .file(file)
                .param("coverImage", file.getName()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddCoverImageWhenSuccess() throws Exception {
        Project project = createProject();
        MockMultipartFile file = createFile();

        when(projectService.uploadImage(project.getId(), file)).thenReturn(file.getOriginalFilename());

        mockMvc.perform(multipart(POST_URL)
                        .file("coverImage", file.getBytes()))
                .andExpect(status().isOk());
    }

    private Project createProject() {
        return Project.builder()
                .id(1L)
                .name("Project")
                .ownerId(1L)
                .storageSize(BigInteger.valueOf(0))
                .maxStorageSize(BigInteger.valueOf(1000000)).build();
    }

    private MockMultipartFile createFile() {
        return new MockMultipartFile("file",
                "test.txt",
                "text/plain",
                new byte[1024]);
    }
}
