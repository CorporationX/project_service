package faang.school.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectVisibilityFilter;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProjectVisibilityFilterTest {

    private ProjectVisibilityFilter projectVisibilityFilter;

    private ProjectFilterDto filterDto;

    @BeforeEach
    public void setUp() {
        projectVisibilityFilter = new ProjectVisibilityFilter();
        filterDto = new ProjectFilterDto();
    }

    @Test
    public void testFilterByVisibilityWhenProjectIsPrivateAndMemberIsInTeam() {
        Project project = mock(Project.class);
        when(project.getVisibility()).thenReturn(ProjectVisibility.PRIVATE);

        TeamMember member = mock(TeamMember.class);
        when(member.getId()).thenReturn(1L);
        Team team = mock(Team.class);
        when(team.getTeamMembers()).thenReturn(List.of(member));
        when(project.getTeams()).thenReturn(List.of(team));

        filterDto.setTeamMemberId(1);
        Stream<Project> projects = Stream.of(project);
        Stream<Project> result = projectVisibilityFilter.apply(projects, filterDto);
        assertEquals(1L, result.count());

    }

    @Test
    public void testFilterByVisibilityWhenProjectIsPrivateAndMemberIsNotInTeam() {
        Project project = mock(Project.class);
        when(project.getVisibility()).thenReturn(ProjectVisibility.PRIVATE);

        TeamMember member = mock(TeamMember.class);
        when(member.getId()).thenReturn(2L);
        Team team = mock(Team.class);
        when(team.getTeamMembers()).thenReturn(List.of(member));
        when(project.getTeams()).thenReturn(List.of(team));

        filterDto.setTeamMemberId(1);
        Stream<Project> projects = Stream.of(project);
        Stream<Project> result = projectVisibilityFilter.apply(projects, filterDto);

        assertEquals(0L, result.count());
    }

    @Test
    public void testFilterByVisibilityWhenProjectIsPublic() {
        Project project = mock(Project.class);
        when(project.getVisibility()).thenReturn(ProjectVisibility.PUBLIC);
        filterDto.setTeamMemberId(1);

        Stream<Project> projects = Stream.of(project);
        Stream<Project> result = projectVisibilityFilter.apply(projects, filterDto);

        assertEquals(1L, result.count());
    }
}
