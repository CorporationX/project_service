package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.exeption.EntityNotFoundException;
import faang.school.projectservice.exeption.NotUniqueProjectException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.projectfilter.ProjectNameFilter;
import faang.school.projectservice.service.projectfilter.ProjectStatusFilter;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectValidator projectValidator;
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectStatusFilter projectStatusFilter;

    @Mock
    private ProjectNameFilter projectNameFilter;
    private ProjectUpdateDto updateDto;
    private ProjectDto projectDto;
    private Project project;

    @BeforeEach
    void setUp() {

        project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setDescription("Test Description");
        project.setOwnerId(1L);
        project.setStatus(ProjectStatus.CREATED);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        updateDto = ProjectUpdateDto.builder()
                .id(1L)
                .name("Test Project")
                .description("Update Description")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .build();
        ProjectFilterDto filterDto;
        filterDto = ProjectFilterDto.builder()
                .name("Test Project")
                .status(ProjectStatus.CREATED)
                .build();
    }

    @Test
    void testCreateProject() {
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        projectService.validateUniqueProject(project);
        Project createdProject = projectService.createProject(project);

        assertNotNull(createdProject);
        assertEquals("Test Project", createdProject.getName());
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void testUpdateProject() {
        Project updatedProject;
        updatedProject = Project.builder()
                .id(1L)
                .name("Test Project")
                .description("Updated Description")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PRIVATE)
                .build();
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Project result = projectService.updateProject(updatedProject);

        assertEquals("Updated Description", result.getDescription());
        assertEquals(ProjectStatus.CREATED, result.getStatus());
        assertEquals(ProjectVisibility.PRIVATE, result.getVisibility());
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void testFindProjectById_NotFound() {
        when(projectRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            projectService.findProjectById(1L);
        });

        assertEquals("Project not found", exception.getMessage());
    }

    @Test
    public void testGetProjectsByFilterName() {
        ProjectFilterDto filterDto;
        filterDto = ProjectFilterDto.builder()
                .name("Test Project")
                .status(ProjectStatus.CREATED)
                .build();

        project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setDescription("Test Description");
        project.setOwnerId(1L);
        when(projectRepository.findAll()).thenReturn(List.of(project));

        List<Project> filteredProjects = projectService.getProjectsByFilterName(filterDto, 1L);

        assertNotNull(filteredProjects);
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    public void testGetProjectsByFilterStatus() {
        ProjectFilterDto filterDto;
        filterDto = ProjectFilterDto.builder()
                .name("Test Project")
                .status(ProjectStatus.CREATED)
                .build();
        when(projectRepository.findAll()).thenReturn(List.of(project));

        var filteredProjects = projectService.getProjectsByFilterStatus(filterDto, 1L);

        assertNotNull(filteredProjects);
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllUserAvailableProjects() {
        when(projectRepository.findAll()).thenReturn(List.of(project));
        when(projectValidator.canUserAccessProject(any(Project.class), eq(1L))).thenReturn(true);

        var availableProjects = projectService.getAllUserAvailableProjects(1L);

        assertNotNull(availableProjects);
        assertEquals(1, availableProjects.size());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    void testValidateUniqueProjectThrowsExceptionWhenProjectExists() {
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setDescription("Test Description");
        project.setOwnerId(1L);

        when(projectRepository.existsByOwnerIdAndName(1L, "Test Project")).thenReturn(true);

        NotUniqueProjectException exception = assertThrows(NotUniqueProjectException.class, () -> {
            projectService.validateUniqueProject(project);
        });
        assertEquals("Project 'Test Project' with ownerId #1 already exists.", exception.getMessage());
        verify(projectRepository).existsByOwnerIdAndName(1L, "Test Project");
    }

    @Test
    void testValidateUniqueProjectSuccessWhenProjectIsUnique() {
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setDescription("Test Description");
        project.setOwnerId(1L);

        when(projectRepository.existsByOwnerIdAndName(1L, "Test Project")).thenReturn(false);

        assertDoesNotThrow(() -> projectService.validateUniqueProject(project));
        verify(projectRepository).existsByOwnerIdAndName(1L, "Test Project");
    }
}