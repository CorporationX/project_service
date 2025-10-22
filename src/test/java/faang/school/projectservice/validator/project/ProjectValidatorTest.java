package faang.school.projectservice.validator.project;

import faang.school.projectservice.exception.project.DuplicateResourceException;
import faang.school.projectservice.helpers.TestUtils;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ProjectValidatorTest {

    @Test
    void validateUniqueProjectNameForOwner_NameExists_ThrowsException() {
        String projectName = "Test";
        Long ownerId = 1L;
        boolean nameExists = true;

        Executable executable = () ->
                ProjectValidator.validateUniqueProjectNameForOwner(projectName, ownerId, nameExists);

        TestUtils.assertThrowsWithMessage(
                DuplicateResourceException.class,
                "Project with this name already exists for this user",
                executable
        );
    }

    @Test
    void validateUniqueProjectNameForOwner_NameNotExists_NoException() {
        String projectName = "Unique";
        Long ownerId = 1L;
        boolean nameExists = false;

        assertDoesNotThrow(() ->
                ProjectValidator.validateUniqueProjectNameForOwner(projectName, ownerId, nameExists)
        );
    }

    @Test
    void validateProjectExists_Null_ThrowsException() {
        Project project = null;

        Executable executable = () -> ProjectValidator.validateProjectExists(project);

        TestUtils.assertThrowsWithMessage(
                IllegalArgumentException.class,
                "Project not found",
                executable
        );
    }

    @Test
    void validateProjectExists_NotNull_NoException() {
        Project project = new Project();
        project.setId(1L);

        assertDoesNotThrow(() -> ProjectValidator.validateProjectExists(project));
    }

    @Test
    void validateAccessToProject_PrivateNotParticipant_ThrowsException() {
        Project project = new Project();
        project.setVisibility(ProjectVisibility.PRIVATE);

        Team team = new Team();
        TeamMember member = new TeamMember();
        member.setId(2L);
        team.setTeamMembers(List.of(member));
        project.setTeams(List.of(team));

        Long userId = 3L;

        Executable executable = () -> ProjectValidator.validateAccessToProject(project, userId);

        TestUtils.assertThrowsWithMessage(
                IllegalArgumentException.class,
                "Access denied to private project",
                executable
        );
    }

    @Test
    void validateAccessToProject_PrivateParticipant_NoException() {
        Project project = new Project();
        project.setVisibility(ProjectVisibility.PRIVATE);

        TeamMember member = new TeamMember();
        member.setId(1L);

        Team team = new Team();
        team.setTeamMembers(List.of(member));

        project.setTeams(List.of(team));

        Long userId = 1L;

        assertDoesNotThrow(() -> ProjectValidator.validateAccessToProject(project, userId));
    }

    @Test
    void validateAccessToProject_Public_NoException() {
        Project project = new Project();
        project.setVisibility(ProjectVisibility.PUBLIC);

        assertDoesNotThrow(() -> ProjectValidator.validateAccessToProject(project, 99L));
    }

    @Test
    void validateUpdate_NothingToUpdate_ThrowsException() {
        Project project = new Project();
        project.setId(1L);

        Executable executable = () -> ProjectValidator.validateUpdate(project, null, null);

        TestUtils.assertThrowsWithMessage(
                IllegalArgumentException.class,
                "Nothing to update",
                executable
        );
    }

    @Test
    void validateUpdate_ProjectNotFound_ThrowsException() {
        Project project = null;

        Executable executable = () -> ProjectValidator.validateUpdate(project, ProjectStatus.CANCELLED, "desc");

        TestUtils.assertThrowsWithMessage(
                IllegalArgumentException.class,
                "Project not found",
                executable
        );
    }

    @Test
    void validateUpdate_Valid_NoException() {
        Project project = new Project();
        project.setId(1L);

        assertDoesNotThrow(() ->
                ProjectValidator.validateUpdate(project, ProjectStatus.CANCELLED, "Updated")
        );
    }
}