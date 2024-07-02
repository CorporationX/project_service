package faang.school.projectservice.validator.resource;

import faang.school.projectservice.exception.NoAccessException;
import faang.school.projectservice.exception.NoAccessExceptionMessage;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.testData.resouce.ResourceServiceTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class TeamMemberResourceValidatorTest {
    @InjectMocks
    private TeamMemberResourceValidator teamMemberResourceValidator;

    private TeamMember teamMemberUploader;
    private TeamMember teamMemberFromAnotherProject;
    private TeamMember teamMemberFromSameProject;

    private Resource resource;

    @BeforeEach
    void init() {
        ResourceServiceTestData resourceServiceTestData = new ResourceServiceTestData();
        resourceServiceTestData.getProject();
        resourceServiceTestData.getAnotherProject();
        teamMemberUploader = resourceServiceTestData.getTeamMember();
        teamMemberFromAnotherProject = resourceServiceTestData.getTeamMemberFromAnotherProject();
        teamMemberFromSameProject = resourceServiceTestData.getTeamMemberFromSameProject();
        resource = resourceServiceTestData.getResource();
    }

    @Nested
    class PositiveTests {
        @Test
        void testValidateDownloadFilePermissionSuccess() {
            teamMemberResourceValidator.validateDownloadFilePermission(teamMemberUploader, resource);

            assertNotNull(teamMemberUploader);
            assertEquals(resource.getProject(), teamMemberUploader.getTeam().getProject());
        }

        @Test
        void testValidateDeleteFilePermissionForUpdaterSuccess() {
            teamMemberResourceValidator.validateDeleteFilePermission(teamMemberUploader, resource);

            assertNotNull(teamMemberUploader);
            assertEquals(resource.getCreatedBy(), teamMemberUploader);
        }

        @Test
        void testValidateDeleteFilePermissionForOwnerSuccess() {
            teamMemberFromSameProject.setRoles(new ArrayList<>(List.of(TeamRole.OWNER)));
            teamMemberResourceValidator.validateDeleteFilePermission(teamMemberFromSameProject, resource);

            assertNotNull(teamMemberFromSameProject);
            assertEquals(teamMemberFromSameProject.getTeam().getProject(), teamMemberUploader.getTeam().getProject());
        }

        @Test
        void testValidateDeleteFilePermissionForManagerSuccess() {
            teamMemberFromSameProject.setRoles(new ArrayList<>(List.of(TeamRole.MANAGER)));
            teamMemberResourceValidator.validateDeleteFilePermission(teamMemberFromSameProject, resource);

            assertNotNull(teamMemberFromSameProject);
            assertEquals(teamMemberFromSameProject.getTeam().getProject(), teamMemberUploader.getTeam().getProject());
        }
    }

    @Nested
    class NegativeTests {
        @Test
        void testValidateDownloadFilePermission_TeamMemberFromAnotherProject_ThrowsNoAccessExceptionWithCorrectErrorMessage() {
            NoAccessException ex = assertThrows(NoAccessException.class,
                    () -> teamMemberResourceValidator.validateDownloadFilePermission(teamMemberFromAnotherProject, resource));

            assertEquals(NoAccessExceptionMessage.DOWNLOAD_PERMISSION_ERROR.getMessage(), ex.getMessage());
        }

        @Test
        void testValidateDeleteFilePermission_TeamMemberFromSameProject_RoleIntern_ThrowsNoAccessExceptionWithCorrectErrorMessage() {
            teamMemberFromSameProject.setRoles(new ArrayList<>(List.of(TeamRole.INTERN)));
            NoAccessException ex = assertThrows(NoAccessException.class,
                    () -> teamMemberResourceValidator.validateDeleteFilePermission(teamMemberFromSameProject, resource));
            assertEquals(NoAccessExceptionMessage.DELETE_PERMISSION_ERROR.getMessage(), ex.getMessage());
        }

        @Test
        void testValidateDeleteFilePermission_TeamMemberFromAnotherProject_RoleOwner_ThrowsNoAccessException() {
            teamMemberFromAnotherProject.setRoles(new ArrayList<>(List.of(TeamRole.OWNER)));
            assertThrows(NoAccessException.class,
                    () -> teamMemberResourceValidator.validateDeleteFilePermission(teamMemberFromAnotherProject, resource));
        }

        @Test
        void testValidateDeleteFilePermission_TeamMemberFromAnotherProject_RoleManager_ThrowsNoAccessException() {
            teamMemberFromAnotherProject.setRoles(new ArrayList<>(List.of(TeamRole.MANAGER)));
            assertThrows(NoAccessException.class,
                    () -> teamMemberResourceValidator.validateDeleteFilePermission(teamMemberFromAnotherProject, resource));
        }

        @Test
        void testValidateDeleteFilePermission_TeamMemberFromAnotherProject_SameAsUploader_ThrowsNoAccessException() {
            teamMemberFromAnotherProject.setRoles(teamMemberUploader.getRoles());
            assertThrows(NoAccessException.class,
                    () -> teamMemberResourceValidator.validateDeleteFilePermission(teamMemberFromAnotherProject, resource));
        }
    }
}