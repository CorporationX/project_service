package faang.school.projectservice.validator.project;

import faang.school.projectservice.exception.project.AccessDeniedException;
import faang.school.projectservice.exception.project.BadRequestException;
import faang.school.projectservice.exception.project.DuplicateResourceException;
import faang.school.projectservice.exception.project.ResourceNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.List;

import static faang.school.projectservice.helpers.TestUtils.assertThrowsAny;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectValidatorTest {

    @Test
    void validateUniqueProjectNameForOwner_NameExists_ThrowsException() {
        Executable exec = () -> ProjectValidator.validateUniqueProjectNameForOwner(
                "TestProject",
                1L,
                () -> true);

        assertThrowsAny(DuplicateResourceException.class, exec);
    }

    @Test
    void validateUniqueProjectNameForOwner_NameNotExists_NoException() {
        assertDoesNotThrow(() ->
                ProjectValidator.validateUniqueProjectNameForOwner(
                        "UniqueProject",
                        1L,
                        () -> false));
    }

    @Test
    void validateProjectExists_Null_ThrowsException() {
        assertThrowsAny(ResourceNotFoundException.class,
                () -> ProjectValidator.validateProjectExists(null));
    }

    @Test
    void validateProjectExists_NotNull_NoException() {
        Project project = Project.builder().id(1L).build();
        assertDoesNotThrow(() -> ProjectValidator.validateProjectExists(project));
    }

    @Test
    void validateAccessToProject_PrivateNotParticipant_ThrowsException() {
        Project project = Project.builder()
                .visibility(ProjectVisibility.PRIVATE)
                .teams(List.of(
                        Team.builder()
                                .teamMembers(List.of(TeamMember.builder().id(2L).build()))
                                .build()))
                .build();

        assertThrowsAny(AccessDeniedException.class,
                () -> ProjectValidator.validateAccessToProject(project, 3L));
    }

    @Test
    void validateAccessToProject_PrivateParticipant_NoException() {
        Project project = Project.builder()
                .visibility(ProjectVisibility.PRIVATE)
                .teams(List.of(
                        Team.builder()
                                .teamMembers(List.of(TeamMember.builder().id(1L).build()))
                                .build()))
                .build();

        assertDoesNotThrow(() -> ProjectValidator.validateAccessToProject(project, 1L));
    }

    @Test
    void validateAccessToProject_Public_NoException() {
        Project project = Project.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        assertDoesNotThrow(() -> ProjectValidator.validateAccessToProject(project, 42L));
    }

    @Test
    void validateUpdate_NothingToUpdate_ThrowsException() {
        Project project = Project.builder().id(1L).build();
        assertThrowsAny(BadRequestException.class,
                () -> ProjectValidator.validateUpdate(project, null, null));
    }

    @Test
    void validateUpdate_ProjectNotFound_ThrowsException() {
        assertThrowsAny(ResourceNotFoundException.class,
                () -> ProjectValidator.validateUpdate(null, ProjectStatus.CREATED, "desc"));
    }

    @Test
    void validateUpdate_Valid_NoException() {
        Project project = Project.builder().id(1L).build();
        assertDoesNotThrow(() ->
                ProjectValidator.validateUpdate(project, ProjectStatus.COMPLETED, "desc"));
    }

    @Test
    void isUserParticipant_ReturnsTrue_WhenMemberExists() {
        Project project = Project.builder()
                .teams(List.of(
                        Team.builder()
                                .teamMembers(List.of(TeamMember.builder().id(5L).build()))
                                .build()))
                .build();

        assertTrue(ProjectValidator.isUserParticipant(project, 5L));
    }
}