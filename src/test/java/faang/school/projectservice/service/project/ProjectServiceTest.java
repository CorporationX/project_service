package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.event.ProjectCreateEvent;
import faang.school.projectservice.service.project.filter.ProjectFilter;
import faang.school.projectservice.service.resource.ResourceService;
import faang.school.projectservice.validation.project.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Spy
    private ProjectMapperImpl projectMapper;
    private ProjectRepository projectRepository;
    private ProjectValidator projectValidator;
    private ProjectFilter projectFilter;
    private List<ProjectFilter> projectFilters;
    private ProjectService projectService;
    private ResourceService resourceService;
    private ProjectCreateEvent projectCreateEvent;

    @BeforeEach
    void setUp() {
        projectRepository = mock(ProjectRepository.class);
        projectValidator = mock(ProjectValidator.class);
        projectFilter = mock(ProjectFilter.class);
        projectFilters = Collections.singletonList(projectFilter);
        resourceService = mock(ResourceService.class);
        projectCreateEvent = mock(ProjectCreateEvent.class);
        projectService = new ProjectService(projectMapper, projectRepository, projectValidator, projectFilters, resourceService, projectCreateEvent);
    }

    @Test
    void createProject_ValidArgs() {
        long userId = 1L;
        ProjectDto projectDto = getProjectDto();
        ProjectDto expected = getExpectedProjectDto(userId);
        when(projectRepository.save(any(Project.class))).thenReturn(getProject(userId));

        ProjectDto actual = projectService.createProject(userId, projectDto);

        assertEquals(expected, actual);
        verify(projectMapper, times(1)).toEntity(any(ProjectDto.class));
        verify(projectValidator, times(1)).validateProjectCreate(any(ProjectDto.class));
        verify(projectRepository, times(1)).save(any(Project.class));
        verify(projectMapper, times(1)).toDto(any(Project.class));
    }

    @Test
    void updateProject_ValidArgs() {
        long projectId = 1L;
        long userId = 1L;
        ProjectDto projectDto = getProjectDto();
        ProjectDto expected = getExpectedProjectDto(userId);
        when(projectRepository.save(any(Project.class))).thenReturn(getProject(userId));

        ProjectDto actual = projectService.updateProject(projectId, projectDto);

        assertEquals(expected, actual);
        verify(projectMapper, times(1)).toEntity(any(ProjectDto.class));
        verify(projectValidator, times(1)).validateProjectUpdate(any(ProjectDto.class));
        verify(projectRepository, times(1)).save(any(Project.class));
        verify(projectMapper, times(1)).toDto(any(Project.class));
    }

    @Test
    void findAllProjectsByFilters_ReturnsAllProjectsByFilters() {
        long userId = 1L;
        ProjectFilterDto filters = new ProjectFilterDto();
        List<ProjectDto> expected = getProjectDtos();
        when(projectRepository.findAll()).thenReturn(getProjects());
        when(projectFilter.isApplicable(any(ProjectFilterDto.class))).thenReturn(true);

        List<ProjectDto> actual = projectService.findAllProjectsByFilters(userId, filters);

        assertEquals(expected, actual);
        verify(projectRepository, times(1)).findAll();
        verify(projectFilter, times(1)).isApplicable(any(ProjectFilterDto.class));
        verify(projectFilter, times(1)).apply(anyList(), any(ProjectFilterDto.class));
        verify(projectMapper, times(1)).toDto(anyList());
    }

    @Test
    void findAllProjects_ReturnsAllProjects() {
        List<ProjectDto> expected = getProjectDtos();
        when(projectRepository.findAll()).thenReturn(getProjects());

        List<ProjectDto> actual = projectService.findAllProjects();

        assertEquals(expected, actual);
        verify(projectMapper, times(1)).toDto(anyList());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    void findProjectById_ValidArgs() {
        Long projectId = 1L;
        ProjectDto expected = getProjectDto();
        when(projectRepository.getProjectById(anyLong())).thenReturn(getProject(1L));

        ProjectDto actual = projectService.findProjectById(projectId);

        assertEquals(expected, actual);
        verify(projectMapper, times(1)).toDto(any(Project.class));
        verify(projectRepository, times(1)).getProjectById(anyLong());
    }

    private ProjectDto getProjectDto() {
        return ProjectDto.builder()
                .id(1L)
                .name("project")
                .description("description")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    private ProjectDto getExpectedProjectDto(long userId) {
        return ProjectDto.builder()
                .id(1L)
                .name("project")
                .description("description")
                .ownerId(userId)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    private Project getProject(long userId) {
        return Project.builder()
                .id(1L)
                .name("project")
                .description("description")
                .ownerId(userId)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    private List<Project> getProjects() {
        return new ArrayList<>(List.of
                (
                        Project.builder()
                                .id(1L)
                                .name("project")
                                .description("description")
                                .ownerId(1L)
                                .status(ProjectStatus.CREATED)
                                .visibility(ProjectVisibility.PUBLIC)
                                .build(),
                        Project.builder()
                                .id(2L)
                                .name("project")
                                .description("description")
                                .ownerId(1L)
                                .status(ProjectStatus.CREATED)
                                .visibility(ProjectVisibility.PUBLIC)
                                .build()
                ));
    }

    private List<ProjectDto> getProjectDtos() {
        return List.of
                (
                        ProjectDto.builder()
                                .id(1L)
                                .name("project")
                                .description("description")
                                .ownerId(1L)
                                .status(ProjectStatus.CREATED)
                                .visibility(ProjectVisibility.PUBLIC)
                                .build(),
                        ProjectDto.builder()
                                .id(2L)
                                .name("project")
                                .description("description")
                                .ownerId(1L)
                                .status(ProjectStatus.CREATED)
                                .visibility(ProjectVisibility.PUBLIC)
                                .build()
                );
    }
}
