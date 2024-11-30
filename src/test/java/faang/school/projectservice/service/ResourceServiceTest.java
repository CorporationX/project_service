package faang.school.projectservice.service;

import faang.school.projectservice.dto.resource.ResourceResponseDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapperImpl;
import faang.school.projectservice.model.*;
import faang.school.projectservice.validator.ProjectValidator;
import faang.school.projectservice.validator.ResourceValidator;
import faang.school.projectservice.validator.TeamMemberValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {
    @Mock
    private ResourceRepository resourceRepository;

    @Spy
    private ResourceMapperImpl resourceMapper;

    @Mock
    private ResourceValidator resourceValidator;

    @Mock
    private TeamMemberValidator teamMemberValidator;

    @Mock
    private ProjectValidator projectValidator;

    @Mock
    private TeamMemberService teamMemberService;

    @Mock
    private StorageService storageService;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ResourceService resourceService;

    private Long userId;
    private Long projectId;
    private Project project;
    private MockMultipartFile file;
    private Resource resource;
    private TeamMember teamMember = createMockTeamMember();

    @BeforeEach
    void setUp() {
        userId = 1L;
        projectId = 1L;
        project = createMockProject();
        file = createMockMultipartFile();
        resource = createMockResource();
        teamMember = createMockTeamMember();
    }

    @Test
    @DisplayName("Upload file with all valid parameters: success")
    void uploadResource_ValidParameters_Success() {

        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(teamMemberService.getTeamMemberByUserId(userId)).thenReturn(teamMember);
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);

        ResourceResponseDto result = resourceService.uploadResource(projectId, userId, file);

        verify(projectValidator, times(1)).validateProjectExistsById(projectId);
        verify(teamMemberValidator, times(1)).validateTeamMemberExistsById(userId);
        verify(resourceValidator, times(1)).validateResourceNotEmpty(file);
        verify(projectValidator, times(1)).validateUserInProjectTeam(userId, project);
        verify(resourceValidator, times(1)).validateEnoughSpaceInStorage(project, file);
        verify(storageService, times(1)).uploadResourceAsync(any(MultipartFile.class), anyString());
        verify(projectService, times(1)).increaseOccupiedStorageSize(project, file);

        assertNotNull(result);
        assertEquals(resource.getId(), result.getId());
        assertEquals(resource.getName(), result.getName());
        assertEquals(resource.getKey(), result.getKey());
        assertEquals(resource.getSize(), result.getSize());
        assertEquals(resource.getType(), result.getType());
        assertEquals(resource.getCreatedAt(), result.getCreatedAt());
        assertEquals(resource.getCreatedBy().getUserId(), result.getCreatedById());
        assertEquals(resource.getUpdatedAt(), result.getUpdatedAt());
        assertEquals(resource.getUpdatedBy().getUserId(), result.getUpdatedById());
    }

    @Test
    @DisplayName("Upload file with invalid project id: fail")
    void uploadResource_InvalidProjectId_Fail() {

        doThrow(new EntityNotFoundException(String.format("Project with id %d doesn't exist", projectId)))
                .when(projectValidator).validateProjectExistsById(projectId);

        RuntimeException ex = assertThrows(EntityNotFoundException.class, () -> resourceService.uploadResource(projectId, userId, file));
        assertEquals(String.format("Project with id %d doesn't exist", projectId), ex.getMessage());

        verify(teamMemberValidator, never()).validateTeamMemberExistsById(userId);
    }

    @Test
    @DisplayName("Upload file with invalid user id: fail")
    void uploadResource_InvalidUserId_Fail() {

        doThrow(new EntityNotFoundException(String.format("Team member not found, id: %d", userId)))
                .when(teamMemberValidator).validateTeamMemberExistsById(userId);

        RuntimeException ex = assertThrows(EntityNotFoundException.class, () -> resourceService.uploadResource(projectId, userId, file));
        assertEquals(String.format("Team member not found, id: %d", userId), ex.getMessage());

        verify(projectValidator, times(1)).validateProjectExistsById(projectId);
        verify(resourceValidator, never()).validateResourceNotEmpty(file);
    }

    @Test
    @DisplayName("Delete file with all valid parameters: success")
    void deleteResource_ValidParameters_Success() {

        when(teamMemberService.getTeamMemberByUserId(userId)).thenReturn(teamMember);
        when(resourceRepository.getReferenceById(anyLong())).thenReturn(resource);
        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(teamMemberService.getTeamMemberByUserId(userId)).thenReturn(teamMember);
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);

        resourceService.deleteResource(projectId, resource.getId(), userId);

        ArgumentCaptor<Resource> resourceCaptor = ArgumentCaptor.forClass(Resource.class);

        verify(projectValidator, times(1)).validateProjectExistsById(projectId);
        verify(resourceValidator, times(1)).validateResourceExistsById(resource.getId());
        verify(teamMemberValidator, times(1)).validateTeamMemberExistsById(teamMember.getUserId());
        verify(projectValidator, times(1)).validateUserInProjectTeam(userId, project);
        verify(resourceValidator, times(1)).validateTeamMemberHasPermissionsToModifyResource(any(TeamMember.class), any(Resource.class));
        verify(storageService, times(1)).deleteResource(anyString());
        verify(projectService, times(1)).decreaseOccupiedStorageSize(any(Project.class), any(BigInteger.class));
        verify(resourceRepository, times(1)).save(resourceCaptor.capture());

        assertEquals(resource.getId(), resourceCaptor.getValue().getId());
        assertEquals(ResourceStatus.DELETED, resourceCaptor.getValue().getStatus());
    }

    @Test
    @DisplayName("Delete file fail: invalid project id")
    void deleteResource_InvalidProjectId_Fail() {

        doThrow(new EntityNotFoundException(String.format("Project with id %d doesn't exist", projectId)))
                .when(projectValidator).validateProjectExistsById(projectId);

        RuntimeException ex = assertThrows(EntityNotFoundException.class, () -> resourceService.deleteResource(projectId, resource.getId(), userId));
        assertEquals(String.format("Project with id %d doesn't exist", projectId), ex.getMessage());

        verify(resourceValidator, never()).validateResourceExistsById(resource.getId());
    }

    @Test
    @DisplayName("Delete file fail: invalid resource id")
    void deleteResource_InvalidResourceId_Fail() {
        Long resourceId = 1L;

        doThrow(new EntityNotFoundException(String.format("Resource not found, id: %d", resourceId)))
                .when(resourceValidator).validateResourceExistsById(resourceId);

        RuntimeException ex = assertThrows(EntityNotFoundException.class, () -> resourceService.deleteResource(projectId, resource.getId(), userId));
        assertEquals(String.format("Resource not found, id: %d", resourceId), ex.getMessage());

        verify(projectValidator, times(1)).validateProjectExistsById(projectId);
        verify(teamMemberValidator, never()).validateTeamMemberExistsById(teamMember.getUserId());
    }

    @Test
    @DisplayName("Delete file fail: invalid user id")
    void deleteResource_InvalidUserId_Fail() {

        doThrow(new EntityNotFoundException(String.format("Team member not found, id: %d", userId)))
                .when(teamMemberValidator).validateTeamMemberExistsById(userId);

        RuntimeException ex = assertThrows(EntityNotFoundException.class, () -> resourceService.deleteResource(projectId, resource.getId(), userId));
        assertEquals(String.format("Team member not found, id: %d", userId), ex.getMessage());

        verify(projectValidator, times(1)).validateProjectExistsById(projectId);
        verify(resourceValidator, times(1)).validateResourceExistsById(anyLong());
        verify(projectValidator, never()).validateUserInProjectTeam(userId, project);
    }

    @Test
    @DisplayName("Update file with all valid parameters: success")
    void updateResource_ValidParameters_Success() {
        Long resourceId = 1L;
        project.setStorageSize(BigInteger.valueOf(100L));

        when(teamMemberService.getTeamMemberByUserId(userId)).thenReturn(teamMember);
        when(resourceRepository.getReferenceById(resourceId)).thenReturn(resource);
        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);

        ResourceResponseDto result = resourceService.updateResource(projectId, resourceId, userId, file);

        verify(projectValidator, times(1)).validateProjectExistsById(projectId);
        verify(resourceValidator, times(1)).validateResourceExistsById(resource.getId());
        verify(teamMemberValidator, times(1)).validateTeamMemberExistsById(teamMember.getUserId());
        verify(resourceValidator, times(1)).validateResourceNotEmpty(file);
        verify(projectValidator, times(1)).validateUserInProjectTeam(userId, project);
        verify(resourceValidator, times(1)).validateTeamMemberHasPermissionsToModifyResource(any(TeamMember.class), any(Resource.class));
        verify(storageService, times(1)).deleteResource(anyString());
        verify(resourceValidator, times(1)).validateEnoughSpaceInStorage(project, file);
        verify(storageService, times(1)).uploadResourceAsync(any(MultipartFile.class), anyString());
        verify(projectService, times(1)).increaseOccupiedStorageSize(any(Project.class), any(MultipartFile.class));
        verify(resourceRepository, times(1)).save(resource);

        assertNotNull(result);
        assertEquals(resource.getId(), result.getId());
        assertEquals(resource.getName(), result.getName());
        assertEquals(resource.getKey(), result.getKey());
        assertEquals(resource.getSize(), result.getSize());
        assertEquals(resource.getType(), result.getType());
        assertEquals(resource.getCreatedAt(), result.getCreatedAt());
        assertEquals(resource.getCreatedBy().getUserId(), result.getCreatedById());
        assertEquals(resource.getUpdatedAt(), result.getUpdatedAt());
        assertEquals(resource.getUpdatedBy().getUserId(), result.getUpdatedById());
    }

    @Test
    @DisplayName("Download file with all valid parameters: success")
    void downloadResource_ValidParameters_Success() {
        byte[] contentFile = "content".getBytes();

        when(resourceRepository.getReferenceById(anyLong())).thenReturn(resource);
        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(storageService.downloadResourceAsync(anyString())).thenReturn(contentFile);

        byte[] result = resourceService.downloadResource(projectId, resource.getId(), userId);

        verify(projectValidator, times(1)).validateProjectExistsById(projectId);
        verify(resourceValidator, times(1)).validateResourceExistsById(resource.getId());
        verify(teamMemberValidator, times(1)).validateTeamMemberExistsById(teamMember.getUserId());
        verify(projectValidator, times(1)).validateUserInProjectTeam(userId, project);
        verify(storageService, times(1)).downloadResourceAsync(anyString());

        assertNotNull(result);
        assertEquals(contentFile, result);
    }

    @Test
    @DisplayName("Download file fail: invalid project id")
    void downloadResource_InvalidProjectId_Fail() {

        doThrow(new EntityNotFoundException(String.format("Project with id %d doesn't exist", projectId)))
                .when(projectValidator).validateProjectExistsById(projectId);

        RuntimeException ex = assertThrows(EntityNotFoundException.class, () -> resourceService.deleteResource(projectId, resource.getId(), userId));
        assertEquals(String.format("Project with id %d doesn't exist", projectId), ex.getMessage());

        verify(resourceValidator, never()).validateResourceExistsById(resource.getId());
    }

    @Test
    @DisplayName("Download file fail: invalid resource id")
    void downloadResource_InvalidResourceId_Fail() {
        Long resourceId = 1L;

        doThrow(new EntityNotFoundException(String.format("Resource not found, id: %d", resourceId)))
                .when(resourceValidator).validateResourceExistsById(resourceId);

        RuntimeException ex = assertThrows(EntityNotFoundException.class, () -> resourceService.deleteResource(projectId, resource.getId(), userId));
        assertEquals(String.format("Resource not found, id: %d", resourceId), ex.getMessage());

        verify(projectValidator, times(1)).validateProjectExistsById(projectId);
        verify(teamMemberValidator, never()).validateTeamMemberExistsById(teamMember.getUserId());
    }

    @Test
    @DisplayName("Download file fail: invalid user id")
    void downloadResource_InvalidUserId_Fail() {

        doThrow(new EntityNotFoundException(String.format("Team member not found, id: %d", userId)))
                .when(teamMemberValidator).validateTeamMemberExistsById(userId);

        RuntimeException ex = assertThrows(EntityNotFoundException.class, () -> resourceService.deleteResource(projectId, resource.getId(), userId));
        assertEquals(String.format("Team member not found, id: %d", userId), ex.getMessage());

        verify(projectValidator, times(1)).validateProjectExistsById(projectId);
        verify(resourceValidator, times(1)).validateResourceExistsById(anyLong());
        verify(projectValidator, never()).validateUserInProjectTeam(userId, project);
    }

    private Project createMockProject() {
        return Project.builder()
                .id(1L)
                .name("Test project")
                .description("Test project description")
                .ownerId(1L)
                .status(null)
                .build();
    }

    private MockMultipartFile createMockMultipartFile() {
        return new MockMultipartFile(
                "file",
                "text.txt",
                "text/plain",
                "content".getBytes()
        );
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
                .createdBy(teamMember)
                .updatedAt(LocalDateTime.now())
                .updatedBy(teamMember)
                .build();
    }

    private TeamMember createMockTeamMember() {
        return TeamMember.builder()
                .id(1L)
                .userId(1L)
                .roles(List.of(TeamRole.OWNER))
                .build();
    }
}