package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ProjectStorageCapacityExceededException;
import faang.school.projectservice.exception.UserNorAccessRightDeleteException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ResourcesValidatorTest {

    @InjectMocks
    private ResourcesValidator resourcesValidator;

    private Resource resource;

    private Project project;

    private TeamMember teamMember;

    private final long userId1 = 1L;

    private final long userId2 = 2L;

    @BeforeEach
    void setUp() {
        project = Project
                .builder()
                .ownerId(1L)
                .build();

        teamMember = TeamMember
                .builder()
                .userId(1L)
                .build();

        resource = Resource
                .builder()
                .createdBy(teamMember)
                .build();
    }

    @Test
    void testCheckStorageCapacityThrowException() {
        assertThrows(ProjectStorageCapacityExceededException.class,
                () -> resourcesValidator.checkStorageCapacity(3_000_000_000L));
    }

    @Test
    void testCheckStorageCapacityDoesNotThrowException() {
        assertDoesNotThrow(() -> resourcesValidator.checkStorageCapacity(1_000_000_000L));
    }

    @Test
    void testCheckRightsToDeleteCheckResourceThrowException() {
        assertThrows(UserNorAccessRightDeleteException.class,
                () -> resourcesValidator.checkRightsToDelete(resource, project, userId2));
    }

    @Test
    void testCheckRightsToDeleteCheckResourceDoesNotThrowException() {
        assertDoesNotThrow(() -> resourcesValidator.checkRightsToDelete(resource, project, userId1));
    }

    @Test
    void testCheckRightsToDeleteCheckProjectThrowException() {
        assertThrows(UserNorAccessRightDeleteException.class,
                () -> resourcesValidator.checkRightsToDelete(resource, project, userId2));
    }

    @Test
    void testCheckRightsToDeleteCheckProjectDoesNotThrowException() {
        assertDoesNotThrow(() -> resourcesValidator.checkRightsToDelete(resource, project, userId1));
    }

    @Test
    void testCheckTeamMemberInProjectThrowException() {
        assertThrows(DataValidationException.class,
                () -> resourcesValidator.checkTeamMemberInProject(null));
    }

    @Test
    void testCheckTeamMemberInProjectDoesNotThrowException() {
        assertDoesNotThrow(() -> resourcesValidator.checkTeamMemberInProject(teamMember));
    }
}