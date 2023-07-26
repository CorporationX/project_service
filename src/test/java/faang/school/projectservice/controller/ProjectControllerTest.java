package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

    private final ProjectMapper projectMapper = new ProjectMapperImpl();
    private final long userId = 1;
    private long projectId = 1;
    private ProjectDto projectDto;

    @BeforeEach
    public void initProjectDto() {
        projectDto = ProjectDto.builder()
                .ownerId(userId)
                .name("Project")
                .description("Cool")
                .build();
    }

    @Test
    public void shouldReturnProjectsList() {
        List<ProjectDto> desiredProjects = List.of(new ProjectDto());

        Mockito.when(projectService.getAllProjects())
                .thenReturn(desiredProjects);
        List<ProjectDto> receivedProject = projectController.getAllProjects();

        Assertions.assertEquals(desiredProjects, receivedProject);
        Mockito.verify(projectService).getAllProjects();
    }

    @Test
    public void shouldReturnProjectByProjectId() {
        ProjectDto desiredProject = new ProjectDto();

        Mockito.when(projectService.getProject(projectId))
                .thenReturn(desiredProject);
        ProjectDto receivedProject = projectController.getProject(projectId);

        Assertions.assertEquals(desiredProject, receivedProject);
        Mockito.verify(projectService).getProject(projectId);
    }

    @Test
    public void shouldReturnAndCreateNewProject() {
        ProjectDto notCreateProject = projectDto;

        Project desiredProject = projectMapper.toEntity(notCreateProject);
        desiredProject.setId(projectId);
        desiredProject.setStatus(ProjectStatus.CREATED);

        Mockito.when(projectService.createProject(notCreateProject))
                .thenReturn(projectMapper.toDto(desiredProject));
        ProjectDto receivedProject = projectController.createProject(notCreateProject);

        Assertions.assertEquals(projectMapper.toDto(desiredProject), receivedProject);

        Mockito.verify(projectService).createProject(notCreateProject);
    }

    @Test
    public void shouldReturnAndUpdateProject() {
        ProjectDto notUpdatedProject = projectDto;
        notUpdatedProject.setId(projectId);
        notUpdatedProject.setName("Mega project");

        Project desiredProject = projectMapper.toEntity(notUpdatedProject);

        Mockito.when(projectService.updateProject(notUpdatedProject))
                .thenReturn(projectMapper.toDto(desiredProject));
        ProjectDto receivedProject = projectController.updateProject(notUpdatedProject, projectId);

        Assertions.assertEquals(projectMapper.toDto(desiredProject), receivedProject);
        Mockito.verify(projectService).updateProject(notUpdatedProject);
    }

    @Test
    public void shouldThrowExceptionWhenProjectIdNotCorrect() {
        long otherProjectId = 2;

        ProjectDto notUpdatedProject = projectDto;
        notUpdatedProject.setId(otherProjectId);
        notUpdatedProject.setName("Mega project");

        Assertions.assertThrows(DataValidationException.class,
                () -> projectController.updateProject(notUpdatedProject, projectId));

        Mockito.verify(projectService, Mockito.times(0)).updateProject(notUpdatedProject);
    }
}
