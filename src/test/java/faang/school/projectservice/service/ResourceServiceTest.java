package faang.school.projectservice.service;

import faang.school.projectservice.dto.resource.ResourceResponseDto;
import faang.school.projectservice.dto.resource.ResourceUpdateDto;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.ConflictException;
import faang.school.projectservice.exception.NotFoundException;
import faang.school.projectservice.mapper.ResourceMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.service.utilservice.ProjectUtilService;
import faang.school.projectservice.service.utilservice.ResourceUtilService;
import faang.school.projectservice.service.utilservice.TeamMemberUtilService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @InjectMocks
    private ResourceService resourceService;

    @Spy
    private ResourceMapperImpl resourceMapper;

    @Mock
    private ResourceUtilService resourceUtilService;

    @Mock
    private TeamMemberUtilService teamMemberUtilService;

    @Mock
    private ProjectUtilService projectUtilService;

    @Mock
    private S3Service s3Service;


    @Test
    void testGetByIdAndProjectId() {
        long resourceId = 1L;
        long projectId = 11L;
        String resourceName = "qwe.jpg";
        Resource resource = Resource.builder()
                .id(resourceId)
                .name(resourceName)
                .project(Project.builder()
                        .id(projectId)
                        .build())
                .build();

        when(resourceUtilService.getByIdAndProjectId(resourceId, projectId))
                .thenReturn(resource);

        ResourceResponseDto responseDto = resourceService.getByIdAndProjectId(resourceId, projectId);

        assertEquals(resourceId, responseDto.getId());
        assertEquals(projectId, responseDto.getProjectId());
        assertEquals(resourceName, responseDto.getName());
        verify(resourceUtilService, times(1)).getByIdAndProjectId(resourceId, projectId);
    }

    @Test
    void testGetAllByProjectId() {
        long projectId = 11L;
        long resource1Id = 1L;
        long resource2Id = 2L;
        String resource1Name = "r1.jpg";
        String resource2Name = "r2.jpg";
        Resource resource1 = Resource.builder()
                .id(resource1Id)
                .name(resource1Name)
                .project(Project.builder().id(projectId).build())
                .build();
        Resource resource2 = Resource.builder()
                .id(resource2Id)
                .name(resource2Name)
                .project(Project.builder().id(projectId).build())
                .build();

        when(resourceUtilService.getAllByProjectId(projectId)).thenReturn(List.of(resource1, resource2));

        List<ResourceResponseDto> responseDtos = resourceService.getAllByProjectId(projectId);

        assertEquals(2, responseDtos.size());
        assertEquals(resource1Id, responseDtos.get(0).getId());
        assertEquals(resource2Id, responseDtos.get(1).getId());
        assertEquals(projectId, responseDtos.get(0).getProjectId());
        assertEquals(projectId, responseDtos.get(1).getProjectId());
        assertEquals(resource1Name, responseDtos.get(0).getName());
        assertEquals(resource2Name, responseDtos.get(1).getName());
        verify(resourceUtilService, times(1)).getAllByProjectId(projectId);
    }

    @Test
    void testUpdateMetadata_updaterIsCreator_saves() {
        long resourceId = 1L;
        long projectId = 11L;
        long updaterId = 21L;
        String newName = "qwe.jpg";
        List<TeamRole> newRoles = List.of(TeamRole.TESTER);
        ResourceUpdateDto updateDto = ResourceUpdateDto.builder()
                .name(newName)
                .newRoles(newRoles)
                .isActive(false)
                .build();
        TeamMember updater = TeamMember.builder().id(updaterId).build();
        Resource resource = Resource.builder()
                .id(resourceId)
                .project(Project.builder().id(projectId).build())
                .status(ResourceStatus.ACTIVE)
                .createdBy(updater)
                .build();

        when(resourceUtilService.getByIdAndProjectIdAndStatusNot(resourceId, projectId, ResourceStatus.DELETED))
                .thenReturn(resource);
        when(teamMemberUtilService.getByUserIdAndProjectId(updaterId, projectId))
                .thenReturn(updater);
        when(resourceUtilService.save(resource)).then(invocationOnMock -> invocationOnMock.getArgument(0));

        ResourceResponseDto responseDto = resourceService.updateMetadata(resourceId, projectId, updaterId, updateDto);

        assertEquals(resourceId, responseDto.getId());
        assertEquals(projectId, responseDto.getProjectId());
        assertEquals(updaterId, responseDto.getCreatorId());
        assertEquals(updaterId, responseDto.getUpdaterId());
        assertEquals(newName, responseDto.getName());
        assertEquals(ResourceStatus.INACTIVE, responseDto.getStatus());
        verify(resourceUtilService, times(1)).save(resource);
    }

    @Test
    void testUpdateMetadata_updaterIsManager_saves() {
        long resourceId = 1L;
        long projectId = 11L;
        long updaterId = 21L;
        long creatorId = 22L;
        String newName = "qwe.jpg";
        List<TeamRole> newRoles = List.of(TeamRole.TESTER);
        ResourceUpdateDto updateDto = ResourceUpdateDto.builder()
                .name(newName)
                .newRoles(newRoles)
                .isActive(false)
                .build();
        TeamMember updater = TeamMember.builder()
                .id(updaterId)
                .roles(List.of(TeamRole.MANAGER))
                .build();
        Resource resource = Resource.builder()
                .id(resourceId)
                .project(Project.builder().id(projectId).build())
                .status(ResourceStatus.ACTIVE)
                .createdBy(TeamMember.builder().id(creatorId).build())
                .build();

        when(resourceUtilService.getByIdAndProjectIdAndStatusNot(resourceId, projectId, ResourceStatus.DELETED))
                .thenReturn(resource);
        when(teamMemberUtilService.getByUserIdAndProjectId(updaterId, projectId))
                .thenReturn(updater);
        when(resourceUtilService.save(resource)).then(invocationOnMock -> invocationOnMock.getArgument(0));

        ResourceResponseDto responseDto = resourceService.updateMetadata(resourceId, projectId, updaterId, updateDto);

        assertEquals(resourceId, responseDto.getId());
        assertEquals(projectId, responseDto.getProjectId());
        assertEquals(creatorId, responseDto.getCreatorId());
        assertEquals(updaterId, responseDto.getUpdaterId());
        assertEquals(newName, responseDto.getName());
        assertEquals(ResourceStatus.INACTIVE, responseDto.getStatus());
        verify(resourceUtilService, times(1)).save(resource);
    }

    @Test
    void testUpdateMetadata_deletedResource_throws() {
        long resourceId = 1L;
        long projectId = 11L;
        long updaterId = 21L;
        ResourceUpdateDto updateDto = ResourceUpdateDto.builder().build();

        when(resourceUtilService.getByIdAndProjectIdAndStatusNot(resourceId, projectId, ResourceStatus.DELETED))
                .thenThrow(new NotFoundException(""));

        assertThrows(NotFoundException.class, () ->
                resourceService.updateMetadata(resourceId, projectId, updaterId, updateDto)
        );
    }

    @Test
    void testUpdateMetadata_updaterNotCreatorAndNotManager_throws() {
        long resourceId = 1L;
        long projectId = 11L;
        long updaterId = 21L;
        long creatorId = 22L;
        TeamMember updater = TeamMember.builder()
                .id(updaterId)
                .roles(List.of(TeamRole.TESTER))
                .build();
        ResourceUpdateDto updateDto = ResourceUpdateDto.builder().build();
        Resource resource = Resource.builder()
                .id(resourceId)
                .status(ResourceStatus.ACTIVE)
                .createdBy(TeamMember.builder().id(creatorId).build())
                .build();

        when(resourceUtilService.getByIdAndProjectIdAndStatusNot(resourceId, projectId, ResourceStatus.DELETED))
                .thenReturn(resource);
        when(teamMemberUtilService.getByUserIdAndProjectId(updaterId, projectId)).thenReturn(updater);

        assertThrows(AccessDeniedException.class, () ->
                resourceService.updateMetadata(resourceId, projectId, updaterId, updateDto)
        );
    }

    @Test
    void testUploadNew() {
        String origFileName = "qwe.jpg";
        int bytesSize = 255;
        MultipartFile file = new MockMultipartFile("file", origFileName, "image/jpeg", new byte[bytesSize]);
        long projectId = 11L;
        long userId = 1L;
        Project project = Project.builder()
                .id(projectId)
                .name("projectName")
                .storageSize(BigInteger.ZERO)
                .maxStorageSize(BigInteger.valueOf(2_147_483_648L))
                .build();
        TeamMember creator = TeamMember.builder()
                .id(userId)
                .roles(List.of(TeamRole.DEVELOPER))
                .build();

        when(projectUtilService.getById(projectId)).thenReturn(project);
        when(teamMemberUtilService.getByUserIdAndProjectId(userId, projectId)).thenReturn(creator);
        when(resourceUtilService.save(any(Resource.class))).then(invocationOnMock -> invocationOnMock.getArgument(0));

        ResourceResponseDto responseDto = resourceService.uploadNew(file, projectId, userId);

        assertEquals(origFileName, responseDto.getName());
        assertEquals(bytesSize, responseDto.getSize().intValue());
        assertEquals(ResourceType.IMAGE, responseDto.getType());
        assertEquals(ResourceStatus.ACTIVE, responseDto.getStatus());
        assertEquals(projectId, responseDto.getProjectId());
        assertEquals(userId, responseDto.getCreatorId());
        assertEquals(bytesSize, project.getStorageSize().intValue());
        verify(s3Service, times(1)).uploadFile(file, projectId + "_" + project.getName());
        verify(resourceUtilService, times(1)).save(any(Resource.class));
        verify(projectUtilService, times(1)).save(project);
    }

    @Test
    void testUploadNew_storageExceeded_throws() {
        String origFileName = "qwe.jpg";
        int bytesSize = 255;
        MultipartFile file = new MockMultipartFile("file", origFileName, "image/jpeg", new byte[bytesSize]);
        long projectId = 11L;
        long userId = 1L;
        Project project = Project.builder()
                .id(projectId)
                .name("projectName")
                .storageSize(BigInteger.valueOf(2_147_483_648L - bytesSize + 1))
                .maxStorageSize(BigInteger.valueOf(2_147_483_648L))
                .build();
        TeamMember creator = TeamMember.builder()
                .id(userId)
                .roles(List.of(TeamRole.DEVELOPER))
                .build();

        when(projectUtilService.getById(projectId)).thenReturn(project);
        when(teamMemberUtilService.getByUserIdAndProjectId(userId, projectId)).thenReturn(creator);

        assertThrows(ConflictException.class, () -> resourceService.uploadNew(file, projectId, userId));
    }

    @Test
    void testDownload_byCreator_success() {
        long resourceId = 1L;
        long projectId = 11L;
        long userId = 21L;
        TeamMember teamMember = TeamMember.builder()
                .id(userId)
                .roles(List.of(TeamRole.DEVELOPER))
                .build();
        Resource resource = Resource.builder()
                .id(resourceId)
                .key("key")
                .status(ResourceStatus.ACTIVE)
                .createdBy(teamMember)
                .allowedRoles(List.of(TeamRole.TESTER))
                .build();

        when(resourceUtilService.getByIdAndProjectIdAndStatusNot(resourceId, projectId, ResourceStatus.DELETED))
                .thenReturn(resource);
        when(teamMemberUtilService.getByUserIdAndProjectId(userId, projectId)).thenReturn(teamMember);

        resourceService.download(resourceId, projectId, userId);

        verify(resourceUtilService, times(1)).getByIdAndProjectIdAndStatusNot(resourceId, projectId, ResourceStatus.DELETED);
        verify(teamMemberUtilService, times(1)).getByUserIdAndProjectId(userId, projectId);
        verify(s3Service, times(1)).download("key");
    }

    @Test
    void testDownload_byManager_success() {
        long resourceId = 1L;
        long projectId = 11L;
        long userId = 21L;
        TeamMember teamMember = TeamMember.builder()
                .id(userId)
                .roles(List.of(TeamRole.MANAGER))
                .build();
        TeamMember creator = TeamMember.builder().id(22L).build();
        Resource resource = Resource.builder()
                .id(resourceId)
                .key("key")
                .status(ResourceStatus.ACTIVE)
                .createdBy(creator)
                .allowedRoles(List.of(TeamRole.TESTER))
                .build();

        when(resourceUtilService.getByIdAndProjectIdAndStatusNot(resourceId, projectId, ResourceStatus.DELETED))
                .thenReturn(resource);
        when(teamMemberUtilService.getByUserIdAndProjectId(userId, projectId)).thenReturn(teamMember);

        resourceService.download(resourceId, projectId, userId);

        verify(resourceUtilService, times(1)).getByIdAndProjectIdAndStatusNot(resourceId, projectId, ResourceStatus.DELETED);
        verify(teamMemberUtilService, times(1)).getByUserIdAndProjectId(userId, projectId);
        verify(s3Service, times(1)).download("key");
    }

    @Test
    void testDownload_byAllowedRole_success() {
        long resourceId = 1L;
        long projectId = 11L;
        long userId = 21L;
        TeamMember teamMember = TeamMember.builder()
                .id(userId)
                .roles(List.of(TeamRole.TESTER))
                .build();
        TeamMember creator = TeamMember.builder().id(22L).build();
        Resource resource = Resource.builder()
                .id(resourceId)
                .key("key")
                .status(ResourceStatus.ACTIVE)
                .createdBy(creator)
                .allowedRoles(List.of(TeamRole.TESTER))
                .build();

        when(resourceUtilService.getByIdAndProjectIdAndStatusNot(resourceId, projectId, ResourceStatus.DELETED))
                .thenReturn(resource);
        when(teamMemberUtilService.getByUserIdAndProjectId(userId, projectId)).thenReturn(teamMember);

        resourceService.download(resourceId, projectId, userId);

        verify(resourceUtilService, times(1)).getByIdAndProjectIdAndStatusNot(resourceId, projectId, ResourceStatus.DELETED);
        verify(teamMemberUtilService, times(1)).getByUserIdAndProjectId(userId, projectId);
        verify(s3Service, times(1)).download("key");
    }

    @Test
    void testDownload_byNotAllowedRoleNotManagerNotCreator_throws() {
        long resourceId = 1L;
        long projectId = 11L;
        long userId = 21L;
        TeamMember teamMember = TeamMember.builder()
                .id(userId)
                .roles(List.of(TeamRole.DEVELOPER))
                .build();
        TeamMember creator = TeamMember.builder().id(22L).build();
        Resource resource = Resource.builder()
                .id(resourceId)
                .key("key")
                .status(ResourceStatus.ACTIVE)
                .createdBy(creator)
                .allowedRoles(List.of(TeamRole.TESTER))
                .build();

        when(resourceUtilService.getByIdAndProjectIdAndStatusNot(resourceId, projectId, ResourceStatus.DELETED))
                .thenReturn(resource);
        when(teamMemberUtilService.getByUserIdAndProjectId(userId, projectId)).thenReturn(teamMember);

        assertThrows(AccessDeniedException.class, () -> resourceService.download(resourceId, projectId, userId));
    }

    @Test
    void testDownload_deleted_throws() {
        long resourceId = 1L;
        long projectId = 11L;
        long userId = 21L;

        when(resourceUtilService.getByIdAndProjectIdAndStatusNot(resourceId, projectId, ResourceStatus.DELETED))
                .thenThrow(new NotFoundException(""));

        assertThrows(NotFoundException.class, () -> resourceService.download(resourceId, projectId, userId));
    }
}