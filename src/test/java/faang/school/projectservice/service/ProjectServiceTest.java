package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.filter.project.ProjectNameFilter;
import faang.school.projectservice.filter.project.ProjectStatusFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    private final long userId = 1L;
    private final long projectId = 1L;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectMapper projectMapper;
    @InjectMocks
    private ProjectService projectService;
    private Project desiredProject;
    private ProjectDto desiredProjectDto;

    static Stream<ProjectFilter> argsProvider1() {
        return Stream.of(
                new ProjectNameFilter(),
                new ProjectStatusFilter()
        );
    }

    static Stream<Arguments> argsProvider2() {
        return Stream.of(
                Arguments.of(new ProjectNameFilter(), ProjectFilterDto.builder().namePattern("r").build()),
                Arguments.of(new ProjectStatusFilter(), ProjectFilterDto.builder().statuses(List.of(ProjectStatus.COMPLETED)).build())
        );
    }

    @BeforeEach
    public void initProject() {
        desiredProject = Project.builder()
                .id(1L)
                .name("Project")
                .description("Cool")
                .status(ProjectStatus.CREATED)
                .build();
        desiredProjectDto = ProjectDto.builder()
                .id(1L)
                .name("Project")
                .description("Cool")
                .status(ProjectStatus.CREATED)
                .build();
    }

    @Test
    public void returnProjectsListTest() {
        List<Project> desiredProjects = List.of(Project.builder().visibility(ProjectVisibility.PUBLIC).build());
        Mockito.when(projectRepository.findAll()).thenReturn(desiredProjects.stream());
        Mockito.when(projectMapper.toDtoList(desiredProjects))
                .thenReturn(List.of(ProjectDto.builder().visibility(ProjectVisibility.PUBLIC).build()));
        List<ProjectDto> receivedProject = projectService.getAllProjects(1L);
        Assertions.assertEquals(projectMapper.toDtoList(desiredProjects), receivedProject);
    }

    @Test
    public void returnProjectByProjectIdTest() {
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
    public void throwExceptionWhenProjectNotExistsTest() {
        Mockito.when(projectRepository.existsById(projectId))
                .thenReturn(false);
        Assertions.assertThrows(DataValidException.class, () -> projectService.getProject(projectId));
        Mockito.verify(projectRepository, Mockito.times(0)).getProjectById(projectId);
    }

    @Test
    public void createdAndReturnNewProjectTest() {
        Mockito.when(projectRepository.existsByOwnerUserIdAndName(userId, "Project"))
                .thenReturn(false);

        Mockito.lenient().when(projectRepository.save(desiredProject))
                .thenReturn(desiredProject);

        Mockito.when(projectMapper.toEntity(desiredProjectDto))
                .thenReturn(desiredProject);

        Mockito.when(projectMapper.toDto(desiredProject))
                .thenReturn(desiredProjectDto);

        ProjectDto receivedProject = projectService.createProject(desiredProjectDto);

        Assertions.assertEquals(desiredProjectDto, receivedProject);
        Mockito.verify(projectRepository).save(desiredProject);
    }

    @Test
    public void throwExceptionWhenProjectExistsTest() {
        Mockito.when(projectRepository.existsByOwnerUserIdAndName(desiredProjectDto.getId(), desiredProjectDto.getName()))
                .thenReturn(true);

        Assertions.assertThrows(DataValidException.class, () -> projectService.createProject(desiredProjectDto));
        Mockito.verify(projectRepository, Mockito.times(0)).save(desiredProject);
    }

    @Test
    public void throwExceptionWhenOptionalIsEmptyTest() {
        Mockito.when(projectRepository.findById(projectId))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> projectService.updateProject(desiredProjectDto, projectId));
    }

    @Test
    public void returnAndUpdateProjectTest() {
        ProjectDto updatedProjectDto = ProjectDto.builder()
                .id(1L)
                .name("Mega project")
                .description("Cool")
                .status(ProjectStatus.CREATED)
                .build();
        Project updatedProject = Project.builder()
                .id(1L)
                .name("Mega project")
                .description("Cool")
                .status(ProjectStatus.CREATED)
                .build();

        Optional<Project> returnedOptional = Optional.of(desiredProject);

        Mockito.when(projectRepository.findById(projectId))
                .thenReturn(returnedOptional);
        Mockito.doNothing().when(projectMapper).updateFromDto(Mockito.any(), Mockito.any());
        Mockito.when(projectMapper.toDto(Mockito.any())).thenReturn(updatedProjectDto);

        Mockito.when(projectRepository.save(desiredProject))
                .thenReturn(updatedProject);

        ProjectDto result = projectService.updateProject(updatedProjectDto, projectId);

        Assertions.assertEquals(updatedProjectDto, result);
        Mockito.verify(projectRepository, Mockito.times(1)).save(desiredProject);
        Mockito.verify(projectRepository, Mockito.times(1)).findById(projectId);
    }

    @ParameterizedTest
    @MethodSource("argsProvider1")
    public void testIsApplicable(ProjectFilter userFilter) {
        ProjectFilterDto filter = new ProjectFilterDto("f", List.of(ProjectStatus.COMPLETED));
        ProjectFilterDto filter2 = new ProjectFilterDto();

        boolean result1 = userFilter.isApplicable(filter);
        boolean result2 = userFilter.isApplicable(filter2);

        assertTrue(result1);
        assertFalse(result2);
    }

    @ParameterizedTest
    @MethodSource("argsProvider2")
    public void testApply(ProjectFilter projectFilter, ProjectFilterDto filter) {
        Project project1 = Project.builder().name("Args").status(ProjectStatus.COMPLETED).build();
        Project project2 = Project.builder().name("no").status(ProjectStatus.CANCELLED).build();

        List<Project> result = projectFilter.apply(Stream.of(project1, project2), filter).collect(Collectors.toList());

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(project1, result.get(0))
        );
    }
}