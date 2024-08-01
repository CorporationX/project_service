package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    private static final long PROJECT_ID = 1L;
    private static final long USER_ID = 2L;

    @Mock
    private ProjectRepository projectRepository;
    @InjectMocks
    private ProjectService projectService;

    @Test
    @DisplayName("Test checkOwnerPermission true")
    public void testGetProjectById() {
        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(Project.builder().name("project").build());
        when(projectRepository.existsByOwnerUserIdAndName(USER_ID, "project")).thenReturn(true);
        Assertions.assertTrue(projectService.checkOwnerPermission(USER_ID, PROJECT_ID));
    }

    @Test
    @DisplayName("Test checkOwnerPermission false")
    public void testGetProjectByIdWithWrongPermission() {
        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(Project.builder().name("project").build());
        when(projectRepository.existsByOwnerUserIdAndName(USER_ID, "project")).thenReturn(false);
        Assertions.assertFalse(projectService.checkOwnerPermission(USER_ID, PROJECT_ID));
    }

    @Test
    @DisplayName("Test checkManagerPermission true")
    public void testCheckManagerPermissionTrue() {

        TeamMember firstTeam = TeamMember.builder().userId(USER_ID).roles(List.of(TeamRole.MANAGER)).build();
        TeamMember secondTeam = TeamMember.builder().userId(2L).roles(List.of(TeamRole.MANAGER)).build();
        List<TeamMember> teamMembers = List.of(firstTeam,secondTeam);

        List<Team> teams = List.of(Team.builder().id(1L).project(Project.builder().id(PROJECT_ID).build()).teamMembers(teamMembers).build());

        Project project = Project.builder().teams(teams).build();

        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(project);
        Assertions.assertTrue(projectService.checkManagerPermission(USER_ID, PROJECT_ID));
    }

    @Test
    @DisplayName("Test checkManagerPermission false")
    public void testCheckManagerPermissionFalse() {

        TeamMember firstTeam = TeamMember.builder().userId(3L).roles(List.of(TeamRole.MANAGER)).build();
        TeamMember secondTeam = TeamMember.builder().userId(4L).roles(List.of(TeamRole.MANAGER)).build();
        List<TeamMember> teamMembers = List.of(firstTeam, secondTeam);

        Team team = Team.builder().id(1L).project(Project.builder().id(PROJECT_ID).build()).teamMembers(teamMembers).build();
        List<Team> teams = List.of(team);

        Project project = Project.builder().teams(teams).build();

        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(project);
        Assertions.assertFalse(projectService.checkManagerPermission(USER_ID, PROJECT_ID));
    }
}
