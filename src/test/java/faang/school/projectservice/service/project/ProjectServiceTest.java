package faang.school.projectservice.service.project;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.NoProjectsInTheDatabase;
import faang.school.projectservice.exception.ProjectAlreadyExistsException;
import faang.school.projectservice.exception.ProjectDoesNotExistInTheDatabase;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.filters.ProjectFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    private static final String PROJECT_DESCRIPTION = "Description";
    private static final Long USER_ID = 1L;
    private ProjectService projectService;
    private ProjectDto projectDto;
    private Project project;
    private ProjectFilterDto projectFilterDto;
    private UserDto userDto;
    private List<ProjectFilter> projectFilters;

    @Mock
    public ProjectRepository projectRepository;

    @Mock
    public ProjectMapper projectMapper;

    @Mock
    public Team team;

    @Mock
    public TeamMember teamMember;

    @Mock
    public ProjectFilter projectFilter;

    @Mock
    public UserServiceClient userServiceClient;

    @BeforeEach
    void setUp() {
        projectDto = new ProjectDto();
        Long PROJECT_ID = 1L;
        projectDto.setOwnerId(PROJECT_ID);
        String PROJECT_NAME = "Name";
        projectDto.setName(PROJECT_NAME);
        projectDto.setDescription(PROJECT_DESCRIPTION);
        project = new Project();
        project.setId(projectDto.getId());
        project.setName(projectDto.getName());
        project.setDescription(projectDto.getDescription());
        projectFilterDto = new ProjectFilterDto();
        userDto = new UserDto();
        projectFilters = List.of(projectFilter);
        projectService = new ProjectService(projectRepository, projectMapper, userServiceClient, projectFilters);
    }

    @Test
    public void testCreateProjectException() {
        when(projectRepository.existsByOwnerUserIdAndName(anyLong(), anyString())).thenReturn(true);
        assertThrows(ProjectAlreadyExistsException.class, () -> projectService.createProject(projectDto));
    }

    @Test
    public void testCreateProjectWithValidDataExpectProjectDtoReturned() {
        when(projectMapper.toProject(projectDto)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDto);
        ProjectDto actual = projectService.createProject(projectDto);
        assertEquals(actual, projectDto);
    }

    @Test
    public void testUpdateProjectException() {
        when(projectRepository.existsByOwnerUserIdAndName(anyLong(), anyString())).thenReturn(true);
        assertThrows(ProjectDoesNotExistInTheDatabase.class, () -> projectService.updateProject(projectDto, ProjectStatus.IN_PROGRESS, PROJECT_DESCRIPTION));
    }

    @Test
    void updateProjectSetsUpdatedAtToNow() {
        when(projectRepository.existsByOwnerUserIdAndName(anyLong(), anyString())).thenReturn(false);
        when(projectMapper.toProject(any())).thenReturn(project);
        when(projectRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        projectService.updateProject(projectDto, ProjectStatus.IN_PROGRESS, "New Description");
        assertNotNull(project.getUpdatedAt());
        assertTrue(project.getUpdatedAt().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    public void testUpdateProjectWhenAllDataValidExpectUpdatedProjectDtoReturned() {
        when(projectMapper.toProject(projectDto)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDto);
        ProjectDto actual = projectService.updateProject(projectDto, ProjectStatus.IN_PROGRESS, PROJECT_DESCRIPTION);
        assertEquals(actual, projectDto);
    }

    @Test
    public void testUpdateProjectWithNullDescription() {
        when(projectMapper.toProject(projectDto)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDto);
        ProjectDto actual = projectService.updateProject(projectDto, ProjectStatus.IN_PROGRESS, null);
        assertEquals(actual, projectDto);
    }

    @Test
    public void testUpdateProjectWithEmptyDescription() {
        when(projectMapper.toProject(projectDto)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDto);
        ProjectDto actual = projectService.updateProject(projectDto, ProjectStatus.IN_PROGRESS, "");
        assertEquals(actual, projectDto);
    }

    @Test
    public void testGetAllProjectsException() {
        when(projectRepository.findAll()).thenReturn(null);
        assertThrows(NoProjectsInTheDatabase.class, () -> projectService.getAllProjects());
    }

    @Test
    void getAllProjectsWhenProjectsExistReturnsDtoList() {
        List<Project> projects = List.of(project);
        when(projectRepository.findAll()).thenReturn(projects);
        when(projectMapper.toDto(projects)).thenReturn(List.of(projectDto));
        List<ProjectDto> projectDtos = projectService.getAllProjects();
        assertNotNull(projectDtos);
        assertEquals(1, projectDtos.size());
        assertEquals(projectDto, projectDtos.get(0));
    }

    @Test
    void testIsProjectVisibleToUserPublicProjectVisible() {
        project.setVisibility(ProjectVisibility.PUBLIC);
        UserDto user = new UserDto();
        boolean isVisible = projectService.isProjectVisibleToUser(project, user);
        assertTrue(isVisible);
    }

    @Test
    void testIsProjectVisibleToUserPrivateProjectVisibleToTeamMember() {
        project.setVisibility(ProjectVisibility.PRIVATE);
        team = new Team();
        teamMember = new TeamMember();
        teamMember.setUserId(1L);
        team.setTeamMembers(Collections.singletonList(teamMember));
        project.setTeams(Collections.singletonList(team));
        UserDto user = new UserDto();
        user.setId(1L);
        boolean isVisible = projectService.isProjectVisibleToUser(project, user);
        assertTrue(isVisible);
    }

    @Test
    void testIsProjectVisibleToUserPrivateProjectNotVisibleToNonTeamMember() {
        project.setVisibility(ProjectVisibility.PRIVATE);
        team = new Team();
        teamMember = new TeamMember();
        teamMember.setUserId(2L);
        team.setTeamMembers(Collections.singletonList(teamMember));
        project.setTeams(Collections.singletonList(team));
        UserDto user = new UserDto();
        user.setId(1L);
        boolean isVisible = projectService.isProjectVisibleToUser(project, user);
        assertFalse(isVisible);
    }


    @Test
    public void testFindAllByFilterWhenFilterNotApplicableExpectFilteredProjectsEmpty() {
        ProjectVisibility projectVisibility = ProjectVisibility.PUBLIC;
        projectDto.setVisibility(projectVisibility);
        project.setVisibility(projectVisibility);
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        when(projectRepository.findAll()).thenReturn(List.of(project));
        when(projectMapper.toDto(project)).thenReturn(projectDto);
        when(projectFilters.get(0).isApplicable(projectFilterDto)).thenReturn(false);
        when(projectMapper.toProject(projectDto)).thenReturn(project);
        List<ProjectDto> actual = projectService.findAllByFilter(projectFilterDto, USER_ID);
        assertEquals(actual, List.of(projectDto));
    }

    @Test
    public void testFindAllByFilterWhenFilterApplicableButNoProjectsMatchExpectFilteredProjectsEmpty() {
        ProjectVisibility projectVisibility = ProjectVisibility.PUBLIC;
        projectDto.setVisibility(projectVisibility);
        project.setVisibility(projectVisibility);
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        when(projectRepository.findAll()).thenReturn(List.of(project));
        when(projectFilters.get(0).isApplicable(projectFilterDto)).thenReturn(true);
        when(projectFilters.get(0).filter(any(), any())).thenReturn(Stream.empty());
        List<ProjectDto> actual = projectService.findAllByFilter(projectFilterDto, USER_ID);
        assertThat(actual).isEmpty();
    }
}