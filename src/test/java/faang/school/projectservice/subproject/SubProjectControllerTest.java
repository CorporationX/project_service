package faang.school.projectservice.subproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.projectservice.controller.subproject.SubProjectController;
import faang.school.projectservice.model.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.dto.subproject.SubProjectDto;
import faang.school.projectservice.model.entity.ProjectStatus;
import faang.school.projectservice.model.entity.ProjectVisibility;
import faang.school.projectservice.service.SubProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class SubProjectControllerTest {
    @Mock
    private SubProjectService subProjectService;
    @InjectMocks
    private SubProjectController subProjectController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private long projectId;
    private ProjectFilterDto projectFilterDto;
    private SubProjectDto createSubProjectDto;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        long ownerId = 1L;
        projectId = 2L;
        String description = "description";
        String name = "name";

        projectFilterDto = ProjectFilterDto.builder()
                .name("SD")
                .projectStatus(ProjectStatus.COMPLETED)
                .build();


        createSubProjectDto = SubProjectDto.builder()
                .name("subProjectName")
                .description("subProjectDescription")
                .ownerId(ownerId)
                .parentProjectId(projectId)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        mockMvc = MockMvcBuilders.standaloneSetup(subProjectController).build();
    }

    @Test
    @DisplayName("testing createSubProject")
    public void testCreateSubProject() throws Exception {
        var body = objectMapper.writeValueAsString(createSubProjectDto);

        mockMvc.perform(post("/api/v1/subproject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
        verify(subProjectService, times(1)).createSubProject(createSubProjectDto);
    }

    @Test
    @DisplayName("testing updateSubProjectStatus")
    public void testUpdateSubProject() throws Exception {
        var body = objectMapper.writeValueAsString(createSubProjectDto);

        mockMvc.perform(put("/api/v1/subproject/{projectId}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
        verify(subProjectService, times(1)).updateSubProject(projectId, createSubProjectDto);
    }

    @Test
    @DisplayName("testing getSubProjects")
    public void testGetSubProjects() throws Exception {
        var body = objectMapper.writeValueAsString(projectFilterDto);

        mockMvc.perform(post("/api/v1/subproject/{projectId}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
        verify(subProjectService, times(1)).getAllSubProjectsWithFilter(projectId, projectFilterDto);
    }

}
