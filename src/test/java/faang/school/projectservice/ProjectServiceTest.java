package faang.school.projectservice;

import faang.school.projectservice.adapter.ProjectRepositoryAdapter;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.excepcion.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectMapper projectMapper;
    @Mock
    private ProjectValidator projectValidator;
    @Mock
    private UserContext userContext;
    @Mock
    private ProjectRepositoryAdapter projectRepositoryAdapter;

    @InjectMocks
    private ProjectService projectService;

    private final long USER_ID = 1L;
    private final long PROJECT_ID = 1L;
    private ProjectDto projectDto;
    private Project project;

    @BeforeEach
    void setUp() {
        projectDto = ProjectDto.builder()
                .id(PROJECT_ID)
                .name("Test Project")
                .description("Test Description")
                .status(ProjectStatus.CREATED)
                .build();

        project = Project.builder()
                .id(PROJECT_ID)
                .name("Test Project")
                .description("Test Description")
                .status(ProjectStatus.CREATED)
                .ownerId(USER_ID)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    @Test
    void createProject_Success() {
        when(projectRepository.existsByOwnerIdAndName(USER_ID, projectDto.getName())).thenReturn(false);
        when(projectMapper.toProject(projectDto)).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDto);

        ProjectDto result = projectService.createProject(USER_ID, projectDto);

        assertNotNull(result);
        assertEquals(projectDto, result);
        verify(projectValidator).validate(projectDto);
        verify(projectRepository).save(project);
    }


    @Test
    void createProject_WhenProjectExists_ThrowsException() {
        when(projectRepository.existsByOwnerIdAndName(USER_ID, projectDto.getName())).thenReturn(true);

        assertThrows(DataValidationException.class,
                () -> projectService.createProject(USER_ID, projectDto));
    }

    @Test
    void updateProject_Success() {
        projectDto.setOwnerId(USER_ID);

        when(projectRepositoryAdapter.getProjectById(PROJECT_ID))
                .thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDto);

        ProjectDto result = projectService.updateProject(USER_ID, projectDto);

        assertNotNull(result);
        assertEquals(projectDto, result);
        verify(projectValidator).validate(projectDto);
        verify(projectRepositoryAdapter).getProjectById(PROJECT_ID);
    }

    @Test
    void updateProject_WhenNotOwner_ThrowsException() {
        long anotherUserId = 2L;
        projectDto.setOwnerId(anotherUserId);

        when(projectRepositoryAdapter.getProjectById(PROJECT_ID))
                .thenReturn(project);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> projectService.updateProject(USER_ID, projectDto));

        assertEquals("This project does not belong to this owner", exception.getMessage());
        verify(projectRepositoryAdapter).getProjectById(PROJECT_ID);
        verify(projectValidator).validate(projectDto);
        verifyNoMoreInteractions(projectRepository, projectMapper);
    }

    @Test
    void getProjectById_Success() {
        when(projectRepositoryAdapter.getProjectById(PROJECT_ID))
                .thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDto);

        ProjectDto result = projectService.getProjectById(PROJECT_ID);

        assertNotNull(result);
        assertEquals(projectDto, result);
    }

    @Test
    void getProjectsByName_Success() {
        String name = "Test Project";
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(projectRepository.findAll()).thenReturn(List.of(project));
        when(projectMapper.toDto(project)).thenReturn(projectDto);

        List<ProjectDto> result = projectService.getProjectsByName(name);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(projectDto, result.get(0));
    }

    @Test
    void getProjectsByStatus_Success() {
        ProjectStatus status = ProjectStatus.CREATED;
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(projectRepository.findAll()).thenReturn(List.of(project));
        when(projectMapper.toDto(project)).thenReturn(projectDto);

        List<ProjectDto> result = projectService.getProjectsByStatus(status);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(projectDto, result.get(0));
    }

    @Test
    void checkProjectExists_WhenProjectExists_DoesNotThrow() {
        when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);

        assertDoesNotThrow(() -> projectService.checkProjectExists(PROJECT_ID));
    }

    @Test
    void checkProjectExists_WhenProjectNotExists_ThrowsException() {
        when(projectRepository.existsById(PROJECT_ID)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> projectService.checkProjectExists(PROJECT_ID));
    }
}
