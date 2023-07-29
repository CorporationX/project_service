package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataAlreadyExistingException;
import faang.school.projectservice.exception.DataNotExistingException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.exception.PrivateAccessException;
import faang.school.projectservice.exception.DataNotFoundException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
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
import java.util.ArrayList;
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
    private TeamMember teamMemberCurrentUser;
    private Team teamWithCurrentUser;
    private Team team;
    private TeamMember teamMember;
    ProjectDto projectDto;
    Project project;
    Project project1;
    Project project2;
    Project project3;

    @BeforeEach
    void setUp() {
        teamMember = TeamMember.builder()
                .userId(2L)
                .build();
        team = Team.builder()
                .teamMembers(List.of(teamMember))
                .build();
        teamMemberCurrentUser = TeamMember.builder()
                .userId(1L)
                .build();
        teamWithCurrentUser = Team.builder()
                .teamMembers(List.of(teamMemberCurrentUser))
                .build();
        projectDto = ProjectDto.builder()
                .id(1L)
                .name("Project")
                .description("new Project")
                .ownerId(1L)
                .build();
        LocalDateTime now = LocalDateTime.now();
        project = Project.builder()
                .id(1L)
                .name("Project")
                .description("new Project")
                .ownerId(1L)
                .visibility(ProjectVisibility.PRIVATE)
                .teams(List.of(teamWithCurrentUser))
                .status(ProjectStatus.CREATED)
                .createdAt(now)
                .updatedAt(now)
                .build();
        project1 = Project.builder()
                .id(2L)
                .name("Project1")
                .description("new Project")
                .ownerId(1L)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PRIVATE)
                .teams(List.of(team))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        project2 = Project.builder()
                .id(3L)
                .name("Project2")
                .description("new Project")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .teams(List.of(team))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        project3 = Project.builder()
                .id(4L)
                .name("Project3")
                .description("new Project")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PRIVATE)
                .teams(List.of(team))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateProject() {
        projectDto.setName("Project^%$^*^^£     C++, Python/C# Мой проект.    ");
        Mockito.when(projectRepository
                .existsByOwnerUserIdAndName(Mockito.anyLong(), Mockito.anyString())).thenReturn(false);
        Assertions.assertEquals(ProjectStatus.CREATED, projectService.create(projectDto).getStatus());
        Mockito.verify(projectRepository).save(any());
        Assertions.assertEquals("project c++, python/c# мой проект.", projectService.create(projectDto).getName());
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
        DataAlreadyExistingException dataAlreadyExistingException = Assertions
                .assertThrows(DataAlreadyExistingException.class, () -> projectService.create(projectDto));
        Assertions.assertEquals(String.format("User with id: %d already exist project %s",
                projectDto.getOwnerId(), projectDto.getName()), dataAlreadyExistingException.getMessage());
    }

    @Test
    void testUpdateStatus() {
        long projectId = projectDto.getId();
        projectDto.setStatus(ProjectStatus.IN_PROGRESS);
        Mockito.when(projectRepository.getProjectById(projectId)).thenReturn(project);
        Assertions.assertEquals(ProjectStatus.IN_PROGRESS, projectService.update(projectDto).getStatus());
        Mockito.verify(projectRepository).save(any());
        Assertions.assertEquals(project.getDescription(), projectService.update(projectDto).getDescription());
    }

    @Test
    void testUpdateThrowsDataNotExistingException() {
        long projectId = projectDto.getId();
        Mockito.when(projectRepository.getProjectById(projectId)).thenReturn(null);
        Assertions.assertThrows(DataNotFoundException.class, () -> projectService.update(projectDto));
    }

    @Test
    void testUpdateDescription() {
        long projectId = projectDto.getId();
        projectDto.setDescription("New Description");

        Mockito.when(projectRepository.getProjectById(projectId)).thenReturn(project);

        Assertions.assertEquals("New Description", projectService.update(projectDto).getDescription());
        Mockito.verify(projectRepository).save(any());
        Assertions.assertEquals(project.getStatus(), projectService.update(projectDto).getStatus());
    }

    @Test
    void testUpdateStatusAndDescription() {
        long projectId = projectDto.getId();
        projectDto.setStatus(ProjectStatus.IN_PROGRESS);
        projectDto.setDescription("New Description");

        Mockito.when(projectRepository.getProjectById(projectId)).thenReturn(project);
        Assertions.assertEquals(ProjectStatus.IN_PROGRESS, projectService.update(projectDto).getStatus());

        Mockito.verify(projectRepository).save(any());
        String descriptionResult = projectService.update(projectDto).getDescription();
        Assertions.assertEquals("New Description", descriptionResult);
    }

    @Test
    void testGetProjectsWithFilter() {
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

        List<ProjectDto> projectsWithFilter = projectService.getProjectsWithFilter(projectFilterDto, 1L);
        Assertions.assertEquals(filteredProjectsResult, projectsWithFilter);
    }

    @Test
    void testGetAllProjects() {
        List<Project> projects = List.of(project, project1, project2, project3);

        Mockito.when(projectRepository.findAll()).thenReturn(projects);
        List<ProjectDto> projectsExpected =
                List.of(mockProjectMapper.toDto(project2), mockProjectMapper.toDto(project));

        List<ProjectDto> projectsResult = projectService.getAllProjects(1L);
        Assertions.assertEquals(projectsExpected, projectsResult);
    }

    @Test
    void testGetEmptyListProjects() {
        List<Project> projects = List.of(project1, project3);

        Mockito.when(projectRepository.findAll()).thenReturn(projects);
        List<ProjectDto> projectsExpected = new ArrayList<>();

        List<ProjectDto> projectsResult = projectService.getAllProjects(1L);
        Assertions.assertEquals(projectsExpected, projectsResult);
    }

    @Test
    void getProjectById() {
        TeamMember teamMember = TeamMember.builder().team(team).build();
        Mockito.when(teamMemberJpaRepository.findByUserIdAndProjectId(anyLong(), anyLong())).thenReturn(teamMember);
        Mockito.when(projectRepository.getProjectById(Mockito.anyLong())).thenReturn(project);
        Assertions.assertEquals(mockProjectMapper.toDto(project),
                projectService.getProjectById(1L, 1L));
    }

    @Test
    void getProjectByIdThrowsPrivateAccessException() {
        TeamMember teamMember = TeamMember.builder().team(team).build();
        Mockito.when(teamMemberJpaRepository.findByUserIdAndProjectId(anyLong(), anyLong())).thenReturn(teamMember);
        Mockito.when(projectRepository.getProjectById(Mockito.anyLong())).thenReturn(project3);
        PrivateAccessException exception = Assertions.assertThrows(PrivateAccessException.class,
                () -> projectService.getProjectById(1L, 1L));
        Assertions.assertEquals("This project is private", exception.getMessage());
    }
}