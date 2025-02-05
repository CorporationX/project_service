package school.faang.project_service.controller;

import faang.school.projectservice.controller.ProjectController;
import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    @Test
    void getProject_WhenProjectExists_ReturnsResponseEntityWithProjectDto() {
        Long projectId = 1L;
        ProjectDto expectedProjectDto = new ProjectDto(projectId, "Test Project");

        when(projectService.getProject(projectId)).thenReturn(ResponseEntity.ok(expectedProjectDto));

        ResponseEntity<ProjectDto> response = projectController.getProject(projectId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedProjectDto, response.getBody());
    }

    @Test
    void getProject_WhenProjectDoesNotExist_ReturnsNotFound() {
        Long projectId = 999L;

        when(projectService.getProject(projectId)).thenReturn(ResponseEntity.notFound().build());

        ResponseEntity<ProjectDto> response = projectController.getProject(projectId);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}