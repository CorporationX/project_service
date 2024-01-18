package faang.school.projectservice.serviсe;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.dto.ProjectUpDateDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.filter.ProjectNameFilter;
import faang.school.projectservice.filter.ProjectStatusFilter;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static faang.school.projectservice.model.ProjectStatus.CREATED;
import static faang.school.projectservice.model.ProjectStatus.IN_PROGRESS;
import static faang.school.projectservice.model.ProjectVisibility.PUBLIC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @Spy
    private ProjectMapperImpl projectMapper;
    @Mock
    private List<ProjectFilter> projectFilters;
    @Mock
    private UserContext userContext;
    @Mock
    private ProjectValidator projectValidator;
    @InjectMocks
    private ProjectService projectService;
    private Project project;

    private List<Project> projects;
    private List<ProjectDto> projectDtos;
    private ProjectDto projectDto;
    private ProjectUpDateDto projectUpDateDto;
    private ProjectFilterDto projectFilterDto;
    private Stream<ProjectFilter> projectFiltersStream;

    @BeforeEach
    void setUp() {
        project = Project.builder().id(1L).status(CREATED).visibility(PUBLIC).build();
        projectDto = projectMapper.toDto(project);
        projects = List.of(project);
        projectDtos = List.of(projectDto);
    }

    @Test
    void testCreateProject() {
        assertEquals(projectDto, projectService.createProject(projectDto));
    }

    @Test
    void testUpdateProject_ShouldReturnUpdateProjectDto() {
        // Arrange
        project = Project.builder().id(1L).status(IN_PROGRESS).visibility(PUBLIC).build();
        projectUpDateDto = ProjectUpDateDto.builder().status(ProjectStatus.COMPLETED).build();
        projectDto = ProjectDto.builder().id(1L).status(ProjectStatus.COMPLETED).build();
        // Act
        when(projectRepository.getProjectById(1L)).thenReturn(project);
        // Assert
        assertEquals(projectDto, projectService.updateProject(1L, projectUpDateDto));
    }

    @Test
    void getAllProjectsWithFilter() {
        // Arrange
        projectFilterDto = ProjectFilterDto.builder().status(CREATED).build();
        projectFiltersStream = Stream.of(new ProjectNameFilter(), new ProjectStatusFilter());
        // Act
        when(projectRepository.findAll()).thenReturn(projects);
        when(projectValidator.validateServiceGetProject(userContext.getUserId(), project)).thenReturn(project);
        when(projectFilters.stream()).thenReturn(projectFiltersStream);
        // Assert
        assertEquals(projectDtos, projectService.getAllProjectsWithFilter(projectFilterDto));
    }

    @Test
    void testGetAllProjects_ShouldReturnAllProjects() {
        // Act
        when(projectRepository.findAll()).thenReturn(projects);
        when(projectValidator.validateServiceGetProject(userContext.getUserId(), project)).thenReturn(project);
        // Assert
        assertEquals(projectDtos, projectService.getAllProjects());
    }

    @Test
    void testGetProjectById() {
        // Act
        when(projectRepository.getProjectById(1L)).thenReturn(project);
        when(projectValidator.validateServiceGetProject(userContext.getUserId(), project)).thenReturn(project);
        // Assert
        assertEquals(projectDto, projectService.getProjectById(1L));
    }
}