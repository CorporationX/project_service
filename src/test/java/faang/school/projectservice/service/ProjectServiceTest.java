package faang.school.projectservice.service;

import faang.school.projectservice.dto.internship.InternshipCreatedDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.filter.project.ProjectStatusFilter;
import faang.school.projectservice.mapper.project.ProjectMapperImpl;
import faang.school.projectservice.mapper.project.UpdateProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectValidator projectValidator;

    @Spy
    private ProjectMapperImpl projectMapper;

    @Spy
    private UpdateProjectMapperImpl updateProjectMapper;

    @Mock
    List<Filter<Project, ProjectFilterDto>> filters;

    @InjectMocks
    private ProjectService projectService;

    @Captor
    private ArgumentCaptor<Project> captor;

    private Project project;
    private ProjectDto projectDto;
    private UpdateProjectDto updateProjectDto;
    private UpdateProjectDto emptyUpdateProjectDto;
    private Project mockProject;
    private ProjectFilterDto filterDto;
    private Long projectId;
    private Long ownerId;

    @BeforeEach
    void setUp() {
        project = Project.builder()
                .name("Test project")
                .description("Test project description")
                .ownerId(1L)
                .status(null)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        projectDto = ProjectDto.builder()
                .name("Test project")
                .description("Test project description")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        updateProjectDto = UpdateProjectDto.builder()
                .name("Test project")
                .description("Updated test project description")
                .ownerId(1L)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        emptyUpdateProjectDto = UpdateProjectDto.builder()
                .name("Test project")
                .description(null)
                .ownerId(1L)
                .status(null)
                .visibility(null)
                .build();

        filterDto = ProjectFilterDto.builder().status(ProjectStatus.IN_PROGRESS).build();
        projectId = project.getId();
        ownerId = 1L;
        mockProject = mock(Project.class);
    }

    @Test
    void testGetByIdThrowsException() {
        when(projectRepository.getProjectById(projectId)).thenThrow(new EntityNotFoundException(
                String.format("Project not found by id: %s", projectId)));

        assertThrows(EntityNotFoundException.class, () -> projectService.getProjectById(projectId),
                String.format("Project not found by id: %s", projectId));
    }

    @Test
    void testGetByIdSuccessfully() {
        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        Project result = projectService.getProjectById(projectId);

        assertEquals(project.getName(), result.getName());
    }

    @Test
    void testCreateProjectSuccessful() {
        when(projectRepository.save(project)).thenReturn(project);
        project.setStatus(ProjectStatus.CREATED);

        projectService.createProject(projectDto);

        verify(projectRepository, times(1)).save(captor.capture());
        Project result = captor.getValue();
        assertEquals(result, project);
    }

    @Test
    void testUpdateProjectShouldNotUpdateIfValuesNull() {
        when(projectRepository.getProjectById(projectId)).thenReturn(mockProject);
        when(projectRepository.save(mockProject)).thenReturn(mockProject);

        projectService.updateProject(emptyUpdateProjectDto);

        verify(mockProject, times(0)).setDescription(updateProjectDto.getDescription());
        verify(mockProject, times(0)).setStatus(updateProjectDto.getStatus());
        verify(mockProject, times(0)).setVisibility(updateProjectDto.getVisibility());
        verify(projectRepository, times(1)).save(mockProject);
    }

    @Test
    @DisplayName("Get project by id success")
    void testGetProjectByIdSuccess() {
        when(projectRepository.getProjectById(project.getId())).thenReturn(project);

        Project result = projectService.getProjectById(project.getId());

        assertNotNull(result);
        assertEquals(project, result);
        assertEquals("Test project", result.getName());
    }

    @Test
    @DisplayName("Get project by id fail")
    void testGetProjectByIdFail() {
        project.setId(1L);
        when(projectRepository.getProjectById(project.getId())).
                thenThrow(new EntityNotFoundException(String.format("Project with id %s doesn't exist", project.getId())));

        Exception ex = assertThrows(EntityNotFoundException.class, () -> projectService.getProjectById(project.getId()));
        assertEquals("Project with id 1 doesn't exist", ex.getMessage());
    }

    @Test
    void testUpdateProjectSuccessful() {
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);

        UpdateProjectDto result = projectService.updateProject(updateProjectDto);

        verify(projectRepository, times(1)).save(project);
        assertEquals(result.getDescription(), updateProjectDto.getDescription());
        assertEquals(result.getStatus(), updateProjectDto.getStatus());
        assertEquals(result.getVisibility(), updateProjectDto.getVisibility());
    }

    @Test
    void testGetProjectsByFilterShouldFilterPrivateAndOwn() {
        List<Project> notFilteredProjects = getProjectsList();
        List<ProjectDto> filteredProjectDtos = getProjectDtosList();
        ProjectStatusFilter statusFilter = new ProjectStatusFilter();
        filters = List.of(statusFilter);
        projectValidator = new ProjectValidator(projectRepository);
        projectService = new ProjectService(projectRepository, projectValidator,
                projectMapper, updateProjectMapper, filters);
        when(projectRepository.findAll()).thenReturn(notFilteredProjects);

        List<ProjectDto> result = projectService.getProjectsByFilter(filterDto, ownerId);

        verify(projectRepository, times(1)).findAll();
        assertEquals(filteredProjectDtos, result);
    }

    @Test
    void testGetAllProjectsForUserSuccess() {
        List<Project> allProjects = getProjectsList();
        allProjects.get(2).setVisibility(ProjectVisibility.PRIVATE);
        List<ProjectDto> availableProjectDtos = getProjectDtosList();
        projectValidator = new ProjectValidator(projectRepository);
        projectService = new ProjectService(projectRepository, projectValidator,
                projectMapper, updateProjectMapper, filters);

        when(projectRepository.findAll()).thenReturn(allProjects);

        List<ProjectDto> result = projectService.getAllProjectsForUser(1L);

        assertEquals(availableProjectDtos, result);
    }

    @Test
    void testGetAccessibleProjectByIdSuccess() {
        project.setId(1L);
        project.setStatus(ProjectStatus.CREATED);
        projectDto.setId(1L);
        when(projectRepository.getProjectById(project.getId())).thenReturn(project);
        when(projectValidator.canUserAccessProject(project, ownerId)).thenReturn(true);

        ProjectDto result = projectService.getAccessibleProjectById(project.getId(), ownerId);

        assertEquals(projectDto, result);
    }

    @Test
    void testGetAccessibleProjectByIdShouldThrowException() {
        project.setId(1L);
        when(projectRepository.getProjectById(project.getId())).thenReturn(project);

        assertThrows(EntityNotFoundException.class, () ->
                projectService.getAccessibleProjectById(project.getId(), ownerId));
    }

    @Test
    public void testFindAllByIdWhenProjectsExist() {
        List<Long> ids = List.of(1L, 2L, 3L);
        Project project1 = new Project();
        project1.setId(1L);
        Project project2 = new Project();
        project2.setId(2L);
        Project project3 = new Project();
        project3.setId(3L);
        when(projectRepository.findAllByIds(ids)).thenReturn(List.of(project1, project2, project3));

        List<ProjectDto> result = projectService.findAllById(ids);

        assertEquals(3, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        assertEquals(3L, result.get(2).getId());
    }

    @Test
    public void testFindAllByIdWhenNoProjectsFound() {
        List<Long> ids = List.of(1L, 2L, 3L);
        when(projectRepository.findAllByIds(ids)).thenReturn(List.of());

        List<ProjectDto> result = projectService.findAllById(ids);
        assertEquals(0, result.size());
    }

    private List<Project> getProjectsList() {
        return List.of(
                Project.builder()
                        .ownerId(1L)
                        .visibility(ProjectVisibility.PRIVATE)
                        .status(ProjectStatus.IN_PROGRESS)
                        .build(),
                Project.builder()
                        .ownerId(2L)
                        .visibility(ProjectVisibility.PUBLIC)
                        .status(ProjectStatus.IN_PROGRESS)
                        .build(),
                Project.builder()
                        .ownerId(2L)
                        .visibility(ProjectVisibility.PUBLIC)
                        .status(ProjectStatus.COMPLETED)
                        .build(),
                Project.builder()
                        .ownerId(2L)
                        .visibility(ProjectVisibility.PRIVATE)
                        .status(ProjectStatus.IN_PROGRESS)
                        .build()
        );
    }

    private List<ProjectDto> getProjectDtosList() {
        return List.of(
                ProjectDto.builder()
                        .ownerId(1L)
                        .visibility(ProjectVisibility.PRIVATE)
                        .status(ProjectStatus.IN_PROGRESS)
                        .build(),
                ProjectDto.builder()
                        .ownerId(2L)
                        .visibility(ProjectVisibility.PUBLIC)
                        .status(ProjectStatus.IN_PROGRESS)
                        .build()
        );
    }

    @Test
    void findByIdWhenProjectExistsShouldReturnProjectDto() {
        when(projectRepository.getProjectById(1L)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDto);

        ProjectDto result = projectService.findById(1L);

        assertNotNull(result);
        assertEquals(projectDto.getId(), result.getId());
        assertEquals(projectDto.getName(), result.getName());

        verify(projectRepository).getProjectById(1L);
        verify(projectMapper).toDto(project);
    }

    @Test
    void findByIdWhenProjectDoesNotExistShouldThrowException() {
        when(projectRepository.getProjectById(1L)).thenThrow(new EntityNotFoundException("Project not found"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            projectService.findById(1L);
        });

        assertEquals("Project not found", exception.getMessage());

        verify(projectRepository).getProjectById(1L);
    }

    @Test
    void findAllById_whenProjectsExist_shouldReturnProjectDtos() {
        List<Long> ids = List.of(1L, 2L);

        when(projectRepository.findAllByIds(ids)).thenReturn(List.of(project));
        when(projectMapper.toDto(project)).thenReturn(projectDto);

        List<ProjectDto> result = projectService.findAllById(ids);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(projectDto.getId(), result.get(0).getId());
        assertEquals(projectDto.getName(), result.get(0).getName());

        verify(projectRepository).findAllByIds(ids);
        verify(projectMapper).toDto(project);
    }

    @Test
    void findAllById_whenNoProjectsExist_shouldReturnEmptyList() {
        List<Long> ids = List.of(1L, 2L);

        when(projectRepository.findAllByIds(ids)).thenReturn(List.of());

        List<ProjectDto> result = projectService.findAllById(ids);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(projectRepository).findAllByIds(ids);
    }
}