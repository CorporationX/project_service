package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.filter.project.ProjectFilterStatus;
import faang.school.projectservice.filter.project.ProjectTitleFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @Spy
    private ProjectMapper projectMapper;
    @InjectMocks
    private ProjectService projectService;
    ProjectDto projectDto;
    Project project;
    Project project1;
    Project project2;
    Project project3;
    Long ownerId = 1L;

    @BeforeEach
    public void init() {
        ProjectTitleFilter projectTitleFilter = new ProjectTitleFilter();
        ProjectFilterStatus projectFilterStatus = new ProjectFilterStatus();
        List<ProjectFilter> projectFilters = List.of(projectTitleFilter, projectFilterStatus);
        projectService = new ProjectService(projectRepository, projectMapper, projectFilters);
        projectDto = ProjectDto.builder().id(1L).description("s").name("q").ownerId(1L).build();
        project = Project.builder().id(1L).createdAt(LocalDateTime.now()).description("s").name("q").ownerId(ownerId).build();

        project1 = Project.builder().id(1L).createdAt(LocalDateTime.now()).description("s").name("CorporationX").status(ProjectStatus.CREATED).build();
        project2 = Project.builder().id(2L).createdAt(LocalDateTime.now()).description("b").name("CorporationX").status(ProjectStatus.ON_HOLD).build();
        project3 = Project.builder().id(3L).createdAt(LocalDateTime.now()).description("a").name("Facebook").status(ProjectStatus.CREATED).build();
    }

    @Test
    void testCreateProjectThrowsException() {
        Mockito.when(projectMapper.toProject(projectDto))
                .thenReturn(project);
        Mockito.when(projectRepository.existsByOwnerUserIdAndName(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(true);

        assertThrows(DataValidationException.class, () -> projectService.createProject(projectDto));
    }

    @Test
    void testCreateProject() {
        ProjectDto projectDto1 = ProjectDto.builder().id(1L).description("s").name("q").ownerId(1L).status(ProjectStatus.CREATED).build();
        Mockito.when(projectRepository.existsByOwnerUserIdAndName(Mockito.anyLong(), Mockito.anyString())).thenReturn(false);
        Mockito.when(projectRepository.save(any(Project.class))).thenReturn(project);
        Mockito.when(projectMapper.toProject(projectDto)).thenReturn(project);
        Mockito.when(projectMapper.toProjectDto(project)).thenReturn(projectDto1);
        assertEquals(ProjectStatus.CREATED, projectService.createProject(projectDto).getStatus());
    }

    @Test
    void testUpdateProject() {
        ProjectDto projectDtoForUpdate = ProjectDto.builder().id(1L).description("new description").name("q").ownerId(1L).status(ProjectStatus.CREATED).build();
        Mockito.when(projectRepository.getProjectById(Mockito.anyLong())).thenReturn(project);
        Mockito.when(projectMapper.toProject(projectDtoForUpdate)).thenReturn(project);
        projectService.updateProject(1L, projectDtoForUpdate);
        Mockito.verify(projectRepository, Mockito.times(1)).save(projectMapper.toProject(projectDtoForUpdate));
    }

    @Test
    void testGetFilteredProjectsByTitle() {
        Mockito.when(projectRepository.findAll()).thenReturn(List.of(project1, project2, project3));

        ProjectFilterDto projectFilterDto = ProjectFilterDto.builder().name("CorporationX").status(ProjectStatus.CREATED).build();
        List<ProjectDto> projectDtoList = projectService.getProjectByFilter(projectFilterDto);
        assertEquals(1, projectDtoList.size());
    }

    @Test
    void testGetFilteredProjectsByStatus() {
        Mockito.when(projectRepository.findAll()).thenReturn(List.of(project1, project2, project3));

        ProjectFilterDto projectFilterDto = ProjectFilterDto.builder().status(ProjectStatus.CREATED).build();

        List<ProjectDto> projectDtoList = projectService.getProjectByFilter(projectFilterDto);
        assertEquals(2, projectDtoList.size());
    }

    @Test
    void testGetAllProjects() {
        Mockito.when(projectRepository.findAll()).thenReturn(List.of(project1, project2, project3));
        List<ProjectDto> projectDtoList = projectService.getAllProjects();
        assertEquals(3, projectDtoList.size());
    }

    @Test
    void testGetProjectById() {
        Mockito.when(projectRepository.getProjectById(1L)).thenReturn(project);
        assertEquals(projectMapper.toProjectDto(project), projectService.getProjectById(1L));
    }

    @Test
    void testGetProjectByIdThrowsException() {
        Mockito.when(projectRepository.getProjectById(-1L)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> projectService.getProjectById(-1L));
    }

    @Test
    void testCheckManageRoleAssertTrue() {
        TeamMember teamMember1 = TeamMember.builder()
                .userId(1L)
                .roles(List.of(TeamRole.MANAGER))
                .build();
        TeamMember teamMember2 = TeamMember.builder()
                .userId(2L)
                .roles(List.of(TeamRole.MANAGER))
                .build();
        Project project = Project.builder()
                .teams(List.of(Team
                        .builder()
                        .teamMembers(List.of(teamMember1, teamMember2))
                        .build()))
                .build();

        Mockito.when(projectRepository.getProjectById(1L)).thenReturn(project);

        assertTrue(projectService.checkManagerRole(1L, 1L));
    }

    @Test
    void testCheckManageRoleAssertFalse() {
        TeamMember teamMember1 = TeamMember.builder()
                .userId(1L)
                .roles(List.of(TeamRole.DESIGNER))
                .build();
        TeamMember teamMember2 = TeamMember.builder()
                .userId(2L)
                .roles(List.of(TeamRole.DEVELOPER))
                .build();
        Project project = Project.builder()
                .teams(List.of(Team
                        .builder()
                        .teamMembers(List.of(teamMember1, teamMember2))
                        .build()))
                .build();

        Mockito.when(projectRepository.getProjectById(1L)).thenReturn(project);

        assertFalse(projectService.checkManagerRole(1L, 1L));
    }

    @Test
    void testCheckOwnerProjectAssertTrue() {
        Project project = Project.builder()
                .ownerId(1L)
                .build();

        Mockito.when(projectRepository.getProjectById(1L)).thenReturn(project);

        assertTrue(projectService.checkOwnerProject(1L, 1L));
    }

    @Test
    void testCheckOwnerProjectAssertFalse() {
        Project project = Project.builder()
                .ownerId(1L)
                .build();

        Mockito.when(projectRepository.getProjectById(1L)).thenReturn(project);

        assertFalse(projectService.checkOwnerProject(1L, 2L));
    }
}