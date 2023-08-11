package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filters.ProjectFilter;
import faang.school.projectservice.filters.ProjectFilterByName;
import faang.school.projectservice.filters.ProjectFilterByStatus;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.Assert;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Spy
    private ProjectMapper projectMapper = new ProjectMapperImpl();
    private ProjectDto projectDto;
    private Project project;

    private TeamMember teamMemberCurrentUser;
    private Team teamWithCurrentUser;

    private Team team;

    private TeamMember teamMember;

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
    }

    @Test
    void createValidProject() {
        TeamMember teamMember = new TeamMember();
        teamMember.setUserId(1L);

        ProjectDto projectDto = new ProjectDto();
        projectDto.setOwnerId(teamMember.getId());
        projectDto.setName("crud");

        projectService.create(projectDto);
        Mockito.verify(projectRepository, Mockito.times(1))
                .save(projectMapper.toEntity(projectDto));
    }

    @Test
    void createWithDataValidationException() {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setOwnerId(1L);
        projectDto.setName("crud");


        Mockito.when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName()))
                .thenReturn(true);

        assertThrows(DataValidationException.class, () -> projectService.create(projectDto));
    }

    @Test
    void updateStatusAndDescription() {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(1L);
        projectDto.setStatus(ProjectStatus.CREATED);
        projectDto.setDescription("crud");
        projectDto.setUpdatedAt(LocalDateTime.now());

        Long id = projectDto.getId();
        Mockito.when(projectRepository.getProjectById(id))
                .thenReturn(Project.builder().build());

        projectService.updateStatusAndDescription(projectDto, id);

        Mockito.verify(projectRepository, Mockito.times(1))
                .save(projectMapper.toEntity(projectDto));

        Project projectById = projectRepository.getProjectById(id);
        assertEquals(projectDto.getDescription(), projectById.getDescription());
        assertEquals(projectDto.getStatus(), projectById.getStatus());
        assertEquals(projectDto.getUpdatedAt(), projectById.getUpdatedAt());
    }

    @Test
    void updateStatusAndDescriptionProjectNotFound() {
        assertThrows(DataValidationException.class, () -> projectService.updateStatusAndDescription(new ProjectDto(), null));
    }

    @Test
    void getProjectByNameAndStatus() {
        Project project1 = Project.builder()
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
        Project project2 = Project.builder()
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
        Project project3 = Project.builder()
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

        List<Project> projects = List.of(project, project1, project2, project3);

        Mockito.when(projectRepository.findAll()).thenReturn(projects);
        List<ProjectFilter> filters = List.of(new ProjectFilterByName(), new ProjectFilterByStatus());
        ProjectFilterDto projectFilterDto = ProjectFilterDto.builder()
                .name("Proj")
                .status(ProjectStatus.CREATED)
                .build();

        projectService = new ProjectService(projectMapper, projectRepository, filters);
        List<ProjectDto> filteredProjectsResult =
                List.of(projectMapper.toDto(project2), projectMapper.toDto(project));

        List<ProjectDto> projectsWithFilter = projectService.getProjectByNameAndStatus(projectFilterDto, 1L);
        Assertions.assertEquals(filteredProjectsResult, projectsWithFilter);
    }

    @Test
    void getAllProjects() {
        List<Project> allProjects = new ArrayList<>();
        Mockito.when(projectRepository.findAll())
                .thenReturn(allProjects);

        List<ProjectDto> list = new ArrayList<>();
        Assertions.assertEquals(list, projectService.getAllProject());
    }

    @Test
    void getProjectById() {
       ProjectDto projectDto = new ProjectDto();
       projectDto.setId(1L);

       Mockito.when(projectRepository.getProjectById(projectDto.getId()))
               .thenReturn(Project.builder().id(1L).build());

       Assertions.assertEquals(projectMapper.toEntity(projectDto), projectMapper.toEntity(projectService.getProjectById(projectDto.getId())));
       assertThrows(DataValidationException.class, () -> projectService.updateStatusAndDescription(new ProjectDto(), null));
       Assert.assertThrows(DataValidationException.class, () -> projectService.create(projectDto));
    }
}