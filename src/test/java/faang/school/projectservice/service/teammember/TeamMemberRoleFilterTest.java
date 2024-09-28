package faang.school.projectservice.service.teammember;

import faang.school.projectservice.dto.team.TeamFilterDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.teamfilter.TeamMemberRoleFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class TeamMemberRoleFilterTest {

    private TeamMemberRoleFilter roleFilter;
    private TeamFilterDto teamFilterDto;

    @BeforeEach
    public void setUp() {
        roleFilter = new TeamMemberRoleFilter();
    }

    @Test
    public void testIsApplicableShouldReturnTrueWhenTeamFilterDtoIsNotNull() {
        teamFilterDto = new TeamFilterDto();
        teamFilterDto.setTeamRole(TeamRole.DEVELOPER);
        boolean result = roleFilter.isApplicable(teamFilterDto);

        assertThat(result).isTrue();
    }

    @Test
    public void testIsApplicableShouldReturnFalseWhenTeamFilterDtoIsNull() {
        teamFilterDto = null;
        boolean result = roleFilter.isApplicable(teamFilterDto);

        assertThat(result).isFalse();
    }

    @Test
    public void testApplyShouldFilterMembersByRole() {
        TeamMember member = new TeamMember();
        member.setRoles(List.of(TeamRole.DESIGNER));
        Stream<TeamMember> teamMembers = Stream.of(member);

        teamFilterDto = new TeamFilterDto();
        teamFilterDto.setTeamRole(TeamRole.DESIGNER);

        Stream<TeamMember> result = roleFilter.apply(teamMembers, teamFilterDto);

        List<TeamMember> filteredMembers = result.toList();
        assertThat(filteredMembers).containsExactly(member);
    }

    @Test
    public void testApplyShouldReturnEmptyStreamWhenNoMembersHaveTheRole() {
        TeamMember member = new TeamMember();
        member.setRoles(List.of(TeamRole.DEVELOPER));
        Stream<TeamMember> teamMembers = Stream.of(member);

        teamFilterDto = new TeamFilterDto();
        teamFilterDto.setTeamRole(TeamRole.ANALYST);

        Stream<TeamMember> result = roleFilter.apply(teamMembers, teamFilterDto);

        List<TeamMember> filteredMembers = result.toList();
        assertThat(filteredMembers).isEmpty();
    }

}
