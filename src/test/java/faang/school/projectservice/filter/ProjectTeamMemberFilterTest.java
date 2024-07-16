package faang.school.projectservice.filter;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectTeamMemberFilterTest {

    @Mock
    private UserContext userContext;

    @InjectMocks
    private ProjectTeamMemberFilter projectTeamMemberFilter;

    @Test
    void apply_should_return_projects_where_user_is_team_member() {
        var project1 = new Project();
        var team1 = new Team();
        var member1 = new TeamMember();
        member1.setUserId(1L);
        team1.setTeamMembers(List.of(member1));
        project1.setTeams(List.of(team1));

        var project2 = new Project();
        var team2 = new Team();
        var member2 = new TeamMember();
        member2.setUserId(2L);
        team2.setTeamMembers(List.of(member2));
        project2.setTeams(List.of(team2));

        var project3 = new Project();
        var team3 = new Team();
        var member3 = new TeamMember();
        member3.setUserId(1L);
        team3.setTeamMembers(List.of(member3));
        project3.setTeams(List.of(team3));

        when(userContext.getUserId()).thenReturn(1L);

        var result = projectTeamMemberFilter.apply(Stream.of(project1, project2, project3));

        assertEquals(2, result.count());
    }

    @Test
    void apply_should_return_empty_stream_when_no_teams() {
        var project1 = new Project();
        var project2 = new Project();
        var project3 = new Project();

        var result = projectTeamMemberFilter.apply(Stream.of(project1, project2, project3));

        assertEquals(0, result.count());
    }

    @Test
    void apply_should_return_empty_stream_when_no_team_members() {
        var project1 = new Project();
        var team1 = new Team();
        project1.setTeams(List.of(team1));

        var project2 = new Project();
        var team2 = new Team();
        project2.setTeams(List.of(team2));

        var project3 = new Project();
        var team3 = new Team();
        project3.setTeams(List.of(team3));

        var result = projectTeamMemberFilter.apply(Stream.of(project1, project2, project3));

        assertEquals(0, result.count());
    }

    @Test
    void apply_should_return_empty_stream_when_no_mappings_for_member_exist() {
        var project1 = new Project();
        var team1 = new Team();
        var member1 = new TeamMember();
        team1.setTeamMembers(List.of(member1));
        project1.setTeams(List.of(team1));

        var project2 = new Project();
        var team2 = new Team();
        var member2 = new TeamMember();
        team2.setTeamMembers(List.of(member2));
        project2.setTeams(List.of(team2));

        var project3 = new Project();
        var team3 = new Team();
        var member3 = new TeamMember();
        team3.setTeamMembers(List.of(member3));
        project3.setTeams(List.of(team3));

        var result = projectTeamMemberFilter.apply(Stream.of(project1, project2, project3));

        assertEquals(0, result.count());
    }
}