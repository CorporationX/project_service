package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.ValidationException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validation.ProjectValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectValidator projectValidator;
    @Spy
    private ProjectMapper projectMapper;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private List<ProjectFilter> filters;

    @InjectMocks
    private ProjectService projectService;

    Long userId = 1L;
    Project project;
    ProjectDto projectDto;
    ProjectFilterDto projectFilterDto;

    @BeforeEach
    void setUp() {
        project = Project.builder()
                .name("test name")
                .description("test description")
                .build();
        projectDto = ProjectDto.builder()
                .name("test name")
                .description("test description")
                .build();
        projectFilterDto = ProjectFilterDto.builder()
                .build();
    }

    @Test
    void createWithProjectNameExistsShouldTrowsException() {
        doThrow(ValidationException.class).when(projectValidator).validateCreation(userId, projectDto);
        assertThrows(ValidationException.class, () -> projectService.create(userId, projectDto));
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void createWithValidParametersShouldMapToEntity() {
        when(projectMapper.toEntity(projectDto)).thenReturn(project);
        projectService.create(userId, projectDto);
        verify(projectMapper).toEntity(projectDto);
    }

    @Test
    void createWithValidParametersShouldSetOwnerId() {
        when(projectMapper.toEntity(projectDto)).thenReturn(project);
        projectService.create(userId, projectDto);
        Assertions.assertEquals(1L, project.getOwnerId());
    }

    @Test
    void createWithValidParametersShouldSetStatus() {
        when(projectMapper.toEntity(projectDto)).thenReturn(project);
        projectService.create(userId, projectDto);
        Assertions.assertEquals(ProjectStatus.CREATED, project.getStatus());
    }

    @Test
    void createWithValidParametersAndVisibilityNotExistsShouldSetDefaultVisibility() {
        when(projectMapper.toEntity(projectDto)).thenReturn(project);
        projectService.create(userId, projectDto);
        Assertions.assertEquals(ProjectVisibility.PUBLIC, project.getVisibility());
    }

    @Test
    void createWithValidParametersShouldSaveProject() {
        when(projectMapper.toEntity(projectDto)).thenReturn(project);
        projectService.create(userId, projectDto);
        verify(projectRepository).save(project);
    }

    @Test
    void createWithValidParametersShouldReturnProjectDto() {
        when(projectMapper.toEntity(projectDto)).thenReturn(project);
        projectService.create(userId, projectDto);
        verify(projectMapper).toDto(projectRepository.save(project));
    }

    @Test
    void updateWithNotValidParametersShouldThrowException() {
        doThrow(ValidationException.class).when(projectValidator).validateUpdating(projectDto);
        Assertions.assertThrows(ValidationException.class, () -> projectService.update(projectDto));
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void updateWithValidParametersShouldMapToEntity() {
        when(projectMapper.toEntity(projectDto)).thenReturn(project);
        projectService.update(projectDto);
        verify(projectMapper).toEntity(projectDto);
    }

    @Test
    void updateWithValidParametersShouldSaveProject() {
        when(projectMapper.toEntity(projectDto)).thenReturn(project);
        projectService.update(projectDto);
        verify(projectRepository).save(project);
    }

    @Test
    void updateWithValidParametersShouldReturnProjectDto() {
        when(projectMapper.toEntity(projectDto)).thenReturn(project);
        projectService.update(projectDto);
        verify(projectMapper).toDto(projectRepository.save(project));
    }

    @Test
    void findProjectsWithFilterWhenInvokesShouldFindAllAvailable() {
        projectService.findProjectsWithFilter(userId, projectFilterDto);
        verify(projectRepository).findAllAvailableProjectsByUserId(1L);
    }

    @Test
    void findProjectsWithFilterWhenInvokesShouldMapToDtos() {
        projectService.findProjectsWithFilter(userId, projectFilterDto);
        verify(projectMapper).toDtos(anyList());
    }

    @Test
    void findByIdWithNotExistIdPatternShouldThrowException() {
        doThrow(ValidationException.class).when(projectValidator).validateProjectFilterDtoForFindById(projectFilterDto);
        assertThrows(ValidationException.class, () -> projectService.findById(userId, projectFilterDto));
    }

    @Test
    void findByIdWithNotExistProjectShouldThrowException() {
        projectFilterDto.setIdPattern(1L);
        Exception exception = assertThrows(ValidationException.class,
                () -> projectService.findById(userId, projectFilterDto));
        assertEquals("Project not found", exception.getMessage());
    }

    @Test
    void findByIdWithValidParametersShouldReturnProjectDto() {
        projectFilterDto.setIdPattern(1L);
        when(projectRepository.findAvailableByUserIdAndProjectId(userId, 1L))
                .thenReturn(Optional.of(project));
        projectService.findById(userId, projectFilterDto);
        verify(projectMapper).toDto(any(Project.class));
    }
}
