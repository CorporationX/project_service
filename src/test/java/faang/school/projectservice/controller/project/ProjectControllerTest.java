package faang.school.projectservice.controller.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.filter.ProjectFilterDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.project.ProjectService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProjectControllerTest {
    @Mock
    private ProjectService projectService;
    @InjectMocks
    private ProjectController projectController;
    private final long ownerId = 1L;
    private final String name = "Project";
    private final String description = "Cool project";
    private final long projectId = 10L;
    private final ProjectVisibility visibility = ProjectVisibility.PUBLIC;
    private ProjectDto projectDto;
    private ProjectFilterDto filters;
    private MockMvc mockMvc;
    private final String userId = "3";

    @BeforeEach
    void setUp() {
        projectDto = ProjectDto.builder()
                .id(projectId)
                .name(name)
                .description(description)
                .ownerId(ownerId)
                .visibility(visibility)
                .build();
        mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();
    }

    @Test
    public void testCreateInvalidArguments() {
        String blank = "  ";
        RuntimeException e;

        projectDto.setDescription(blank);
        e = Assertions.assertThrows(RuntimeException.class, () -> projectController.create(userId, projectDto));
        Assertions.assertEquals(e.getMessage(), "Invalid description " + projectDto.getDescription());

        projectDto.setDescription(null);
        e = Assertions.assertThrows(RuntimeException.class, () -> projectController.create(userId, projectDto));
        Assertions.assertEquals(e.getMessage(), "Invalid description " + projectDto.getDescription());

        projectDto.setDescription(description);
        projectDto.setName(blank);
        e = Assertions.assertThrows(RuntimeException.class, () -> projectController.create(userId, projectDto));
        Assertions.assertEquals(e.getMessage(), "Invalid name " + projectDto.getName());

        projectDto.setName(null);
        e = Assertions.assertThrows(RuntimeException.class, () -> projectController.create(userId, projectDto));
        Assertions.assertEquals(e.getMessage(), "Invalid name " + projectDto.getName());
    }

    @Test
    public void testCreate() throws Exception {
        String jsonRequest = new ObjectMapper().writeValueAsString(projectDto);
        Mockito.when(projectService.create(projectDto)).thenReturn(projectDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/project/create")
                        .header("x-user-id", "3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(projectDto.getName()));

        Mockito.verify(projectService).create(projectDto);
    }

    @Test
    public void testUpdateInvalidArguments() {
        long id = 0L;
        projectDto.setId(id);
        Assertions.assertThrows(RuntimeException.class, () -> projectController.update(projectDto));

        projectDto.setId(null);
        Assertions.assertThrows(RuntimeException.class, () -> projectController.update(projectDto));
    }

    @Test
    public void testUpdate() throws Exception {
        String jsonRequest = new ObjectMapper().writeValueAsString(projectDto);
        Mockito.when(projectService.update(projectDto)).thenReturn(projectDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/project/update")
                        .header("x-user-id", "3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(projectDto.getName()));

        Mockito.verify(projectService).update(projectDto);
    }

    @Test
    public void testGetProjectsWithFilters() throws Exception {
        filters = new ProjectFilterDto("name", ProjectStatus.CREATED);
        Mockito.when(projectService.getProjectsWithFilters(filters)).thenReturn(List.of(projectDto));
        String jsonRequest = new ObjectMapper().writeValueAsString(filters);

        mockMvc.perform(MockMvcRequestBuilders.post("/project/getByFilters")
                        .header("x-user-id", "3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(projectDto.getName()));

        Mockito.verify(projectService).getProjectsWithFilters(filters);
    }

    @Test
    public void testGetAllProjects() {
        Mockito.when(projectService.getAllProjects()).thenReturn(List.of(projectDto));
        projectController.getAllProjects();
        Mockito.verify(projectService).getAllProjects();
    }

    @Test
    public void testGetProjectByIdWithWrongId() {
        Long idZero = 0L;
        Assertions.assertThrows(RuntimeException.class, () -> projectController.getProjectById(idZero));
        Assertions.assertThrows(RuntimeException.class, () -> projectController.getProjectById(null));
    }

    @Test
    public void testGetProjectById() throws Exception {
        Mockito.when(projectService.getProjectById(projectId)).thenReturn(projectDto);
        String jsonRequest = new ObjectMapper().writeValueAsString(projectDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/project/" + projectId)
                        .header("x-user-id", "3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(projectDto.getName()));

        Mockito.verify(projectService).getProjectById(projectId);
    }
}
