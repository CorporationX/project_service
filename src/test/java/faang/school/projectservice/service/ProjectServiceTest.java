package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Assertions;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @Spy
    private ProjectMapper projectMapper = new ProjectMapperImpl();
    @InjectMocks
    private ProjectService projectService;

    private ProjectDto projectDto;
    private final long userId = 1;
    private final long projectId = 1;

    @BeforeEach
    public void initProject() {
        projectDto = ProjectDto.builder()
                .id(projectId)
                .name("Project")
                .description("Cool")
                .ownerId(userId)
                .status(ProjectStatus.CREATED)
                .build();
    }

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

    @Test
    public void shouldCreatedAndReturnNewProject() {
        Mockito.when(projectRepository.existsByOwnerUserIdAndName(userId, "Project"))
                .thenReturn(false);

        Project desiredProject = projectMapper.toEntity(projectDto);

        Mockito.lenient().when(projectRepository.save(desiredProject))
                .thenReturn(desiredProject);

        ProjectDto receivedProject = projectService.createProject(projectDto);

        Assertions.assertEquals(projectMapper.toDto(desiredProject), receivedProject);
        Mockito.verify(projectRepository).save(projectMapper.toEntity(projectDto));
    }

    @Test
    public void shouldThrowExceptionWhenProjectExists() {
        ProjectDto testProjectDto = projectDto;

        Mockito.when(projectRepository.existsByOwnerUserIdAndName(testProjectDto.getId(), testProjectDto.getName()))
                .thenReturn(true);

        Assertions.assertThrows(DataValidationException.class, () -> projectService.createProject(testProjectDto));
        Mockito.verify(projectRepository, Mockito.times(0))
                .save(projectMapper.toEntity(projectDto));
    }

    @Test
    public void shouldReturnAndUpdateProject() {
        Project newProject = projectMapper.toEntity(projectDto);
        newProject.setName("Mega project");

        Project updatedProject = projectMapper.toEntity(projectDto);
        projectMapper.updateFromDto(projectMapper.toDto(newProject), updatedProject);

        Optional<Project> returnedOptional = Optional.of(projectMapper.toEntity(projectDto));

        Mockito.when(projectRepository.findById(projectId))
                .thenReturn(returnedOptional);
        Mockito.when(projectRepository.save(updatedProject))
                .thenReturn(updatedProject);

        ProjectDto receivedProject = projectService.updateProject(projectMapper.toDto(newProject));

        Assertions.assertEquals(projectMapper.toDto(returnedOptional.get()), receivedProject);
    }

    @Test
    public void shouldThrowExceptionWhenOptionalIsEmpty() {
        Mockito.when(projectRepository.findById(projectId))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(ProjectNotFoundException.class, () -> projectService.updateProject(projectDto));
    }
}
