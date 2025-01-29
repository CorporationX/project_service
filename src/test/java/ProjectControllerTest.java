import faang.school.projectservice.controller.ProjectController;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exceptionhandler.GlobalExceptionHandler;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProjectControllerTest {
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @InjectMocks
    private ProjectController projectController;

    @Mock
    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(projectController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void testSuccessCreateProject() throws Exception {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setName("Chairs hand made");
        projectDto.setDescription("Some description");
        projectDto.setStatus(ProjectStatus.CREATED);

        ArgumentCaptor<ProjectDto> captor = ArgumentCaptor.forClass(ProjectDto.class);

        Mockito.when(projectService.createProject(Mockito.any(ProjectDto.class))).thenReturn(projectDto);

        mockMvc.perform(post("/api/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Chairs hand made"))
                .andExpect(jsonPath("$.description").value("Some description"))
                .andExpect(jsonPath("$.status").value("CREATED"));

        Mockito.verify(projectService, times(1)).createProject(captor.capture());

        ProjectDto result = captor.getValue();
        assertEquals("Chairs hand made", result.getName());
        assertEquals("Some description", result.getDescription());
        assertEquals(ProjectStatus.CREATED, result.getStatus());
    }

    @Test
    void testCreateProjectWithoutName() throws Exception {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setName(" ");
        projectDto.setDescription("Some description");
        projectDto.setStatus(ProjectStatus.CREATED);

        mockMvc.perform(post("/api/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("name must be fielded"));
    }

    @Test
    void testCreateProjectWithoutDescription() throws Exception {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setName("Project name");
        projectDto.setDescription(" ");
        projectDto.setStatus(ProjectStatus.CREATED);

        mockMvc.perform(post("/api/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("description must be fielded"));
    }

    @Test
    void testUpdateProjectSuccess() throws Exception {
        ProjectDto updatedProjectDto = new ProjectDto();
        updatedProjectDto.setName("Project name");
        updatedProjectDto.setDescription("Project description");
        updatedProjectDto.setStatus(ProjectStatus.ON_HOLD);

        Mockito.when(projectService.updatedProject(Mockito.any(ProjectDto.class))).thenReturn(updatedProjectDto);

        mockMvc.perform(put("/api/project/updated")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProjectDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Project name"))
                .andExpect(jsonPath("$.description").value("Project description"))
                .andExpect(jsonPath("$.status").value("ON_HOLD"));

        Mockito.verify(projectService, times(1)).updatedProject(updatedProjectDto);
    }

    @Test
    void getProjectsWithFilter() throws Exception {
        Long userId = 10L;
        ProjectFilterDto nameFilter = new ProjectFilterDto();
        nameFilter.setNamePattern("chairs");

        ProjectDto dto = new ProjectDto();
        dto.setName("Project name");
        dto.setDescription("Some description");

        Mockito.when(projectService.getAllAvailableProjectsForUserWithFilter(nameFilter, userId)).thenReturn(List.of(dto));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/project/filter/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nameFilter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Project name"))
                .andExpect(jsonPath("$[0].description").value("Some description"));

        Mockito.verify(projectService, times(1)).getAllAvailableProjectsForUserWithFilter(nameFilter, userId);
    }

    @Test
    void testGetAllAvailableProjectsForUser() throws Exception {
        Long userId = 1L;

        ProjectDto dto = new ProjectDto();
        dto.setName("Project name");
        dto.setDescription("Some description");

        Mockito.when(projectService.getAllAvailableProjectsForUser(userId)).thenReturn(List.of(dto));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/project/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Project name"))
                .andExpect(jsonPath("$[0].description").value("Some description"));

        Mockito.verify(projectService, times(1)).getAllAvailableProjectsForUser(userId);
    }

    @Test
    void testGetProjectById() throws Exception {
        Long projectId = 20L;

        ProjectDto dto = new ProjectDto();
        dto.setId(projectId);
        dto.setName("Project name");

        Mockito.when(projectService.getProjectById(projectId)).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/project/20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.name").value("Project name"));
    }
}
