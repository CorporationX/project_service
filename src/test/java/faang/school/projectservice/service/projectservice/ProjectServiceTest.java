package faang.school.projectservice.service.projectservice;

import static org.junit.jupiter.api.Assertions.*;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project project;
    private final Long PROJECT_ID = 1L;
    private final Long OWNER_ID = 2L;
    private final String PROJECT_NAME = "Test Project";

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(PROJECT_ID);
        project.setName(PROJECT_NAME);
        project.setOwnerId(OWNER_ID);
        project.setDescription("Test Description");
        project.setStatus(ProjectStatus.CREATED);
    }

    @Test
    void createProjectTest() {
        when(projectRepository.existsByOwnerIdAndName(OWNER_ID, PROJECT_NAME)).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        Project result = projectService.createProject(project);

        assertNotNull(result);
        assertEquals(ProjectStatus.CREATED, result.getStatus());
        verify(projectRepository).save(project);
    }

    @Test
    void createProjectWithNameExistTest() {
        when(projectRepository.existsByOwnerIdAndName(OWNER_ID, PROJECT_NAME)).thenReturn(true);

        assertThrows(DataValidationException.class,
                () -> projectService.createProject(project),
                "Project with this name already exists for the user.");
    }

    @Test
    @Transactional
    void updateProjectTest() {
        Project updatedData = new Project();
        updatedData.setDescription("Updated Description");
        updatedData.setStatus(ProjectStatus.IN_PROGRESS);

        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        Project result = projectService.updateProject(PROJECT_ID, updatedData);

        assertEquals("Updated Description", result.getDescription());
        assertEquals(ProjectStatus.IN_PROGRESS, result.getStatus());
        verify(projectRepository).save(project);
    }

    @Test
    void updateProjectNotFoundTest() {
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class,
                () -> projectService.updateProject(PROJECT_ID, new Project()),
                "Project not found.");
    }

    @Test
    void getAllProjects_ShouldReturnAllProjects() {
        List<Project> projects = List.of(project);
        when(projectRepository.findAll()).thenReturn(projects);

        List<Project> result = projectService.getAllProjects();

        assertEquals(1, result.size());
        assertEquals(project, result.get(0));
        verify(projectRepository).findAll();
    }

    @Test
    void getProjectByIdTest() {
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));

        Project result = projectService.getProjectById(PROJECT_ID);

        assertEquals(project, result);
    }

    @Test
    void getNotFoundProjectByIdTest() {
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class,
                () -> projectService.getProjectById(PROJECT_ID),
                "Project not found.");
    }
}