package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.dto.ProjectUpDateDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.serviсe.ProjectService;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProjectControllerTests {
    @Mock
    private ProjectService projectService;
    @Spy
    private ProjectValidator projectValidator;
    @InjectMocks
    private ProjectController projectController;

    @Test
    void testCreateProject_ShouldCallServiceMethod() {
        var projectDto = ProjectDto.builder().name("test").description("test").build();
        projectController.createProject(projectDto);
        verify(projectService, times(1)).createProject(projectDto);
    }

    @Test
    void testUpdateProject_ShouldCallServiceMethod() {
        var projectUpDateDto = ProjectUpDateDto.builder().status(ProjectStatus.COMPLETED).build();
        projectController.updateProject(1L, projectUpDateDto);
        verify(projectService, times(1))
                .updateProject(1L, projectUpDateDto);
    }

    @Test
    void testGetAllProjectsWithFilter_ShouldCallServiceMethod() {
        var projectFilterDto = ProjectFilterDto.builder().name("Java").build();
        projectController.getAllProjectsWithFilter(projectFilterDto);
        verify(projectService, times(1)).getAllProjectsWithFilter(projectFilterDto);
    }

    @Test
    void testGetAllProjects_ShouldCallServiceMethod() {
        projectController.getAllProjects();
        verify(projectService, times(1)).getAllProjects();
    }

    @Test
    void testGetProjectById_ShouldCallServiceMethod() {
        projectController.getProjectById(1L);
        verify(projectService, times(1)).getProjectById(1L);
    }
}
