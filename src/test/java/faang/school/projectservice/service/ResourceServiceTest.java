package faang.school.projectservice.service;

import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.DataNotFoundException;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.resource.ResourceService;
import faang.school.projectservice.service.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {

    @Mock
    private S3Service s3Service;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private ResourceService resourceService;

    private Long userId;
    private Project project;
    private TeamMember member;
    private Resource resource;
    private MultipartFile file;

    @BeforeEach
    void setUp() {
        userId = 1L;

        project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setStorageSize(BigInteger.ZERO);
        project.setMaxStorageSize(BigInteger.valueOf(10000));

        member = new TeamMember();
        member.setId(1L);
        member.setNickname("Test Member");
        member.setRoles(List.of(TeamRole.OWNER));

        resource = new Resource();
        resource.setId(1L);
        resource.setSize(BigInteger.valueOf(500));
        resource.setType(ResourceType.IMAGE);
        resource.setKey("test-key");
        resource.setProject(project);
        resource.setCreatedBy(member);
        resource.setUpdatedBy(member);
        resource.setStatus(ResourceStatus.ACTIVE);

        file = mock(MultipartFile.class);
    }

    @Test
    void testAddResource_Saves_WhenValid() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.ofNullable(project));
        when(teamMemberRepository.findByUserIdAndProjectId(userId, project.getId())).thenReturn(member);
        when(file.getContentType()).thenReturn("image");
        when(file.getSize()).thenReturn(500L);
        when(s3Service.uploadFile(file, "1-Test Project")).thenReturn(resource);
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);

        Resource result = resourceService.addResource(project.getId(), userId, file);

        assertNotNull(result);
        assertEquals(resource, result);
        verify(resourceRepository).save(any(Resource.class));
    }

    @Test
    void testAddResource_Throws_WhenProjectNotFound() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () ->
                resourceService.addResource(project.getId(), userId, file));
    }

    @Test
    void testAddResource_Throws_WhenMemberNotFound() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.ofNullable(project));
        when(teamMemberRepository.findByUserIdAndProjectId(userId, project.getId())).thenReturn(null);

        assertThrows(DataNotFoundException.class, () ->
                resourceService.addResource(project.getId(), userId, file));
    }

    @Test
    void testAddResource_Throws_WhenInvalidFileExtension() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(teamMemberRepository.findByUserIdAndProjectId(userId, project.getId())).thenReturn(member);
        when(file.getContentType()).thenReturn("application/octet-stream");

        assertThrows(FileException.class, () ->
                resourceService.addResource(project.getId(), userId, file));
    }

    @Test
    void testUpdateResource_UpdatesWhenValid() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.ofNullable(project));
        when(teamMemberRepository.findByUserIdAndProjectId(userId, project.getId())).thenReturn(member);
        when(resourceRepository.findById(resource.getId())).thenReturn(Optional.ofNullable(resource));
        when(file.getContentType()).thenReturn("image");
        when(file.getSize()).thenReturn(500L);
        when(s3Service.uploadFile(file, "1-Test Project")).thenReturn(resource);
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);

        Resource result = resourceService.updateResource(resource.getId(), project.getId(), userId, file);

        assertNotNull(result);
        assertEquals(resource, result);
        verify(resourceRepository).save(any(Resource.class));
    }

    @Test
    void testUpdateResource_Throws_WhenResourceNotFound() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.ofNullable(project));
        when(teamMemberRepository.findByUserIdAndProjectId(userId, project.getId())).thenReturn(member);
        when(resourceRepository.findById(resource.getId())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () ->
                resourceService.updateResource(resource.getId(), project.getId(), userId, file));
    }

    @Test
    void testUpdateResource_Throws_WhenInvalidFileExtension() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(teamMemberRepository.findByUserIdAndProjectId(userId, project.getId())).thenReturn(member);
        when(resourceRepository.findById(resource.getId())).thenReturn(Optional.of(resource));
        when(file.getContentType()).thenReturn("audio");

        assertThrows(FileException.class, () ->
                resourceService.updateResource(resource.getId(), project.getId(), userId, file));
    }

    @Test
    void testAddResource_Throws_WhenFileTooLarge() {
        when(file.getSize()).thenReturn(15000L);
        when(projectRepository.findById(project.getId())).thenReturn(Optional.ofNullable(project));
        when(teamMemberRepository.findByUserIdAndProjectId(userId, project.getId())).thenReturn(member);
        when(file.getContentType()).thenReturn("image");
        when(s3Service.uploadFile(any(), anyString())).thenReturn(resource);

        assertThrows(FileException.class, () ->
                resourceService.addResource(project.getId(), userId, file));
    }

    @Test
    void testDeleteResource_Succeeds_WhenValid() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.ofNullable(project));
        when(teamMemberRepository.findByUserIdAndProjectId(userId, project.getId())).thenReturn(member);
        when(resourceRepository.findById(resource.getId())).thenReturn(Optional.ofNullable(resource));

        resourceService.deleteResource(resource.getId(), project.getId(), userId);

        ArgumentCaptor<Resource> resourceCaptor = ArgumentCaptor.forClass(Resource.class);
        verify(resourceRepository).save(resourceCaptor.capture());

        assertEquals(ResourceStatus.DELETED, resourceCaptor.getValue().getStatus());
        assertEquals(member, resourceCaptor.getValue().getUpdatedBy());
    }

    @Test
    void testDeleteResource_Throws_WhenProjectNotFound() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () ->
                resourceService.deleteResource(resource.getId(), project.getId(), userId));
    }

    @Test
    void testDeleteResource_Throws_WhenMemberNotFound() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.ofNullable(project));
        when(teamMemberRepository.findByUserIdAndProjectId(userId, project.getId())).thenReturn(null);

        assertThrows(DataNotFoundException.class, () ->
                resourceService.deleteResource(resource.getId(), project.getId(), userId));
    }

    @Test
    void testDeleteResource_Throws_WhenResourceNotFound() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.ofNullable(project));
        when(teamMemberRepository.findByUserIdAndProjectId(userId, project.getId())).thenReturn(member);
        when(resourceRepository.findById(resource.getId())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () ->
                resourceService.deleteResource(resource.getId(), project.getId(), userId));
    }

    @Test
    void testDeleteResource_Throws_WhenAccessDenied() {
        member.setRoles(List.of(TeamRole.INTERN));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(teamMemberRepository.findByUserIdAndProjectId(userId, project.getId())).thenReturn(member);
        when(resourceRepository.findById(resource.getId())).thenReturn(Optional.of(resource));

        assertThrows(AccessDeniedException.class, () ->
                resourceService.deleteResource(resource.getId(), project.getId(), userId));
    }
}
