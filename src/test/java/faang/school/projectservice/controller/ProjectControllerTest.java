package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ProjectControllerTest {
    @Mock
    private ProjectService projectService;
    @InjectMocks
    private ProjectController projectController;

    @Test
    public void shouldReturnProjectsList() {
        List<ProjectDto> desiredProjects = List.of(new ProjectDto());
        Mockito.when(projectService.getAllProjects()).thenReturn(desiredProjects);

        List<ProjectDto> receivedProject = projectController.getAllProjects();
        Assertions.assertEquals(desiredProjects, receivedProject);
    }
}
