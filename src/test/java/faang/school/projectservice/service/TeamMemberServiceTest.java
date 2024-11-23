package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamMemberServiceTest {

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private TeamMemberService teamMemberService;

    private TeamMember teamMember;

    @BeforeEach
    void setUp() {
        teamMember = createTestTeamMember();
    }

    @Test
    @DisplayName("Get team member by id")
    void testGetTeamMemberById() {
        when(teamMemberRepository.findById(teamMember.getId())).thenReturn(teamMember);

        TeamMember result = teamMemberService.getTeamMemberByUserId(teamMember.getId());

        assertNotNull(result);
        assertEquals(teamMember, result);
        assertEquals(TeamRole.OWNER, result.getRoles().get(0));
    }

    @Test
    void testGetProjectParticipantsWithRoleWithExistingRoleReturnsCorrectMembers() {
        Team designerAndDeveloperTeam = setUpTeam();
        Team developerTeam = new Team();
        developerTeam.setTeamMembers(List.of(setUpDeveloper()));
        Project project = new Project();
        project.setTeams(List.of(designerAndDeveloperTeam, developerTeam));

        List<TeamMember> participantsList = teamMemberService.getProjectParticipantsWithRole(
                project, TeamRole.DEVELOPER.toString());

        assertEquals(2, participantsList.size());
        assertEquals(setUpDeveloper(), participantsList.get(0));
        assertEquals(setUpDeveloper(), participantsList.get(1));
    }

    @Test
    void testGetProjectParticipantsWithRoleWithNonExistingRoleReturnsEmptyList() {
        Project project = setUpProject();

        List<TeamMember> result = teamMemberService.getProjectParticipantsWithRole(
                project, TeamRole.INTERN.toString());

        assertEquals(0, result.size());
    }

    @Test
    void testGetProjectParticipantsWithRoleWithEmptyProjectReturnsEmptyList() {
        Project project = new Project();
        project.setTeams(Collections.emptyList());

        List<TeamMember> result = teamMemberService.getProjectParticipantsWithRole(
                project, TeamRole.DESIGNER.toString());

        assertEquals(0, result.size());
    }


    private TeamMember createTestTeamMember() {
        return TeamMember.builder()
                .id(1L)
                .userId(1L)
                .roles(List.of(TeamRole.OWNER))
                .build();
    }

    private TeamMember setUpDeveloper() {
        TeamMember developer = new TeamMember();
        developer.setRoles(List.of(TeamRole.DEVELOPER));
        return developer;
    }

    private TeamMember setUpDesigner() {
        TeamMember designer = new TeamMember();
        designer.setRoles(List.of(TeamRole.DESIGNER));
        return designer;
    }

    private Team setUpTeam() {
        Team team = new Team();
        team.setTeamMembers(List.of(setUpDeveloper(), setUpDesigner()));
        return team;
    }

    private Project setUpProject() {
        Project project = new Project();
        project.setTeams(Collections.singletonList(setUpTeam()));
        return project;
    }
}
