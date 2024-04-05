package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.OutOfMemoryException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.service.S3.S3ServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {
    @Mock
    S3ServiceImpl s3Service;
    @Mock
    ResourceRepository resourceRepository;
    @Mock
    ProjectService projectService;
    @Mock
    ResourceMapper resourceMapper;
    @Mock
    UserServiceClient userServiceClient;
    @Mock
    TeamMemberJpaRepository teamMemberJpaRepository;
    @InjectMocks
    ResourceService resourceService;

    long projectId = 1;
    long userId = 1;
    MockMultipartFile file;
    byte[] content;
    Project firstProject;
    Project secondProject;
    Resource firstResource;
    Resource thirdResource;
    TeamMember firstTeamMember;
    TeamMember secondTeamMember;
    String folder;

    @BeforeEach
    void setUp() {

        firstProject = Project.builder()
                .id(1L)
                .name("firstProject")
                .description("string")
                .storageSize(BigInteger.valueOf(121212))
                .maxStorageSize(BigInteger.valueOf(2000000000))
                .ownerId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        secondProject = Project.builder()
                .id(1L)
                .name("firstProject")
                .description("string")
                .storageSize(BigInteger.valueOf(9212120000000L))
                .maxStorageSize(BigInteger.valueOf(2000000000))
                .ownerId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        firstTeamMember = TeamMember.builder()
                .team(new Team())
                .id(2L)
                .userId(userId)
                .build();

        firstResource = Resource.builder()
                .id(1L)
                .name("resource")
                .key("1firstProject/resource")
                .type(ResourceType.TEXT)
                .status(ResourceStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .project(firstProject)
                .size(BigInteger.valueOf(121212))
                .createdBy(firstTeamMember)
                .build();
        secondTeamMember = TeamMember.builder()
                .id(5L)
                .team(new Team())
                .build();

        thirdResource = Resource.builder()
                .id(1L)
                .name("resource")
                .key("1firstProject/resource")
                .type(ResourceType.TEXT)
                .status(ResourceStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .project(secondProject)
                .size(BigInteger.valueOf(121212))
                .createdBy(secondTeamMember)
                .build();

        content = "Mock file content".getBytes();
        file = new MockMultipartFile("file", "test.txt", "text/plain", content);

        folder = firstProject.getId() + firstProject.getName();

    }

    @Test
    public void testUploadResource_TeamMemberNotFound() {
        when(teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> resourceService.uploadResource(projectId, file, userId));
        verify(projectService, times(1)).getProjectById(projectId);
    }

    @Test
    public void testUploadResource_StorageSizeExceeded() {
        when(projectService.getProjectById(secondProject.getId())).thenReturn(secondProject);
        when(teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(firstTeamMember);
        assertThrows(OutOfMemoryException.class, () -> resourceService.uploadResource(projectId, file, userId));
    }

    @Test
    public void testUploadResource() {
        when(projectService.getProjectById(firstProject.getId())).thenReturn(firstProject);
        when(teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(firstTeamMember);
        when(s3Service.uploadFile(file, folder)).thenReturn(firstResource);
        when(resourceRepository.save(firstResource)).thenReturn(firstResource);
        when(projectService.save(firstProject)).thenReturn(firstProject);

        resourceService.uploadResource(firstProject.getId(), file, userId);

        verify(resourceMapper, times(1)).toDto(firstResource);
    }

    @Test
    public void testDownLoadResource_ResourceNotFound() {
        when(resourceRepository.findById(firstResource.getId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> resourceService.downloadResource(firstResource.getId()));
    }

    @Test
    public void testDownLoadResource() {
        when(resourceRepository.findById(firstResource.getId())).thenReturn(Optional.of(firstResource));
        when(s3Service.downloadFile(firstResource.getKey())).thenReturn(any(InputStream.class));
        resourceService.downloadResource(firstResource.getId());
    }

    @Test
    public void testDeleteResource_TeamMemberNotFound() {
        when(userServiceClient.getUser(userId)).thenReturn(Mockito.any());
        when(resourceRepository.getReferenceById(firstResource.getId())).thenReturn(firstResource);
        when(teamMemberJpaRepository.findByUserIdAndProjectId(userId, firstProject.getId())).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> resourceService.deleteResource(firstResource.getId(), userId));
    }

    @Test
    public void testDeleteResource_OnlyAuthorCanDeleteFile() {
        when(userServiceClient.getUser(userId)).thenReturn(Mockito.any());
        when(resourceRepository.getReferenceById(firstResource.getId())).thenReturn(firstResource);
        when(teamMemberJpaRepository.findByUserIdAndProjectId(userId, firstProject.getId())).thenReturn(secondTeamMember);

        assertThrows(DataValidationException.class, () -> resourceService.deleteResource(firstResource.getId(), userId));
    }

    @Test
    public void testDeleteResource() {
        when(userServiceClient.getUser(userId)).thenReturn(Mockito.any());
        when(resourceRepository.getReferenceById(firstResource.getId())).thenReturn(firstResource);
        when(teamMemberJpaRepository.findByUserIdAndProjectId(userId, firstProject.getId())).thenReturn(firstResource.getCreatedBy());

        resourceService.deleteResource(firstResource.getId(), userId);

        s3Service.deleteFile(firstResource.getKey());
        resourceRepository.delete(firstResource);
    }
}