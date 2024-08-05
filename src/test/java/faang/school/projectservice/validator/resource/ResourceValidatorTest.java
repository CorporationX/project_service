package faang.school.projectservice.validator.resource;

import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.StorageSizeException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.*;
import faang.school.projectservice.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceValidatorTest {
    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private ResourceValidator resourceValidator;


    @Test
    void testValidateStorageOverflow_ThrowsStorageSizeException() {
        Project project = new Project();
        project.setStorageSize(BigInteger.valueOf(1048576));
        project.setMaxStorageSize(BigInteger.valueOf(2097152));
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", new byte[1572864]);

        StorageSizeException exception = assertThrows(StorageSizeException.class, () ->
                resourceValidator.validateStorageOverflow(project, file));
        assertEquals("Storage is full! Needed 0,5 MB more", exception.getMessage());
    }

    @Test
    void testGetTeamMemberAndValidate_ThrowsAccessDeniedException() {
        long userId = 1L;
        Project project = new Project();
        Team team = new Team();
        TeamMember teamMember = new TeamMember();
        teamMember.setUserId(2L);
        team.setTeamMembers(List.of(teamMember));
        project.setTeams(List.of(team));


        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
                resourceValidator.getTeamMemberAndValidate(project, userId));
        assertEquals("User " + userId + " are not a team member of project " + project.getId(), exception.getMessage());
    }

    @Test
    void testValidateCanUserDownload_ThrowsAccessDeniedException() {
        Resource resource = new Resource();
        TeamMember teamMember = new TeamMember();
        teamMember.setRoles(List.of(TeamRole.ANALYST));
        resource.setAllowedRoles(List.of(TeamRole.OWNER));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
                resourceValidator.validateCanUserDownload(resource, teamMember));
        assertEquals("User " + teamMember.getUserId() + " has not access to resource " + resource.getId(), exception.getMessage());
    }

    @Test
    void testValidateIsUpdateRequired_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                resourceValidator.validateIsUpdateRequired(null, null));
        assertEquals("No update required", exception.getMessage());
    }

    @Test
    void testGetResourceAndValidate_ThrowsEntityNotFoundException() {
        Long resourceId = 1L;
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                resourceValidator.getResourceAndValidate(resourceId));
        assertEquals("Resource with id " + resourceId + " not found", exception.getMessage());
    }

    @Test
    void testGetResourceAndValidate_ThrowsEntityNotFoundException_WhenDeleted() {
        Long resourceId = 1L;
        Resource resource = new Resource();
        resource.setStatus(ResourceStatus.DELETED);
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                resourceValidator.getResourceAndValidate(resourceId));
        assertEquals("Resource with id " + resourceId + " was deleted", exception.getMessage());
    }

    @Test
    void testValidateCanUserRemoveOrUpdate_ThrowsAccessDeniedException() {
        TeamMember teamMember = new TeamMember();
        teamMember.setId(1L);
        teamMember.setRoles(List.of(TeamRole.ANALYST));
        Resource resource = new Resource();
        TeamMember teamMember2 = new TeamMember();
        teamMember2.setId(2L);
        resource.setCreatedBy(teamMember2);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
                resourceValidator.validateCanUserRemoveOrUpdate(teamMember, resource));
        assertEquals("A file can only be deleted by its creator or project manager", exception.getMessage());
    }

    @Test
    void testGetProjectAndValidate_ThrowsEntityNotFoundException() {
        Long projectId = 1L;
        Resource resource = new Resource();

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                resourceValidator.getProjectAndValidate(projectId, resource));
        assertEquals("Project " + projectId + " is null", exception.getMessage());
    }

    @Test
    void testGetProjectAndValidate_ThrowsIllegalArgumentException() {
        Long projectId = 1L;
        Project project = new Project();
        project.setId(2L);
        Resource resource = new Resource();
        resource.setProject(project);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                resourceValidator.getProjectAndValidate(projectId, resource));
        assertEquals("Resource " + resource.getId() + " is owned by another project", exception.getMessage());
    }

    @Test
    void testValidateIsFileExist_ThrowsRuntimeException() {
        String bucketName = "bucket";
        String path = "path";
        when(s3Service.doesObjectExist(bucketName, path)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                resourceValidator.validateIsFileExist(bucketName, path));
        assertEquals("File" + path + "not exist", exception.getMessage());
    }

    @Test
    void testValidateResourceStatus_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                resourceValidator.validateResourceStatus(ResourceStatus.DELETED));
        assertEquals("Status cannot be updated to DELETED", exception.getMessage());
    }
}
