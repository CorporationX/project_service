package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.client.project.ProjectCreateDto;
import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.dto.client.project.ProjectUpdateDto;
import faang.school.projectservice.exception.project.DuplicateResourceException;
import faang.school.projectservice.helpers.TestUtils;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @InjectMocks
    private ProjectService projectService;

    private static final Long ID = 1L;
    private static final Long OWNER_ID = 10L;
    private Project project;

    @BeforeEach
    void setUp() {
        project = Project.builder()
                .id(ID)
                .name("TestProject")
                .description("Description")
                .ownerId(OWNER_ID)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createProject_Success() {
        ProjectCreateDto dto = new ProjectCreateDto("NewProject",
                "Some description",
                ProjectVisibility.PUBLIC);

        when(projectRepository.existsByOwnerIdAndName(OWNER_ID, "NewProject")).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> {
            Project saved = invocation.getArgument(0);
            saved.setId(ID);
            return saved;
        });

        Project result = projectService.create(dto, OWNER_ID);

        assertNotNull(result);
        assertEquals("NewProject", result.getName());
        assertEquals(ProjectStatus.CREATED, result.getStatus());
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void createProject_NameAlreadyExists_ThrowsException() {
        ProjectCreateDto dto = new ProjectCreateDto("DuplicateProject",
                "Desc",
                ProjectVisibility.PUBLIC);
        when(projectRepository.existsByOwnerIdAndName(OWNER_ID, "DuplicateProject")).thenReturn(true);

        Executable executable = () -> projectService.create(dto, OWNER_ID);

        TestUtils.assertThrowsWithMessage(
                DuplicateResourceException.class,
                "Project with this name already exists for this user",
                executable
        );
    }

    @Test
    void updateProject_Success() {
        ProjectUpdateDto dto = new ProjectUpdateDto("Updated description",
                "COMPLETED",
                ProjectVisibility.PRIVATE);
        when(projectRepository.getReferenceById(ID)).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        Project updated = projectService.update(ID, dto);

        assertEquals("Updated description", updated.getDescription());
        assertEquals(ProjectStatus.COMPLETED, updated.getStatus());
        verify(projectRepository).save(project);
    }

    @Test
    void updateProject_InvalidStatus_ThrowsException() {
        ProjectUpdateDto dto = new ProjectUpdateDto("Updated description",
                "INVALID",
                ProjectVisibility.PUBLIC);
        when(projectRepository.getReferenceById(ID)).thenReturn(project);

        Executable executable = () -> projectService.update(ID, dto);

        TestUtils.assertThrowsWithMessage(
                IllegalArgumentException.class,
                "No enum constant faang.school.projectservice.model.ProjectStatus.INVALID",
                executable
        );
    }

    @Test
    void getAllProjects_ReturnsList() {
        when(projectRepository.findAll()).thenReturn(List.of(project));

        List<ProjectDto> result = projectService.getAllProjects();

        assertEquals(1, result.size());
        assertEquals("TestProject", result.get(0).name());
        verify(projectRepository).findAll();
    }

    @Test
    void getProjectsByFilter_FiltersByNameAndStatus() {
        Project project2 = Project.builder()
                .id(2L)
                .name("Another")
                .status(ProjectStatus.COMPLETED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        when(projectRepository.findAll()).thenReturn(List.of(project, project2));

        List<ProjectDto> result = projectService.getProjectsByFilter("Test", ProjectStatus.CREATED, OWNER_ID);

        assertEquals(1, result.size());
        assertEquals("TestProject", result.get(0).name());
    }

    @Test
    void getProjectById_Success() {
        when(projectRepository.getReferenceById(ID)).thenReturn(project);

        Project result = projectService.getProjectById(ID, OWNER_ID);

        assertEquals(project, result);
    }
}