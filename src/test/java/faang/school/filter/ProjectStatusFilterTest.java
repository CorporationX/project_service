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
    public void testApply_FiltersProjectsByStatus() {
        Project project1 = mock(Project.class);
        Project project2 = mock(Project.class);
        Project project3 = mock(Project.class);

        when(project1.getStatus()).thenReturn(ProjectStatus.IN_PROGRESS);
        when(project2.getStatus()).thenReturn(ProjectStatus.COMPLETED);
        when(project3.getStatus()).thenReturn(ProjectStatus.IN_PROGRESS);

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
}
