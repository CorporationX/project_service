package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.filter.ProjectFilter;
import faang.school.projectservice.service.project.filter.ProjectNameFilter;
import faang.school.projectservice.service.project.filter.ProjectStatusFilter;
import faang.school.projectservice.service.project.filter.ProjectVisibilityFilter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private static ProjectNameFilter projectNameFilter;

    @Mock
    private static ProjectStatusFilter projectStatusFilter;

    @Mock
    private ProjectVisibilityFilter visibilityFilter;

    @Mock
    private UserContext userContext;

    @Mock
    private List<ProjectFilter> projectFilters;

    @Spy
    private ProjectMapper mapper = Mappers.getMapper(ProjectMapper.class);

    private ProjectDto projectDto = new ProjectDto();
    private ProjectFilterDto filters = new ProjectFilterDto();
    private static Long userId;

    @BeforeAll
    static void setup() {
        userId = 1L;
    }

    @Test
    void create_repeatedNameAndOwner_throwsException() {
        when(projectRepository.existsByOwnerUserIdAndName(
                projectDto.getOwnerId(),
                projectDto.getName())).thenReturn(true);
        assertThrows(DataValidationException.class,
                () -> projectService.create(projectDto));
        verify(projectRepository, times(0)).save(any());
    }

    @Test
    void create_newProject_repositorySaveCalled() {
        when(projectRepository.existsByOwnerUserIdAndName(
                projectDto.getOwnerId(),
                projectDto.getName())).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenReturn(new Project());
        projectService.create(projectDto);
        assertEquals(ProjectStatus.CREATED, projectDto.getStatus());
        verify(mapper, times(1)).toEntity(projectDto);
        verify(projectRepository, times(1)).save(any(Project.class));
        verify(mapper, times(1)).toDto(any(Project.class));
    }

    @Test
    void update_unexistingProject_throwsException() {
        when(projectRepository.existsById(projectDto.getId()))
                .thenReturn(false);
        assertThrows(DataValidationException.class,
                () -> projectService.update(projectDto));
        verifyNoMoreInteractions(projectRepository, mapper);
    }

    @Test
    void update_existingProject_repositorySaveCalled() {
        when(projectRepository.existsById(projectDto.getId()))
                .thenReturn(true);
        when(projectRepository.getProjectById(projectDto.getId())).thenReturn(new Project());
        when(projectRepository.save(any(Project.class))).thenReturn(new Project());
        projectService.update(projectDto);
        verify(projectRepository, times(1)).getProjectById(projectDto.getId());
        verify(projectRepository, times(1)).save(any(Project.class));
        verify(mapper, times(1)).toDto(any(Project.class));
    }

    @Test
    void getFiltered_validRequest_filtersApplied() {
        Project project = new Project();
        List<Project> projects = new ArrayList<>(Collections.singleton(project));
        when(projectRepository.findAll()).thenReturn(projects);
        when(projectFilters.stream()).thenReturn(Stream.of(projectNameFilter, projectStatusFilter));
        when(userContext.getUserId()).thenReturn(userId);
        when(projectNameFilter.isApplicable(filters)).thenReturn(true);
        when(projectStatusFilter.isApplicable(filters)).thenReturn(true);
        when(projectNameFilter.apply(projects, filters)).thenReturn(Stream.of(new Project()));
        when(visibilityFilter.isVisible(project, userId)).thenReturn(true);
        projectService.getFiltered(filters);
        verify(projectNameFilter, times(1)).isApplicable(filters);
        verify(projectStatusFilter, times(1)).isApplicable(filters);
        verify(projectNameFilter, times(1)).apply(projects, filters);
        verify(projectStatusFilter, times(1)).apply(projects, filters);
        verify(visibilityFilter, times(projects.size())).isVisible(project, userId);
        verify(mapper, times(projects.size())).toDto(project);
    }

    @Test
    void getAll_validRequest_returnsListOfProjects() {
        Project project = new Project();
        List<Project> projects = new ArrayList<>(Collections.singleton(project));
        when(userContext.getUserId()).thenReturn(userId);
        when(projectRepository.findAll()).thenReturn(projects);
        when(visibilityFilter.isVisible(project, userId)).thenReturn(true);
        projectService.getAll();
        verify(visibilityFilter, times(1)).isVisible(project, userId);
        verify(mapper, times(1)).toDto(project);
    }

    @Test
    void getById_validRequest_returnsProject() {
        Long id = 1L;
        Project project = new Project();
        when(projectRepository.getProjectById(id)).thenReturn(project);
        projectService.getById(id);
        verify(mapper, times(1)).toDto(project);
    }
}
