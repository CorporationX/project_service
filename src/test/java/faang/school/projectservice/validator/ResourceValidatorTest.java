package faang.school.projectservice.validator;

import faang.school.projectservice.exception.EmptyResourceException;
import faang.school.projectservice.exception.InsufficientStorageException;
import faang.school.projectservice.exception.InvalidAccessException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceValidatorTest {

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private ResourceValidator resourceValidator;

    private MockMultipartFile file;
    private TeamMember teamMember;
    private Resource resource;

    @BeforeEach
    void setUp() {
        teamMember = createMockTeamMember();
        resource = createMockResource();
    }

    @Test
    @DisplayName("Validate Resource is not empty: success")
    void validateResourceNotEmpty_FileWithContent_Success() {
        file = new MockMultipartFile(
                "file",
                "text.txt",
                "text/plain",
                "content".getBytes());

        assertDoesNotThrow(() -> resourceValidator.validateResourceNotEmpty(file));
    }

    @Test
    @DisplayName("Validate Resource is empty: fail")
    void validateResourceNotEmpty_FileWithoutContent_Fail() {
        file = new MockMultipartFile(
                "file",
                "text.txt",
                "text/plain",
                "".getBytes());

        Exception ex = assertThrows(EmptyResourceException.class, () -> resourceValidator.validateResourceNotEmpty(file));
        assertEquals(String.format("File %s is empty. It cannot be uploaded", file.getName()), ex.getMessage());
    }

    @Test
    @DisplayName("Validate enough space in storage: success")
    void validateEnoughSpaceInStorage_EnoughSpace_Success() {
        file = new MockMultipartFile(
                "file",
                "text.txt",
                "text/plain",
                "content".getBytes());

        Project project = Project.builder().
                storageSize(BigInteger.valueOf(10)).
                maxStorageSize(BigInteger.valueOf(100)).
                build();

        assertDoesNotThrow(() -> resourceValidator.validateEnoughSpaceInStorage(project, file));
    }

    @Test
    @DisplayName("Validate space in storage: available space is not enough: success")
    void validateEnoughSpaceInStorage_NotEnoughAvailableSpace_Fail() {
        file = new MockMultipartFile(
                "file",
                "text.txt",
                "text/plain",
                "content".getBytes());

        Project project = Project.builder().
                storageSize(BigInteger.valueOf(8)).
                maxStorageSize(BigInteger.valueOf(10)).
                build();

        Exception ex = assertThrows(InsufficientStorageException.class, () -> resourceValidator.validateEnoughSpaceInStorage(project, file));
        assertEquals("Not enough space to store files", ex.getMessage());
    }

    @Test
    @DisplayName("Validate resource exists by id: success")
    void validateResourceExistsById_ValidId_Success() {
        when(resourceRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> resourceValidator.validateResourceExistsById(1L));
    }

    @Test
    @DisplayName("Validate resource exists by id: fail")
    void validateResourceExistsById_InvalidId_Fail() {
        when(resourceRepository.existsById(1L)).thenReturn(false);

        Exception ex = assertThrows(EntityNotFoundException.class, () -> resourceValidator.validateResourceExistsById(1L));
        assertEquals("Resource not found, id: 1", ex.getMessage());
    }

    @Test
    @DisplayName("Validate team member has permissions to modify resource success: valid user id")
    void validateTeamMemberHasPermissionsToModifyResource_ValidUserId_Success() {
        teamMember.setRoles(List.of(TeamRole.OWNER));

        resourceValidator.validateTeamMemberHasPermissionsToModifyResource(teamMember, resource);

        assertDoesNotThrow(() -> resourceValidator.validateTeamMemberHasPermissionsToModifyResource(teamMember, resource));
    }

    @Test
    @DisplayName("Validate team member has permissions to modify resource success: valid user role")
    void validateTeamMemberHasPermissionsToModifyResource_ValidRole_IdSuccess() {
        teamMember.setUserId(10L);
        teamMember.setRoles(List.of(TeamRole.MANAGER));

        resourceValidator.validateTeamMemberHasPermissionsToModifyResource(teamMember, resource);

        assertDoesNotThrow(() -> resourceValidator.validateTeamMemberHasPermissionsToModifyResource(teamMember, resource));
    }

    @Test
    @DisplayName("Validate team member has permission to modify resource fail: invalid user id and role")
    void validateTeamMemberHasPermissionToModifyResource_InvalidUserId_Fail() {
        teamMember.setUserId(3L);
        teamMember.setRoles(List.of(TeamRole.DEVELOPER));

        RuntimeException ex = assertThrows(InvalidAccessException.class, () ->
                resourceValidator.validateTeamMemberHasPermissionsToModifyResource(teamMember, resource));
        assertEquals("User id: 3 doesn't have rights to modify file id: 1", ex.getMessage());
    }

    private TeamMember createMockTeamMember() {
        return TeamMember.builder()
                .id(1L)
                .userId(1L)
                .roles(List.of(TeamRole.OWNER))
                .build();
    }

    private Resource createMockResource() {
        return Resource.builder()
                .id(1L)
                .name("Test resource")
                .key("test-key")
                .size(BigInteger.valueOf(1024))
                .type(ResourceType.TEXT)
                .status(ResourceStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .createdBy(createMockTeamMember())
                .updatedAt(LocalDateTime.now())
                .updatedBy(createMockTeamMember())
                .build();
    }


}