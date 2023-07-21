package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filters.ProjectFilter;
import faang.school.projectservice.service.filters.ProjectFilterByName;
import faang.school.projectservice.service.filters.ProjectFilterByStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @InjectMocks
    private ProjectService projectService;
    @Spy
    private ProjectMapper mockProjectMapper = new ProjectMapperImpl();
    @Mock
    private ProjectRepository projectRepository;


    ProjectDto projectDto;
    Project project;
    Team team = new Team();

    @BeforeEach
    void setUp() {
        projectDto = ProjectDto.builder()
                .id(1L)
                .name("Project")
                .description("new Project")
                .ownerId(1L)
                .build();
        project = Project.builder()
                .id(1L)
                .name("Project")
                .description("new Project")
                .ownerId(1L)
                .visibility(ProjectVisibility.PRIVATE)
                .teams(List.of(team))
                .status(ProjectStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateProject() {
        Mockito.when(projectRepository
                .existsByOwnerUserIdAndName(Mockito.anyLong(), Mockito.anyString())).thenReturn(false);
        Assertions.assertEquals(ProjectStatus.CREATED, projectService.create(projectDto).getStatus());
        Mockito.verify(projectRepository).save(any());
    }

    @Test
    void testSetDefaultVisibilityProject() {
        Mockito.when(projectRepository
                .existsByOwnerUserIdAndName(Mockito.anyLong(), Mockito.anyString())).thenReturn(false);
        Assertions.assertEquals(ProjectVisibility.PUBLIC, projectService.create(projectDto).getVisibility());
    }

    @Test
    void testSetPrivateVisibilityProject() {
        projectDto.setVisibility(ProjectVisibility.PRIVATE);
        Mockito.when(projectRepository
                .existsByOwnerUserIdAndName(Mockito.anyLong(), Mockito.anyString())).thenReturn(false);
        Assertions.assertEquals(ProjectVisibility.PRIVATE, projectService.create(projectDto).getVisibility());
    }

    @Test
    void testCreateProjectThrowsException() {
        Mockito.when(projectRepository
                .existsByOwnerUserIdAndName(Mockito.anyLong(), Mockito.anyString())).thenReturn(true);
        DataValidationException dataValidationException = Assertions
                .assertThrows(DataValidationException.class, () -> projectService.create(projectDto));
        Assertions.assertEquals(String
                .format("Project %s already exist", projectDto.getName()), dataValidationException.getMessage());
    }

    @Test
    void testUpdateStatus() {
        long projectId = 1L;
        projectDto.setStatus(ProjectStatus.IN_PROGRESS);
        Mockito.when(projectRepository.getProjectById(projectId)).thenReturn(project);
        Assertions.assertEquals(ProjectStatus.IN_PROGRESS, projectService.update(projectDto, projectId).getStatus());
        Mockito.verify(projectRepository).save(any());
    }

    @Test
    void testUpdateDescription() {
        long projectId = 1L;
        projectDto.setDescription("New Description");
        Mockito.when(projectRepository.getProjectById(projectId)).thenReturn(project);
        Assertions.assertEquals("New Description", projectService.update(projectDto, projectId).getDescription());
        Mockito.verify(projectRepository).save(any());
    }

    @Test
    void testGetProjectsWithFilter() {
        Project project1 = Project.builder()
                .id(1L)
                .name("Project1")
                .description("new Project")
                .ownerId(1L)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PRIVATE)
                .teams(List.of(new Team()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Project project2 = Project.builder()
                .id(2L)
                .name("Project2")
                .description("new Project")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .teams(List.of(new Team()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Project project3 = Project.builder()
                .id(3L)
                .name("Project3")
                .description("new Project")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PRIVATE)
                .teams(List.of(new Team()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        List<Project> projects = List.of(project, project1, project2, project3);

        Mockito.when(projectRepository.findAll()).thenReturn(projects);
        List<ProjectFilter> filters = List.of(new ProjectFilterByName(), new ProjectFilterByStatus());
        ProjectFilterDto projectFilterDto = ProjectFilterDto.builder()
                .projectNamePattern("Proj")
                .status(ProjectStatus.CREATED)
                .build();
        projectService = new ProjectService(mockProjectMapper, projectRepository, filters);
        List<ProjectDto> filteredProjectsResult =
                List.of(mockProjectMapper.toDto(project2), mockProjectMapper.toDto(project));

        List<ProjectDto> projectsWithFilter = projectService.getProjectsWithFilter(projectFilterDto, List.of(team));
        Assertions.assertEquals(filteredProjectsResult, projectsWithFilter);
    }
}