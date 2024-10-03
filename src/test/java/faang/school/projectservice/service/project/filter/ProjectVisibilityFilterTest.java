package faang.school.projectservice.service.project.filter;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ProjectVisibilityFilterTest {

    @InjectMocks
    ProjectVisibilityFilterImpl projectVisibilityFilter;

    private static final Team team = new Team();
    private static final TeamMember teamMember = new TeamMember();
    private static final Project projectPublic = new Project();
    private static final Project projectPrivate = new Project();
    private static Long userId;
    private static Long teamMemberId = 1L;

    @BeforeAll
    static void setup() {
        projectPublic.setVisibility(ProjectVisibility.PUBLIC);
        projectPrivate.setVisibility(ProjectVisibility.PRIVATE);
        teamMember.setId(teamMemberId);
        team.setTeamMembers(List.of(teamMember));
        projectPrivate.setTeams(List.of(team));
    }

    @Test
    void isVisible_publicProject_returnsTrue() {
        userId = 1L;
        boolean result = projectVisibilityFilter.isVisible(projectPublic, userId);
        assertTrue(result);
    }

    @Test
    void isVisible_privateProjectHasMatchingTeamMemberId_returnsTrue() {
        userId = 1L;
        boolean result = projectVisibilityFilter.isVisible(projectPrivate, userId);
        assertTrue(result);
    }

    @Test
    void isVisible_privateProjectNoMatchingTeamMemberId_returnsFalse() {
        userId = 2L;
        boolean result = projectVisibilityFilter.isVisible(projectPrivate, userId);
        assertFalse(result);
    }
}
