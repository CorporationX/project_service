package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @Spy
    private ProjectMapper projectMapper = new ProjectMapperImpl();
    @InjectMocks
    private ProjectService projectService;

    private final long projectId = 1;

    @Test
    public void shouldReturnProjectsList() {
        List<Project> desiredProjects = List.of(new Project());
        Mockito.when(projectRepository.findAll()).thenReturn(desiredProjects);

        List<ProjectDto> receivedProject = projectService.getAllProjects();
        Assertions.assertEquals(projectMapper.toDtoList(desiredProjects), receivedProject);
    }

    @Test
    public void shouldReturnProjectByProjectId() {
        Project desiredProject = new Project();

        Mockito.when(projectRepository.existsById(projectId))
                .thenReturn(true);
        Mockito.when(projectRepository.getProjectById(projectId))
                .thenReturn(desiredProject);

        ProjectDto receivedProject = projectService.getProject(projectId);

        Assertions.assertEquals(projectMapper.toDto(desiredProject), receivedProject);
        Mockito.verify(projectRepository).getProjectById(projectId);
    }

    @Test
    public void shouldThrowExceptionWhenProjectNotExists() {
        Mockito.when(projectRepository.existsById(projectId))
                .thenReturn(false);

        Assertions.assertThrows(DataValidationException.class, () -> projectService.getProject(projectId));
        Mockito.verify(projectRepository, Mockito.times(0)).getProjectById(projectId);
    }
}
