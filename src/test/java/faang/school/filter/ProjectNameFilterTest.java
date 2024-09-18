package faang.school.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectNameFilter;
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

public class ProjectNameFilterTest {

    private ProjectNameFilter projectNameFilter;

    private ProjectFilterDto filterDto;

    @BeforeEach
    public void setUp() {
        projectNameFilter = new ProjectNameFilter();
        filterDto = new ProjectFilterDto();
    }

    @Test
    public void testIsApplicableWhenProjectNameIsNull() {
        filterDto.setProjectName(null);
        assertFalse(projectNameFilter.isApplicable(filterDto));
    }

    @Test
    public void testIsApplicableWhenProjectNameIsNotNull() {
        filterDto.setProjectName("Some Project");
        assertTrue(projectNameFilter.isApplicable(filterDto));
    }

    @Test
    public void testApplyFiltersProjectsByNameAndVisibility() {
        Project project1 = mock(Project.class);
        Project project2 = mock(Project.class);
        Project project3 = mock(Project.class);

        when(project1.getName()).thenReturn("Test Project 1");
        when(project2.getName()).thenReturn("Test Project 2");
        when(project3.getName()).thenReturn("Some Project");

        when(project1.getVisibility()).thenReturn(ProjectVisibility.PUBLIC);
        when(project2.getVisibility()).thenReturn(ProjectVisibility.PRIVATE);
        when(project3.getVisibility()).thenReturn(ProjectVisibility.PRIVATE);

        TeamMember member = mock(TeamMember.class);
        when(member.getId()).thenReturn(1L);
        Team team = mock(Team.class);
        when(team.getTeamMembers()).thenReturn(List.of(member));
        when(project2.getTeams()).thenReturn(List.of(team));
        when(project3.getTeams()).thenReturn(List.of(team));

        filterDto.setProjectName("Some Project");
        filterDto.setTeamMemberId(1);

        Stream<Project> projects = Stream.of(project1, project2, project3);

        Stream<Project> result = projectNameFilter.apply(projects, filterDto);

        List<Project> filteredProjects = result.toList();

        assertEquals(1, filteredProjects.size());
        assertEquals("Some Project", filteredProjects.get(0).getName());
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

        assertTrue(projectNameFilter.filterByVisibility(project, filterDto));
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

        assertFalse(projectNameFilter.filterByVisibility(project, filterDto));
    }

    @Test
    public void testFilterByVisibilityWhenProjectIsPublic() {
        Project project = mock(Project.class);
        when(project.getVisibility()).thenReturn(ProjectVisibility.PUBLIC);

        assertTrue(projectNameFilter.filterByVisibility(project, filterDto));
    }
}
