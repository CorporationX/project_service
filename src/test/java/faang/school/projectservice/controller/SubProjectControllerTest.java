package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
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
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SubProjectControllerTest {

    @Mock
    private SubProjectService subProjectService;

    @InjectMocks
    private SubProjectController subProjectController;

    private MockMvc mockMvc;

    private long projectId;
    private ProjectDto projectDto;
    private ProjectFilterDto projectFilterDto;
    private CreateSubProjectDto createSubProjectDto;
    private String projectDtoJson;
    private String projectFilterDtoJson;
    private String createSubProjectDtoJson;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        long ownerId = 1L;
        projectId = 2L;
        String description = "descrioption";
        String name = "name";

        projectFilterDto = new ProjectFilterDto();

        createSubProjectDto = CreateSubProjectDto.builder()
                .name("subProjectName")
                .description("subProjectDescription")
                .ownerId(ownerId)
                .parentProjectId(projectId)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        projectDto = ProjectDto.builder()
                .ownerId(ownerId)
                .name(name)
                .description(description)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        projectDtoJson = objectMapper.writeValueAsString(projectDto);
        projectFilterDtoJson = objectMapper.writeValueAsString(projectFilterDto);
        createSubProjectDtoJson = objectMapper.writeValueAsString(createSubProjectDto);
        mockMvc = MockMvcBuilders.standaloneSetup(subProjectController).build();
    }

    @Test
    @DisplayName("testing createSubProject")
    public void testCreateSubProject() throws Exception {
        mockMvc.perform(post("/api/v1/subproject/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createSubProjectDtoJson))
                .andExpect(status().isCreated());
        verify(subProjectService, times(1)).createSubProject(createSubProjectDto);
    }

    @Test
    @DisplayName("testing updateSubProjectStatus")
    public void testUpdateSubProject() throws Exception {
        mockMvc.perform(put("/api/v1/subproject/{projectId}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectDtoJson))
                .andExpect(status().isOk());
        verify(subProjectService, times(1)).updateProject(projectId, projectDto);
    }

    @Test
    @DisplayName("testing getSubProjects")
    public void testGetSubProjects() throws Exception {
        mockMvc.perform(post("/api/v1/subproject/{projectId}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectFilterDtoJson))
                .andExpect(status().isOk());
        verify(subProjectService, times(1)).getSubProjects(projectId, projectFilterDto);
    }
}