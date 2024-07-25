package faang.school.projectservice.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.resource.ResourceService;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.service.teamMember.TeamMemberService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static faang.school.projectservice.resource.ResourceMock.minioKey;
import static faang.school.projectservice.resource.ResourceMock.resourceUrl;
import static faang.school.projectservice.util.ProjectMock.generateProject;
import static faang.school.projectservice.util.ProjectMock.twoGB;
import static faang.school.projectservice.resource.ResourceMock.fileSize;
import static faang.school.projectservice.resource.ResourceMock.generateMultipartFile;
import static faang.school.projectservice.resource.ResourceMock.generateResource;
import static faang.school.projectservice.resource.ResourceMock.generateTeamMember;
import static faang.school.projectservice.resource.ResourceMock.projectId;
import static faang.school.projectservice.resource.ResourceMock.resourceId;
import static faang.school.projectservice.resource.ResourceMock.resourceName;
import static faang.school.projectservice.resource.ResourceMock.teamMemberId;
import static faang.school.projectservice.resource.ResourceMock.userId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {

    @Mock
    private TeamMemberService teamMemberService;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectService projectService;
    @Mock
    private ResourceRepository repository;
    @Mock
    private S3Service s3Service;
    @Spy
    private ResourceMapper mapper = Mappers.getMapper(ResourceMapper.class);

    @InjectMocks
    private ResourceService resourceService;

    @Test
    @DisplayName("Get resource returns resource URL")
    public void getResourceSuccess() {
        // Arrange
        when(repository.findById(resourceId)).thenReturn(Optional.of(generateResource()));
        when(s3Service.isObjectExist(minioKey)).thenReturn(true);
        when(s3Service.getFileUrl(minioKey)).thenReturn(resourceUrl);

        // Act
        String actual = resourceService.getResource(resourceId);

        // Assert
        verify(repository, times(1)).findById(resourceId);
        assertEquals(resourceUrl, actual);
    }

    @Test
    @DisplayName("Get resource should fail if the resource is 404")
    public void getResourceNotFound() {
        // Arrange
        when(repository.findById(resourceId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> resourceService.getResource(resourceId));
    }

    @Test
    @DisplayName("Get resource should fail if the resource is deleted")
    public void getResourceDeleted() {
        // Arrange
        when(repository.findById(resourceId)).thenReturn(Optional.of(generateResource(ResourceStatus.DELETED)));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> resourceService.getResource(resourceId));
    }

    @Test
    @DisplayName("Create resource returns Resource DTO")
    public void createResourceSuccess() {
        // Arrange
        Project generatedProject = generateProject(projectId);
        when(projectService.getOneOrThrow(projectId)).thenReturn(generatedProject);
        when(teamMemberService.getOneByUserIdAndProjectIdOrThrow(teamMemberId, projectId)).thenReturn(generateTeamMember());
        when(repository.save(any(Resource.class))).thenReturn(generateResource());
        when(projectRepository.save(any(Project.class))).thenReturn(generateProject(projectId, fileSize));

        // Act
        ResourceDto actual = resourceService.createResource(teamMemberId, projectId, generateMultipartFile());

        // Assert
        verify(repository, times(1)).save(any(Resource.class));
        verify(projectRepository, times(1)).save(any(Project.class));
        assertEquals(resourceId, actual.id());
        assertEquals(resourceName, actual.name());
        assertEquals(fileSize, actual.size());
        assertEquals(projectId, actual.projectId());
        assertEquals(fileSize, generatedProject.getStorageSize());
    }

    @Test
    @DisplayName("Create resource should throw an error if there is no project")
    public void createResourceThrowsErrorIfProjectNotFound() {
        // Arrange
        when(projectService.getOneOrThrow(projectId)).thenThrow(EntityNotFoundException.class);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> resourceService.createResource(teamMemberId, projectId, generateMultipartFile()));
    }

    @Test
    @DisplayName("Create resource should throw an error if there is no team member")
    public void createResourceThrowsErrorIfTeamMemberNotFound() {
        // Arrange
        when(projectService.getOneOrThrow(projectId)).thenReturn(generateProject(projectId));
        when(teamMemberService.getOneByUserIdAndProjectIdOrThrow(teamMemberId, projectId)).thenThrow(EntityNotFoundException.class);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> resourceService.createResource(teamMemberId, projectId, generateMultipartFile()));
    }

    @Test
    @DisplayName("Create resource should throw an error if the file size exceeds the project max storage size")
    public void createResourceThrowsErrorIfExceedsProjectMaxStorageSize() {
        // Arrange
        when(projectService.getOneOrThrow(projectId)).thenReturn(generateProject(projectId, twoGB));
        when(teamMemberService.getOneByUserIdAndProjectIdOrThrow(teamMemberId, projectId)).thenReturn(generateTeamMember());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> resourceService.createResource(teamMemberId, projectId, generateMultipartFile()));
    }

    @Test
    @DisplayName("Update resource returns Resource DTO")
    public void updateResourceSuccess() {
        // Arrange
        String newFileName = "changedAvatar.jpg";
        BigInteger newFileSize = BigInteger.valueOf(20000);

        Project generatedProject = generateProject(projectId, fileSize);
        when(repository.findById(resourceId)).thenReturn(Optional.of(generateResource()));
        when(projectService.getOneOrThrow(projectId)).thenReturn(generatedProject);
        when(teamMemberService.getOneByUserIdAndProjectIdOrThrow(teamMemberId, projectId)).thenReturn(generateTeamMember());
        when(repository.save(any(Resource.class))).thenReturn(generateResource(newFileName, newFileSize));
        when(projectRepository.save(any(Project.class))).thenReturn(generateProject(projectId, newFileSize));

        // Act
        ResourceDto actual = resourceService.updateResource(teamMemberId, resourceId, generateMultipartFile(newFileName, newFileSize));

        // Assert
        verify(repository, times(1)).save(any(Resource.class));
        verify(projectRepository, times(1)).save(any(Project.class));
        assertEquals(newFileName, actual.name());
        assertEquals(newFileSize, actual.size());
        assertEquals(newFileSize, generatedProject.getStorageSize());
    }

    @Test
    @DisplayName("Update resource should throw an error if the file size exceeds the project max storage size")
    public void updateResourceThrowsErrorIfExceedsProjectMaxStorageSize() {
        // Arrange
        when(repository.findById(resourceId)).thenReturn(Optional.of(generateResource()));
        when(projectService.getOneOrThrow(projectId)).thenReturn(generateProject(projectId, twoGB));
        when(teamMemberService.getOneByUserIdAndProjectIdOrThrow(teamMemberId, projectId)).thenReturn(generateTeamMember());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> resourceService.updateResource(teamMemberId, resourceId, generateMultipartFile(resourceName, BigInteger.valueOf(50000))));
    }

    @Test
    @DisplayName("Update resource should fail if the resource is deleted")
    public void updateResourceDeleted() {
        // Arrange
        when(repository.findById(resourceId)).thenReturn(Optional.of(generateResource(ResourceStatus.DELETED)));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> resourceService.updateResource(teamMemberId, resourceId, generateMultipartFile()));
    }

    @Test
    @DisplayName("Delete resource success path")
    public void deleteResourceSuccess() {
        // Arrange
        Project generatedProject = generateProject(projectId, fileSize);
        Resource generatedResource = generateResource();
        when(repository.findById(resourceId)).thenReturn(Optional.of(generatedResource));
        when(projectService.getOneOrThrow(projectId)).thenReturn(generatedProject);
        when(teamMemberService.getOneByUserIdAndProjectIdOrThrow(teamMemberId, projectId)).thenReturn(generateTeamMember());
        when(repository.save(any(Resource.class))).thenReturn(generateResource(resourceName, null));
        when(projectRepository.save(any(Project.class))).thenReturn(generateProject(projectId, BigInteger.ZERO));

        // Act
        resourceService.deleteResource(teamMemberId, resourceId);

        // Assert
        verify(repository, times(1)).save(any(Resource.class));
        verify(projectRepository, times(1)).save(any(Project.class));
        assertNull(generatedResource.getKey());
        assertNull(generatedResource.getSize());
        assertEquals(ResourceStatus.DELETED, generatedResource.getStatus());
        assertEquals(BigInteger.ZERO, generatedProject.getStorageSize());
    }

    @Test
    @DisplayName("Delete resource success if team member is a manager")
    public void deleteResourceSuccessTeamMemberIsManager() {
        // Arrange
        long managerTeamMemberId = 2;

        Project generatedProject = generateProject(projectId, fileSize);
        Resource generatedResource = generateResource();
        when(repository.findById(resourceId)).thenReturn(Optional.of(generatedResource));
        when(projectService.getOneOrThrow(projectId)).thenReturn(generatedProject);
        when(teamMemberService.getOneByUserIdAndProjectIdOrThrow(managerTeamMemberId, projectId)).thenReturn(generateTeamMember(managerTeamMemberId, userId, List.of(TeamRole.MANAGER)));
        when(repository.save(any(Resource.class))).thenReturn(generateResource(resourceName, null));
        when(projectRepository.save(any(Project.class))).thenReturn(generateProject(projectId, BigInteger.ZERO));

        // Act
        resourceService.deleteResource(managerTeamMemberId, resourceId);

        // Assert
        verify(repository, times(1)).save(any(Resource.class));
    }

    @Test
    @DisplayName("Delete resource should fail if the user does not have enough rights")
    public void deleteResourceThrowsErrorIfNotEnoughRights() {
        // Arrange
        long teamMemberIdWithNoRights = 2;

        Project generatedProject = generateProject(projectId, fileSize);
        Resource generatedResource = generateResource();
        when(repository.findById(resourceId)).thenReturn(Optional.of(generatedResource));
        when(projectService.getOneOrThrow(projectId)).thenReturn(generatedProject);
        when(teamMemberService.getOneByUserIdAndProjectIdOrThrow(teamMemberIdWithNoRights, projectId)).thenReturn(generateTeamMember(teamMemberIdWithNoRights));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> resourceService.deleteResource(teamMemberIdWithNoRights, resourceId));
    }

    @Test
    @DisplayName("Delete resource should not delete resource from DB if it's already deleted")
    public void deleteResourceNotDeleted() {
        // Arrange
        when(repository.findById(resourceId)).thenReturn(Optional.of(generateResource(ResourceStatus.DELETED)));

        // Act
        resourceService.deleteResource(teamMemberId, resourceId);

        // Assert
        verify(repository, times(0)).save(any(Resource.class));
    }
}