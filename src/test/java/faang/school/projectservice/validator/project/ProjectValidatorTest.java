package faang.school.projectservice.validator.project;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProjectValidatorTest {

    private ProjectRepository projectRepository;
    private ProjectValidator validator;

    @BeforeEach
    void setup() {
        projectRepository = mock(ProjectRepository.class);
        validator = new ProjectValidator(projectRepository);
    }

    @Test
    void validateUniqueProjectNameForOwner_Throws_WhenNameExists() {
        when(projectRepository.existsByOwnerIdAndName(1L, "TestProject")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validateUniqueProjectNameForOwner("TestProject", 1L));
        assertEquals("Project with this name already exists for user", ex.getMessage());
    }

    @Test
    void validateUniqueProjectNameForOwner_Passes_WhenNameDoesNotExist() {
        when(projectRepository.existsByOwnerIdAndName(1L, "TestProject")).thenReturn(false);

        assertDoesNotThrow(() -> validator.validateUniqueProjectNameForOwner("TestProject", 1L));
    }

    @Test
    void validateProjectExists_Throws_WhenProjectNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validateProjectExists(null));
        assertEquals("Project not found", ex.getMessage());
    }

    @Test
    void validateProjectExists_Passes_WhenProjectNotNull() {
        Project project = mock(Project.class);
        assertDoesNotThrow(() -> validator.validateProjectExists(project));
    }

    @Test
    void validateAccessToProject_Throws_WhenPrivateAndUserNotParticipant() {
        Project project = mock(Project.class);
        Team team = mock(Team.class);
        TeamMember member = mock(TeamMember.class);

        when(project.getVisibility()).thenReturn(ProjectVisibility.PRIVATE);
        when(project.getTeams()).thenReturn(List.of(team));

        when(team.getTeamMembers()).thenReturn(List.of(member));
        when(member.getId()).thenReturn(2L);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validateAccessToProject(project, 1L));

        assertEquals("Access denied to private project", ex.getMessage());
    }

    @Test
    void validateAccessToProject_Passes_WhenPublicProject() {
        Project project = mock(Project.class);
        when(project.getVisibility()).thenReturn(ProjectVisibility.PUBLIC);

        assertDoesNotThrow(() -> validator.validateAccessToProject(project, 1L));
    }

    @Test
    void validateAccessToProject_Passes_WhenUserIsParticipant() {
        Project project = mock(Project.class);
        Team team = mock(Team.class);
        TeamMember member = mock(TeamMember.class);

        when(project.getVisibility()).thenReturn(ProjectVisibility.PRIVATE);
        when(project.getTeams()).thenReturn(List.of(team));

        when(team.getTeamMembers()).thenReturn(List.of(member));
        when(member.getId()).thenReturn(1L);

        assertDoesNotThrow(() -> validator.validateAccessToProject(project, 1L));
    }

    @Test
    void validateUpdate_Throws_WhenNothingToUpdate() {
        Project project = mock(Project.class);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validateUpdate(project, null, null));
        assertEquals("Nothing to update", ex.getMessage());
    }

    @Test
    void validateUpdate_CallsValidateProjectExists() {
        Project project = mock(Project.class);
        ProjectValidator spyValidator = spy(validator);

        doNothing().when(spyValidator).validateProjectExists(project);
        spyValidator.validateUpdate(project, ProjectStatus.IN_PROGRESS, null);
        verify(spyValidator).validateProjectExists(project);
    }
}