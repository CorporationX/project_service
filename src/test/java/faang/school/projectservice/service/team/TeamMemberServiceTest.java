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
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

    @Test
    @DisplayName("+ Add members to team")
    public void testAddToTeam() {
        Project project = new Project();
        project.setId(1L);
        project.setOwnerId(1L);

        Team team = new Team();
        team.setId(1L);
        team.setProject(project);

        TeamMember member = new TeamMember();
        member.setId(1L);
        member.setUserId(2L);
        member.setRoles(List.of(TeamRole.DESIGNER, TeamRole.TEAM_LEAD));

        List<UserDto> dtos = List.of(
                UserDto.builder()
                        .id(3L)
                        .username("user_3")
                        .build(),
                UserDto.builder()
                        .id(4L)
                        .username("user_4")
                        .build()
        );

        TeamMember tm1 = new TeamMember();
        tm1.setTeam(team);
        tm1.setUserId(3L);
        tm1.setRoles(List.of(TeamRole.DEVELOPER));
        tm1.setNickname("user_3");

        TeamMember tm2 = new TeamMember();
        tm2.setTeam(team);
        tm2.setUserId(4L);
        tm2.setRoles(List.of(TeamRole.DEVELOPER));
        tm2.setNickname("user_4");

        List<TeamMember> assumedMembers = List.of(tm1, tm2);

        when(teamRepository.findById(team.getId()))
                .thenReturn(Optional.of(team));
        when(userContext.getUserId())
                .thenReturn(1L);
        when(teamMemberRepository.findByUserIdAndProjectId(1L, project.getId())).thenReturn(null);
        when(userServiceClient.getUsersByIds(List.of(3L, 4L)))
                .thenReturn(dtos);
        when(teamMemberRepository.saveAll(List.of(tm1, tm2)))
                .thenReturn(List.of(tm1, tm2));

        List<TeamMember> createdMembers = teamMemberService.addToTeam(team.getId(), TeamRole.DEVELOPER, List.of(3L, 4L));

        assertNotNull(createdMembers);
        assertIterableEquals(assumedMembers, createdMembers);
    }

    @Test
    @DisplayName("- Add members to team: team not found")
    public void testAddToTeam_TeamIdInvalid() {
        when(teamRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> teamMemberService.addToTeam(1L, TeamRole.DEVELOPER, List.of(1L, 2L)));
    }

    @Test
    @DisplayName("- Add members to team: no tech lead privileges")
    public void testAddToTeam_NotTechLead() {
        Project project = new Project();
        project.setId(1L);
        project.setOwnerId(1L);

        Team team = new Team();
        team.setId(1L);
        team.setProject(project);

        TeamMember member = new TeamMember();
        member.setId(1L);
        member.setUserId(2L);
        member.setRoles(List.of(TeamRole.DESIGNER));

        when(teamRepository.findById(team.getId()))
                .thenReturn(Optional.of(team));
        when(userContext.getUserId()).thenReturn(2L);
        when(teamMemberRepository.findByUserIdAndProjectId(member.getUserId(), project.getId()))
                .thenReturn(member);
        assertThrows(SecurityException.class,
                () -> teamMemberService.addToTeam(team.getId(), TeamRole.DEVELOPER, List.of(3L, 4L)));
    }

    @Test
    @DisplayName("- Add members to team: feign doesn't locate users by id")
    public void testAddToTeam_InvalidUserId() {
        Project project = new Project();
        project.setId(1L);
        project.setOwnerId(1L);

        Team team = new Team();
        team.setId(1L);
        team.setProject(project);

        TeamMember member = new TeamMember();
        member.setId(1L);
        member.setUserId(2L);
        member.setRoles(List.of(TeamRole.DESIGNER, TeamRole.TEAM_LEAD));

        when(teamRepository.findById(team.getId()))
                .thenReturn(Optional.of(team));
        when(userContext.getUserId()).thenReturn(2L);
        when(teamMemberRepository.findByUserIdAndProjectId(member.getUserId(), project.getId()))
                .thenReturn(member);
        when(userServiceClient.getUsersByIds(List.of(3L, 4L)))
                .thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class,
                () -> teamMemberService.addToTeam(team.getId(), TeamRole.DEVELOPER, List.of(3L, 4L)));
    }

    @Test
    @DisplayName("- Add members to team: user to be added already in project")
    public void testAddToTeam_UserAlreadyInProject() {
        Project project = new Project();
        project.setId(1L);
        project.setOwnerId(1L);

        Team team = new Team();
        team.setId(1L);
        team.setProject(project);

        TeamMember member = new TeamMember();
        member.setId(1L);
        member.setUserId(2L);
        member.setRoles(List.of(TeamRole.DESIGNER, TeamRole.TEAM_LEAD));

        List<UserDto> dtos = List.of(
                UserDto.builder().id(3L).build(),
                UserDto.builder().id(4L).build()
        );

        when(teamRepository.findById(team.getId()))
                .thenReturn(Optional.of(team));
        when(userContext.getUserId()).thenReturn(2L);
        when(teamMemberRepository.findByUserIdAndProjectId(member.getUserId(), project.getId()))
                .thenReturn(member);
        when(userServiceClient.getUsersByIds(List.of(3L, 4L)))
                .thenReturn(dtos);
        when(teamMemberRepository.findByUserIdAndProjectId(3L, project.getId()))
                .thenReturn(member);
        assertThrows(IllegalStateException.class,
                () -> teamMemberService.addToTeam(team.getId(), TeamRole.DEVELOPER, List.of(3L, 4L)));
    }

    @Test
    @DisplayName("+ Update roles")
    public void testUpdateMemberRoles() {
        Team team = new Team();
        team.setId(1L);

        Project project = new Project();
        project.setId(1L);
        team.setProject(project);

        TeamMember member = new TeamMember();
        member.setUserId(1L);
        member.setId(1L);
        member.setTeam(team);
        member.setRoles(List.of(TeamRole.TEAM_LEAD));

        when(teamMemberRepository.findByIdOrThrow(member.getId())).thenReturn(member);
        when(userContext.getUserId()).thenReturn(member.getId());
        when(teamMemberRepository.findByUserIdAndProjectId(userContext.getUserId(), project.getId()))
                .thenReturn(member);
        when(teamMemberRepository.save(any())).thenReturn(member);

        TeamMember updated = teamMemberService.updateMemberRoles(member.getId(), List.of(TeamRole.DEVELOPER));
        assertNotNull(updated);
        verify(teamMemberRepository, times(1)).findByIdOrThrow(member.getId());
        verify(teamMemberRepository, times(1)).findByUserIdAndProjectId(userContext.getUserId(), project.getId());
        assertThat(updated).usingRecursiveComparison().isEqualTo(member);
    }

    @Test
    @DisplayName("- Update roles: member ID invalid")
    public void testUpdateMemberRoles_InvalidMemberId() {
        when(teamMemberRepository.findByIdOrThrow(1L))
                .thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class,
                () -> teamMemberService.updateMemberRoles(1L, List.of(TeamRole.DEVELOPER)));
    }

    @Test
    @DisplayName("- Update roles: caller invalid")
    public void testUpdateMemberRoles_CallerInvalid() {
        Team team = new Team();
        team.setId(1L);

        Project project = new Project();
        project.setId(1L);
        team.setProject(project);

        TeamMember member = new TeamMember();
        member.setUserId(1L);
        member.setId(1L);
        member.setTeam(team);
        member.setRoles(List.of(TeamRole.TEAM_LEAD));

        when(teamMemberRepository.findByIdOrThrow(1L)).thenReturn(member);
        when(userContext.getUserId()).thenReturn(member.getId());
        when(teamMemberRepository.findByUserIdAndProjectId(userContext.getUserId(), project.getId())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> teamMemberService.updateMemberRoles(member.getId(), List.of(TeamRole.DEVELOPER)));
    }

    @Test
    @DisplayName("- Update roles: not a team lead")
    public void testUpdateMemberRoles_NotTeamLead() {
        Team team = new Team();
        team.setId(1L);

        Project project = new Project();
        project.setId(1L);
        team.setProject(project);

        TeamMember member = new TeamMember();
        member.setUserId(1L);
        member.setId(1L);
        member.setTeam(team);
        member.setRoles(List.of(TeamRole.DESIGNER));

        when(teamMemberRepository.findByIdOrThrow(1L)).thenReturn(member);
        when(userContext.getUserId()).thenReturn(member.getId());
        when(teamMemberRepository.findByUserIdAndProjectId(userContext.getUserId(), project.getId())).thenReturn(member);

        assertThrows(SecurityException.class,
                () -> teamMemberService.updateMemberRoles(member.getId(), List.of(TeamRole.DEVELOPER)));
    }

    @Test
    @DisplayName("- Update roles: empty roles")
    public void testUpdateMemberRoles_TeamRolesEmpty() {
        Team team = new Team();
        team.setId(1L);

        Project project = new Project();
        project.setId(1L);
        team.setProject(project);

        TeamMember member = new TeamMember();
        member.setUserId(1L);
        member.setId(1L);
        member.setTeam(team);
        member.setRoles(List.of(TeamRole.DESIGNER));

        when(teamMemberRepository.findByIdOrThrow(1L)).thenReturn(member);
        when(userContext.getUserId()).thenReturn(member.getId());
        when(teamMemberRepository.findByUserIdAndProjectId(userContext.getUserId(), project.getId())).thenReturn(member);

        assertThrows(SecurityException.class,
                () -> teamMemberService.updateMemberRoles(member.getId(), List.of()));
    }

    @Test
    @DisplayName("+ Update nickname")
    public void testUpdateMemberNickname() {
        String updatedNickname = "updated_nickname";
        TeamMember tm = new TeamMember();
        tm.setUserId(1L);
        tm.setId(1L);
        tm.setNickname("old_nickname");

        TeamMember saved = new TeamMember();
        tm.setUserId(1L);
        tm.setId(1L);
        saved.setNickname(updatedNickname);

        when(teamMemberRepository.findByIdOrThrow(tm.getId())).thenReturn(tm);
        when(userContext.getUserId()).thenReturn(1L);
        when(teamMemberRepository.save(tm)).thenReturn(saved);

        TeamMember updated = teamMemberService.updateMemberNickname(tm.getId(), updatedNickname);

        assertNotNull(updated);
        verify(teamMemberRepository, times(1)).findByIdOrThrow(tm.getId());
        verify(teamMemberRepository, times(1)).save(tm);
        assertEquals(saved, updated);
    }

    @Test
    @DisplayName("- Update nickname: member id invalid ")
    public void testUpdateMemberNickname_InvalidId() {
        when(teamMemberRepository.findByIdOrThrow(1L)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class,
                () -> teamMemberService.updateMemberNickname(1L, "old_nickname"));
    }

    @Test
    @DisplayName("- Update nickname: owner id != user id ")
    public void testUpdateMemberNickname_NotOwner() {
        TeamMember tm = new TeamMember();
        tm.setId(1L);
        tm.setUserId(2L);
        when(teamMemberRepository.findByIdOrThrow(tm.getId())).thenReturn(tm);
        when(userContext.getUserId()).thenReturn(1L);

        assertThrows(SecurityException.class,
                () -> teamMemberService.updateMemberNickname(tm.getId(), "nickname"));
    }

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
        when(teamMemberRepository.findByUserIdAndProjectId(dto1.getId(), project.getId())).thenReturn(tm1);
        when(teamMemberRepository.findByUserIdAndProjectId(dto2.getId(), project.getId())).thenReturn(tm2);
        doNothing().when(teamMemberRepository).deleteAll(List.of(tm1, tm2));

        teamMemberService.removeFromProject(project.getId(), List.of(dto1.getId(), dto2.getId()));

        verify(projectRepository, times(1)).findByIdThrowing(project.getId());
        verify(userContext, times(1)).getUserId();
        verify(userServiceClient, times(1)).getUsersByIds(List.of(2L, 3L));
        verify(teamMemberRepository, times(1)).findByUserIdAndProjectId(dto1.getId(), project.getId());
        verify(teamMemberRepository, times(1)).findByUserIdAndProjectId(dto2.getId(), project.getId());

        ArgumentCaptor<List<TeamMember>> tmCaptor = ArgumentCaptor.forClass(List.class);
        verify(teamMemberRepository, times(1)).deleteAll(tmCaptor.capture());
        assertIterableEquals(List.of(tm1, tm2), tmCaptor.getValue());
    }

    @Test
    @DisplayName("- Remove members from project: project ID invalid")
    public void testRemoveMembersFromProject_NotFound() {
        when(projectRepository.findByIdThrowing(1L)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class,
                () -> teamMemberService.removeFromProject(1L, List.of(1L, 2L)));
    }

    @Test
    @DisplayName("- Remove members from project: context not working")
    public void testRemoveMembersFromProject_ContextError() {
        Project project = new Project();
        project.setId(1L);

        when(projectRepository.findByIdThrowing(1L)).thenReturn(project);
        assertThrows(NullPointerException.class,
                () -> teamMemberService.removeFromProject(1L, List.of(1L, 2L)));
    }

    @Test
    @DisplayName("- Remove members from project: not project owner")
    public void testRemoveMembersFromProject_NotProjectOwner() {
        Project project = new Project();
        project.setId(1L);
        project.setOwnerId(2L);

        when(projectRepository.findByIdThrowing(1L)).thenReturn(project);
        when(userContext.getUserId()).thenReturn(1L);
        assertThrows(SecurityException.class,
                () -> teamMemberService.removeFromProject(1L, List.of(1L, 2L)));
    }

    @Test
    @DisplayName("- Remove members from project: user IDs invalid")
    public void testRemoveMembersFromProject_UsersNotFound() {
        Project project = new Project();
        project.setId(1L);
        project.setOwnerId(1L);
        List<Long> userIds = List.of(1L, 2L);

        when(projectRepository.findByIdThrowing(1L)).thenReturn(project);
        when(userContext.getUserId()).thenReturn(1L);
        when(userServiceClient.getUsersByIds(userIds)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class,
                () -> teamMemberService.removeFromProject(1L, userIds));
    }

    @Test
    @DisplayName("- Remove members from project: members not registered for IDs")
    public void testRemoveMembersFromProject_MembersNotOnTeam() {
        Project project = new Project();
        project.setId(1L);
        project.setOwnerId(1L);
        List<Long> userIds = List.of(1L, 2L);
        List<UserDto> dtos = List.of(
                UserDto.builder().id(1L).build(),
                UserDto.builder().id(2L).build()
        );

        when(projectRepository.findByIdThrowing(1L)).thenReturn(project);
        when(userContext.getUserId()).thenReturn(1L);
        when(userServiceClient.getUsersByIds(userIds)).thenReturn(dtos);
        when(teamMemberRepository.findByUserIdAndProjectId(project.getId(), dtos.get(0).getId())).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class,
                () -> teamMemberService.removeFromProject(1L, userIds));
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
    @DisplayName("+ Get member by id")
    public void testGetMember() {
        TeamMember member = new TeamMember();
        member.setId(1L);
        when(teamMemberRepository.findByIdOrThrow(member.getId())).thenReturn(member);

        TeamMember result = teamMemberService.getMemberById(member.getId());

        verify(teamMemberRepository, times(1)).findByIdOrThrow(member.getId());
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("- Get member by id: invalid ID")
    public void testGetMember_invalidId() {
        when(teamMemberRepository.findByIdOrThrow(1L)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class,
                () -> teamMemberService.getMemberById(1L));
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
