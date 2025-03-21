package faang.school.projectservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
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

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProjectControllerTest {

    private final Long firstId = 1L;
    private final Long secondId = 2L;
    private final List<ProjectDto> projects = List.of(
            createDto(firstId, "name 1", "description 1", ProjectStatus.CREATED),
            createDto(secondId, "name 2", "description 2", ProjectStatus.CANCELLED));

    @InjectMocks
    private ProjectController projectController;

    @Mock
    private ProjectService projectService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testPositiveCreateProject() throws Exception {
        ProjectDto projectDto = createDto(null, "name", "description", null);
        String jsonBody = objectMapper.writeValueAsString(projectDto);

        mockMvc.perform(post("/projects/new")
                        .param("userId", firstId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk());
    }

    @Test
    public void testPositiveUpdateProject() throws Exception {
        ProjectDto projectDto = createDto(null, "name", "description", ProjectStatus.IN_PROGRESS);
        String jsonBody = objectMapper.writeValueAsString(projectDto);

        mockMvc.perform(put("/projects/{projectId}", firstId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk());
    }

    @Test
    public void testPositiveFindProjectsByFilters() throws Exception {
        ProjectFilterDto filter = new ProjectFilterDto(null, ProjectStatus.CREATED);
        String jsonBody = objectMapper.writeValueAsString(filter);
        List<ProjectDto> filteredProjects = projects.stream()
                .filter(project -> project.status().equals(ProjectStatus.CREATED)).toList();

        when(projectService.findProjectsByFilters(firstId, filter)).thenReturn(filteredProjects);

        mockMvc.perform(post("/projects/all-filtered")
                        .param("userId", firstId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(projects.get(0).id().intValue())))
                .andExpect(jsonPath("$[0].name", is(projects.get(0).name())))
                .andExpect(jsonPath("$[0].description", is(projects.get(0).description())))
                .andExpect(jsonPath("$[0].status", is(projects.get(0).status().toString())));
    }

    @Test
    public void testPositiveGetAllProjects() throws Exception {
        when(projectService.getAllProjects(firstId)).thenReturn(projects);

        mockMvc.perform(get("/projects/all")
                        .param("userId", firstId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testPositiveGetProjectById() throws Exception {
        when(projectService.getProjectById(firstId, firstId)).thenReturn(projects.get(0));

        mockMvc.perform(get("/projects/{projectId}", firstId)
                        .param("userId", firstId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(projects.get(0).id().intValue())))
                .andExpect(jsonPath("$.name", is(projects.get(0).name())))
                .andExpect(jsonPath("$.description", is(projects.get(0).description())))
                .andExpect(jsonPath("$.status", is(projects.get(0).status().toString())));
    }

    @DisplayName("Create project dto for tests")
    private ProjectDto createDto(Long id, String name, String description, ProjectStatus status) {
        return ProjectDto.builder()
                .id(id)
                .name(name)
                .description(description)
                .status(status)
                .build();
    }
}
