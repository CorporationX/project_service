package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.project.filter.FilterProject;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private List<FilterProject> filters;

    @InjectMocks
    private ProjectService projectService;

    private Project projectFirst;
    private Project projectSecond;
    private ProjectUpdateDto projectUpdateDto;
    private Long userId;
    private Long projectId;

    @BeforeEach
    public void setUp() {
        userId = 1L;
        projectId = 1L;

        projectFirst = Project.builder()
                .id(1L)
                .name("Test Project")
                .description("Test Description")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        projectSecond = Project.builder()
                .id(2L)
                .name("Test Project2")
                .description("Test Description2")
                .ownerId(2L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        projectUpdateDto = new ProjectUpdateDto("Updated Name", "Updated Description",
                ProjectStatus.IN_PROGRESS, ProjectVisibility.PRIVATE);
    }

    @Test
    void createProject_WithValidData_ShouldCreateProject() {
        ProjectCreateDto projectCreateDto = new ProjectCreateDto("Test Project",
                "Test Description", ProjectVisibility.PUBLIC);
        Project expectedProject = Project.builder()
                .id(1L)
                .name("Test Project")
                .description("Test Description")
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .ownerId(userId)
                .build();
        when(userContext.getUserId()).thenReturn(userId);
        when(projectRepository.existsByOwnerIdAndName(userId, projectCreateDto.name())).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenReturn(expectedProject);

        Project result = projectService.createProject(projectCreateDto);

        assertNotNull(result);
        assertEquals(projectCreateDto.name(), result.getName());
        assertEquals(projectCreateDto.description(), result.getDescription());
        assertEquals(userId, result.getOwnerId());
        assertEquals(ProjectStatus.CREATED, result.getStatus());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void updateProject_WithValidData_ShouldUpdateProject() {
        when(userContext.getUserId()).thenReturn(userId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectFirst));
        when(projectRepository.save(any(Project.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        Long originalId = projectFirst.getId();
        Long originalOwnerId = projectFirst.getOwnerId();

        Project result = projectService.updateProject(projectId, projectUpdateDto);

        assertNotNull(result);
        verify(projectRepository).save(projectFirst);
        assertEquals(projectUpdateDto.name(), result.getName());
        assertEquals(projectUpdateDto.description(), result.getDescription());
        assertEquals(projectUpdateDto.status(), result.getStatus());
        assertEquals(projectUpdateDto.visibility(), result.getVisibility());

        assertEquals(projectFirst.getId(), originalId);
        assertEquals(projectFirst.getOwnerId(), originalOwnerId);
    }

    @Test
    void updateProject_WhenUserIsNotOwner_ShouldThrowException() {
        Long currentUserId = 2L;
        when(userContext.getUserId()).thenReturn(currentUserId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectFirst));

        assertThrows(DataValidationException.class,
                () -> projectService.updateProject(projectId, projectUpdateDto));
    }

    @Test
    void updateProject_WhenProjectNotFound_ShouldThrowException() {
        projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> projectService.updateProject(projectId, projectUpdateDto));
    }

    @Test
    void getProjectById_WhenProjectExists_ShouldReturnProject() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectFirst));

        Project result = projectService.getProjectById(projectId);

        assertNotNull(result);
        assertEquals(projectFirst, result);
    }

    @Test
    void getProjectById_WhenProjectNotExists_ShouldThrowException() {
        projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> projectService.getProjectById(projectId));
    }

    @Test
    void deleteProject_WhenUserHasAccess_ShouldDeleteProject() {
        when(userContext.getUserId()).thenReturn(userId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.ofNullable(projectFirst));

        projectService.deleteProject(projectId);

        verify(projectRepository).delete(projectFirst);
    }

    @Test
    void deleteProject_WhenProjectNotAccessible_ShouldThrowException() {
        projectId = 2L;
        when(userContext.getUserId()).thenReturn(userId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.ofNullable(projectSecond));


        assertThrows(DataValidationException.class,
                () -> projectService.deleteProject(projectId));
    }

    @Test
    void filterProjectsByAccess_ShouldReturnOnlyAccessibleProjects() {
        Project otherPrivateProject = Project.builder()
                .id(3L)
                .name("Test Project")
                .description("Test Description")
                .ownerId(2L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PRIVATE)
                .build();
        ProjectFilterDto projectFilterDto = ProjectFilterDto.builder()
                .name(null)
                .status(null)
                .build();

        List<Project> allProjects = List.of(projectFirst, projectSecond, otherPrivateProject);

        projectSecond.setVisibility(ProjectVisibility.PRIVATE);
        projectSecond.setOwnerId(2L);

        when(userContext.getUserId()).thenReturn(userId);
        when(projectRepository.findAll()).thenReturn(allProjects);
        when(filters.iterator()).thenReturn(Collections.emptyIterator());

        List<Project> result = projectService.getProjectsByFilter(projectFilterDto);

        assertEquals(List.of(projectFirst), result);
    }
}