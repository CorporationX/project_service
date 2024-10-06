package faang.school.projectservice.service.team;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeamMemberServiceTest {
    @InjectMocks
    private TeamMemberService teamMemberService;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private UserContext userContext;

    @Test
    @DisplayName("+ All members for a team")
    public void getMembersForTeamTest() {
        Team team = new Team();
        team.setId(1L);
        TeamMember tm1 = new TeamMember();
        tm1.setTeam(team);
        tm1.setId(1L);
        TeamMember tm2 = new TeamMember();
        tm1.setTeam(team);
        tm1.setId(2L);
        TeamMember tm3 = new TeamMember();
        tm1.setTeam(team);
        tm1.setId(3L);
        team.setTeamMembers(List.of(tm1, tm2, tm3));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        List<TeamMember> foundMembers = teamMemberService.getMembersForTeam(team.getId());

        verify(teamRepository, times(1)).findById(team.getId());
        assertNotNull(foundMembers);
        assertIterableEquals(List.of(tm1, tm2, tm3), foundMembers);
    }

    @Test
    @DisplayName("- All members for a team: invalid ID")
    public void getMembersForTeamTest_NotFound() {
        assertThrows(EntityNotFoundException.class,
                () -> teamMemberService.getMembersForTeam(1L));
    }

    // create team without members
    // create team with members
    // update nickname
    // update roles

    @Test
    @DisplayName("+ Remove members from project")
    public void testRemoveMembersFromProject() {
        Project project = new Project();
        project.setId(1L);
        project.setOwnerId(1L);

        UserDto dto1 = UserDto.builder()
                .id(2L)
                .build();
        UserDto dto2 = UserDto.builder()
                .id(3L)
                .build();
        TeamMember tm1 = new TeamMember();
        tm1.setUserId(dto1.getId());
        TeamMember tm2 = new TeamMember();
        tm2.setUserId(dto2.getId());

        when(projectRepository.findByIdThrowing(project.getId())).thenReturn(project);
        when(userContext.getUserId()).thenReturn(1L);
        when(userServiceClient.getUsersByIds(List.of(2L, 3L))).thenReturn(List.of(dto1, dto2));
        when(teamMemberRepository.findByUserIdAndProjectId(project.getId(), dto1.getId())).thenReturn(tm1);
        when(teamMemberRepository.findByUserIdAndProjectId(project.getId(), dto2.getId())).thenReturn(tm2);
        doNothing().when(teamMemberRepository).deleteAll(List.of(tm1, tm2));

        teamMemberService.removeFromProject(project.getId(), List.of(dto1.getId(), dto2.getId()));

        verify(projectRepository, times(1)).findByIdThrowing(project.getId());
        verify(userContext, times(1)).getUserId();
        verify(userServiceClient, times(1)).getUsersByIds(List.of(2L, 3L));
        verify(teamMemberRepository, times(1)).findByUserIdAndProjectId(project.getId(), dto1.getId());
        verify(teamMemberRepository, times(1)).findByUserIdAndProjectId(project.getId(), dto2.getId());

        ArgumentCaptor<List<TeamMember>> tmCaptor = ArgumentCaptor.forClass(List.class);
        verify(teamMemberRepository, times(1)).deleteAll(tmCaptor.capture());
        assertIterableEquals(List.of(tm1, tm2), tmCaptor.getValue());
    }

    @Test
    @DisplayName("+ All members for a project: no filtering")
    public void testGetProjectMembersFiltered_WithoutFilters() {
        Project project = new Project();
        project.setId(1L);
        Team t1 = new Team();
        t1.setId(1L);
        Team t2 = new Team();
        t2.setId(2L);

        TeamMember tm1 = new TeamMember();
        tm1.setId(1L);
        tm1.setTeam(t1);
        TeamMember tm2 = new TeamMember();
        tm2.setId(2L);
        tm2.setTeam(t2);

        t1.setTeamMembers(List.of(tm1));
        t2.setTeamMembers(List.of(tm2));
        project.setTeams(List.of(t1, t2));

        when(projectRepository.getProjectByIdOrThrow(project.getId())).thenReturn(project);
        List<TeamMember> resultingMembers = teamMemberService.getProjectMembersFiltered(project.getId(),null, null);

        verify(projectRepository, times(1)).getProjectByIdOrThrow(project.getId());
        assertNotNull(resultingMembers);
        assertIterableEquals(List.of(tm1, tm2), resultingMembers);
    }

    @Test
    @DisplayName("+ All members for a project: nickname filtering")
    public void testGetProjectMembersFiltered_Nickname() {
        Project project = new Project();
        project.setId(1L);
        Team t1 = new Team();
        t1.setId(1L);
        Team t2 = new Team();
        t2.setId(2L);

        TeamMember tm1 = new TeamMember();
        tm1.setId(1L);
        tm1.setTeam(t1);
        tm1.setNickname("user_1");
        TeamMember tm2 = new TeamMember();
        tm2.setId(2L);
        tm2.setTeam(t2);
        tm2.setNickname("nickname_1");

        t1.setTeamMembers(List.of(tm1));
        t2.setTeamMembers(List.of(tm2));
        project.setTeams(List.of(t1, t2));

        when(projectRepository.getProjectByIdOrThrow(project.getId())).thenReturn(project);
        List<TeamMember> foundMembers = teamMemberService.getProjectMembersFiltered(project.getId(),"nick", null);

        verify(projectRepository, times(1)).getProjectByIdOrThrow(project.getId());
        assertNotNull(foundMembers);
        assertIterableEquals(List.of(tm2), foundMembers);
    }

    @Test
    @DisplayName("+ All members for a project: role filtering")
    public void testGetProjectMembersFiltered_Role() {
        Project project = new Project();
        project.setId(1L);
        Team t1 = new Team();
        t1.setId(1L);
        Team t2 = new Team();
        t2.setId(2L);

        TeamMember tm1 = new TeamMember();
        tm1.setId(1L);
        tm1.setTeam(t1);
        tm1.setNickname("user_1");
        tm1.setRoles(List.of(TeamRole.DEVELOPER, TeamRole.INTERN));

        TeamMember tm2 = new TeamMember();
        tm2.setId(2L);
        tm2.setTeam(t2);
        tm2.setNickname("nickname_1");
        tm2.setRoles(List.of(TeamRole.DEVELOPER, TeamRole.DESIGNER));

        t1.setTeamMembers(List.of(tm1));
        t2.setTeamMembers(List.of(tm2));
        project.setTeams(List.of(t1, t2));

        when(projectRepository.getProjectByIdOrThrow(project.getId())).thenReturn(project);
        List<TeamMember> foundMembers = teamMemberService.getProjectMembersFiltered(project.getId(), null, TeamRole.DEVELOPER);

        assertNotNull(foundMembers);
        assertIterableEquals(List.of(tm1, tm2), foundMembers);
    }

    @Test
    @DisplayName("- All members for a project: invalid project ID")
    public void testGetProjectMembersFiltered_NotFound() {
        when(projectRepository.getProjectByIdOrThrow(1L)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class,
                () -> teamMemberService.getProjectMembersFiltered(1L, null, null));
    }

    @Test
    @DisplayName("+ Team members for registered user")
    public void testGetMembersForUser() {
        Team team1 = new Team();
        team1.setId(1L);
        Team team2 = new Team();
        team2.setId(2L);

        UserDto userDto = UserDto.builder()
                .id(1L)
                .build();
        TeamMember tm1 = new TeamMember();
        tm1.setId(2L);

        tm1.setUserId(userDto.getId());
        TeamMember tm2 = new TeamMember();
        tm2.setId(3L);
        tm2.setUserId(userDto.getId());

        when(userServiceClient.getUser(userDto.getId())).thenReturn(userDto);
        when(teamMemberRepository.findByUserId(userDto.getId())).thenReturn(List.of(tm1, tm2));

        List<TeamMember> members = teamMemberService.getMembersForUser(userDto.getId());

        assertNotNull(members);
        verify(userServiceClient, times(1)).getUser(userDto.getId());
        verify(teamMemberRepository, times(1)).findByUserId(userDto.getId());
        assertIterableEquals(List.of(tm1, tm2), members);
    }

    @Test
    @DisplayName("- Team members for registered user: user not found")
    public void testGetMembersForUser_NoUserFound() {
        when(userServiceClient.getUser(1L)).thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class,
                () -> teamMemberService.getMembersForUser(1L));
    }
}
