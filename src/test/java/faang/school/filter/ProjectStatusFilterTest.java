package faang.school.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectStatusFilter;
import faang.school.projectservice.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProjectStatusFilterTest {

    private ProjectStatusFilter projectStatusFilter;

    private ProjectFilterDto filterDto;

    @BeforeEach
    public void setUp() {
        projectStatusFilter = new ProjectStatusFilter();
        filterDto = new ProjectFilterDto();
    }

    @Test
    public void testIsApplicableWhenProjectNameIsNull() {
        filterDto.setProjectStatus(null);
        assertFalse(projectStatusFilter.isApplicable(filterDto));
    }

    @Test
    public void testIsApplicableWhenProjectNameIsNotNull() {
        filterDto.setProjectStatus("Active");
        assertTrue(projectStatusFilter.isApplicable(filterDto));
    }

    @Test
    public void testApply_FiltersProjectsByStatusAndVisibility() {
        Project project1 = mock(Project.class);
        Project project2 = mock(Project.class);
        Project project3 = mock(Project.class);

        when(project1.getStatus()).thenReturn(ProjectStatus.IN_PROGRESS);
        when(project2.getStatus()).thenReturn(ProjectStatus.COMPLETED);
        when(project3.getStatus()).thenReturn(ProjectStatus.IN_PROGRESS);

        when(project1.getVisibility()).thenReturn(ProjectVisibility.PUBLIC);
        when(project2.getVisibility()).thenReturn(ProjectVisibility.PRIVATE);
        when(project3.getVisibility()).thenReturn(ProjectVisibility.PRIVATE);

        TeamMember member = mock(TeamMember.class);
        when(member.getId()).thenReturn(1L);
        Team team = mock(Team.class);
        when(team.getTeamMembers()).thenReturn(List.of(member));
        when(project2.getTeams()).thenReturn(List.of(team));
        when(project3.getTeams()).thenReturn(List.of(team));

        filterDto.setProjectStatus("in_progress");
        filterDto.setTeamMemberId(1L);

        Stream<Project> projects = Stream.of(project1, project2, project3);

        Stream<Project> result = projectStatusFilter.apply(projects, filterDto);

        List<Project> filteredProjects = result.toList();

        assertEquals(2, filteredProjects.size());
        assertTrue(filteredProjects.stream().anyMatch(p -> p.getStatus() == ProjectStatus.IN_PROGRESS));
    }

    @Test
    public void testFilterByVisibility_WhenProjectIsPrivateAndMemberIsInTeam_ReturnsTrue() {
        Project project = mock(Project.class);
        when(project.getVisibility()).thenReturn(ProjectVisibility.PRIVATE);

        TeamMember member = mock(TeamMember.class);
        when(member.getId()).thenReturn(1L);
        Team team = mock(Team.class);
        when(team.getTeamMembers()).thenReturn(List.of(member));
        when(project.getTeams()).thenReturn(List.of(team));

        filterDto.setTeamMemberId(1);

        assertTrue(projectStatusFilter.filterByVisibility(project, filterDto));
    }

    @Test
    public void testFilterByVisibility_WhenProjectIsPrivateAndMemberIsNotInTeam_ReturnsFalse() {
        Project project = mock(Project.class);
        when(project.getVisibility()).thenReturn(ProjectVisibility.PRIVATE);

        TeamMember member = mock(TeamMember.class);
        when(member.getId()).thenReturn(2L);
        Team team = mock(Team.class);
        when(team.getTeamMembers()).thenReturn(List.of(member));
        when(project.getTeams()).thenReturn(List.of(team));

        filterDto.setTeamMemberId(1);

        assertFalse(projectStatusFilter.filterByVisibility(project, filterDto));
    }

    @Test
    public void testFilterByVisibility_WhenProjectIsPublic_ReturnsTrue() {
        Project project = mock(Project.class);
        when(project.getVisibility()).thenReturn(ProjectVisibility.PUBLIC);

        assertTrue(projectStatusFilter.filterByVisibility(project, filterDto));
    }
}
