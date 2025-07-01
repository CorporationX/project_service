package faang.school.projectservice.service.projectservice;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project project;
    private Project project1;
    private Project project2;

    private final Long PROJECT_ID = 1L;
    private final Long OWNER_ID = 2L;
    private final String PROJECT_NAME = "Test Project";

    @BeforeEach
    void setUp() {
        project = Project.builder()
                .id(PROJECT_ID)
                .name(PROJECT_NAME)
                .ownerId(OWNER_ID)
                .description("Test Description")
                .status(ProjectStatus.CREATED)
                .build();

        project1 = Project.builder()
                .id(10L)
                .name("Test Project 2")
                .ownerId(OWNER_ID)
                .description("Description 1")
                .status(ProjectStatus.CREATED)
                .build();

        project2 = Project.builder()
                .id(20L)
                .name("Another Project")
                .ownerId(OWNER_ID)
                .description("Description 2")
                .status(ProjectStatus.IN_PROGRESS)
                .build();
    }

    @Test
    void testCreateProject() {
        when(projectRepository.existsByOwnerIdAndName(OWNER_ID, PROJECT_NAME)).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        Project result = projectService.createProject(project);

        assertNotNull(result);
        assertEquals(ProjectStatus.CREATED, result.getStatus());
        verify(projectRepository).save(project);
    }

    @Test
    void createProjectThrowsWhenNameIsNull() {
        project = Project.builder()
                .id(PROJECT_ID)
                .name(null)
                .ownerId(OWNER_ID)
                .build();

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> projectService.createProject(project));
        assertEquals("Project name must not be empty.", ex.getMessage());

        verify(projectRepository, never()).existsByOwnerIdAndName(anyLong(), anyString());
        verify(projectRepository, never()).save(any());
    }

    @Test
    void testCreateProjectWithNameExist() {
        when(projectRepository.existsByOwnerIdAndName(OWNER_ID, PROJECT_NAME)).thenReturn(true);

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> projectService.createProject(project));
        assertEquals("Project with this name already exists for the user.", ex.getMessage());
    }

    @Test
    @Transactional
    void testUpdateProject() {
        Project updatedData = Project.builder()
                .description("Updated Description")
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        Project result = projectService.updateProject(PROJECT_ID, updatedData);

        assertEquals("Updated Description", result.getDescription());
        assertEquals(ProjectStatus.IN_PROGRESS, result.getStatus());
        verify(projectRepository).save(project);
    }

    @Test
    void testUpdateProjectNotFound() {
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.empty());

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> projectService.updateProject(PROJECT_ID, Project.builder().build()));
        assertEquals("Project not found.", ex.getMessage());
    }

    @Test
    void testGetAllProjects() {
        List<Project> projects = List.of(project);
        when(projectRepository.findAll()).thenReturn(projects);

        List<Project> result = projectService.getAllProjects();

        assertEquals(1, result.size());
        assertEquals(project, result.get(0));
        verify(projectRepository).findAll();
    }

    @Test
    void testGetProjectById() {
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));

        Project result = projectService.getProjectById(PROJECT_ID);

        assertEquals(project, result);
    }

    @Test
    void testGetNotFoundProjectById() {
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.empty());

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> projectService.getProjectById(PROJECT_ID));
        assertEquals("Project not found.", ex.getMessage());
    }


    @Test
    void testGetProjectFilterNameAndStatus() {
        when(projectRepository.findAll()).thenReturn(Arrays.asList(project, project1, project2));

        List<Project> result = projectService.getProjectFilter("Another Project", ProjectStatus.IN_PROGRESS);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(project2, result.get(0));

        verify(projectRepository, times(1)).findAll();
    }

    @Test
    void testGetProjectFilterNameOnly() {
        when(projectRepository.findAll()).thenReturn(Arrays.asList(project, project1, project2));

        List<Project> result = projectService.getProjectFilter("Test Project", null);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(project));

        verify(projectRepository, times(1)).findAll();
    }

    @Test
    void testGetProjectFilterStatusOnly() {
        when(projectRepository.findAll()).thenReturn(Arrays.asList(project, project1, project2));

        List<Project> result = projectService.getProjectFilter(null, ProjectStatus.IN_PROGRESS);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(project2));

        verify(projectRepository, times(1)).findAll();
    }

    @Test
    void testGetProjectFilterNoFilters() {
        when(projectRepository.findAll()).thenReturn(Arrays.asList(project, project1, project2));

        List<Project> result = projectService.getProjectFilter(null, null);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains(project));
        assertTrue(result.contains(project1));
        assertTrue(result.contains(project2));

        verify(projectRepository, times(1)).findAll();
    }

    @Test
    void testGetProjectFilterEmptyList() {
        when(projectRepository.findAll()).thenReturn(Collections.emptyList());

        List<Project> result = projectService.getProjectFilter("Alpha", ProjectStatus.CREATED);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(projectRepository, times(1)).findAll();
    }
}