package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ProjectControllerTest — описание класса.
 * <p>
 * Тесты методов контроллера
 * </p>*
 *
 * @author fuckmynameagain
 * @since 19.08.2025
 */
public class ProjectControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateProject() {
        ProjectService projectService = mock(ProjectService.class);

        ProjectController controller = new ProjectController(projectService);

        ProjectDto inputDto = new ProjectDto();
        inputDto.setName("Test project");

        ProjectDto outputDto = new ProjectDto();
        outputDto.setName("Test project");

        when(projectService.createProject(inputDto)).thenReturn(outputDto);

        ProjectDto result = controller.createProject(inputDto).getBody();

        assertNotNull(result);
        assertEquals("Test project", result.getName());
    }

    @Test
    void testUpdateProject() {
        ProjectService projectService = mock(ProjectService.class);
        ProjectController controller = new ProjectController(projectService);

        ProjectDto inputDto = new ProjectDto();
        inputDto.setDescription("Old description");

        ProjectDto updatedProject = new ProjectDto();
        updatedProject.setDescription("New description");

        when(projectService.updateProject(1L, inputDto)).thenReturn(updatedProject);

        ProjectDto result = controller.updateProject(1L, inputDto).getBody();

        assertNotNull(result);
        assertEquals("New description", result.getDescription());
    }

    @Test
    void testGetProjectsByFilter() {
        ProjectService projectService = mock(ProjectService.class);
        ProjectController controller = new ProjectController(projectService);

        ProjectDto filter = new ProjectDto();
        filter.setName("Project");

        ProjectDto expectedProject = new ProjectDto();
        expectedProject.setName("Project");

        when(projectService.getProjectsByFilter(filter)).thenReturn(List.of(expectedProject));

        List<ProjectDto> result = controller.getProjectsByFilter(filter).getBody();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Project", result.get(0).getName());
    }

    @Test
    void testGetAllProjects() {
        ProjectService projectService = mock(ProjectService.class);
        ProjectController controller = new ProjectController(projectService);

        ProjectDto project1 = new ProjectDto();
        ProjectDto project2 = new ProjectDto();
        ProjectDto project3 = new ProjectDto();

        project1.setName("firstProject");
        project2.setName("secondProject");
        project3.setName("thirdProject");

        when(projectService.getAllProjects()).thenReturn(List.of(project1, project2, project3));

        List<ProjectDto> result = controller.getAllProjects().getBody();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("firstProject", result.get(0).getName());
        assertEquals("secondProject", result.get(1).getName());
        assertEquals("thirdProject", result.get(2).getName());
    }

    @Test
    void testGetProjectById() {
        ProjectService projectService = mock(ProjectService.class);
        ProjectController controller = new ProjectController(projectService);

        ProjectDto project = new ProjectDto();
        project.setName("Project1");

        when(projectService.getProjectById(5L)).thenReturn(project);

        ProjectDto result = controller.getProjectById(5L).getBody();

        assertNotNull(result);
        assertEquals("Project1", result.getName());
    }

    @Test
    void testDeleteProject() {
        ProjectService projectService = mock(ProjectService.class);
        ProjectController controller = new ProjectController(projectService);

        ResponseEntity<Void> deleteResult = controller.deleteProject(1L);

        verify(projectService, times(1)).deleteProject(1L);

        assertEquals(HttpStatus.NO_CONTENT, deleteResult.getStatusCode());
    }
}