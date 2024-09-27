package faang.school.projectservice.Resource;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.dto.resource.ResourceDownloadDto;
import faang.school.projectservice.exception.resource.ResourceDeleteNotAllowedException;
import faang.school.projectservice.exception.resource.ResourceDeletedException;
import faang.school.projectservice.exception.resource.ResourceDownloadException;
import faang.school.projectservice.exception.resource.ResourceUploadException;
import faang.school.projectservice.exception.resource.StorageLimitExceededException;
import faang.school.projectservice.exception.resource.UnauthorizedFileUploadException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.resource.ResourceService;
import faang.school.projectservice.service.resource.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {
    @InjectMocks
    private ResourceService resourceService;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectService projectService;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private S3Service s3Service;
    @Mock
    private TeamMemberRepository teamMemberRepository;

    private Project project;
    private TeamMember teamMember;
    private Resource resource;
    private MultipartFile file;

    @BeforeEach
    public void setUp() {
        project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setStorageSize(BigInteger.valueOf(0L));
        project.setMaxStorageSize(BigInteger.valueOf(1000L));

        teamMember = new TeamMember();
        teamMember.setId(1L);
        teamMember.setRoles(new ArrayList<>());
        teamMember.setTeam(Team.builder()
                .project(project)
                .build());

        resource = new Resource();
        resource.setKey("test/key");
        resource.setSize(BigInteger.valueOf(100L));
        resource.setCreatedBy(teamMember);
        resource.setProject(project);

        file = new MockMultipartFile("file", "test content".getBytes());
    }

    @Test
    public void testCreateResource() {
        when(projectRepository.getProjectById(any())).thenReturn(project);
        when(teamMemberRepository.findById(any())).thenReturn(teamMember);
        when(resourceRepository.save(any())).thenReturn(resource);

        Resource result = resourceService.createResource(file, 1L, 1L);

        assertEquals(result.getProject(), project);
        assertEquals(result.getName(), file.getOriginalFilename());
        assertEquals(result.getStatus(), ResourceStatus.ACTIVE);
        assertEquals(result.getAllowedRoles(), teamMember.getRoles());
        assertEquals(result.getSize(), BigInteger.valueOf(file.getSize()));
        assertNotNull(result.getCreatedAt());

        verify(projectRepository).getProjectById(1L);
        verify(teamMemberRepository).findById(1L);
        verify(resourceRepository).save(any(Resource.class));
        verify(projectService).updateStorageSize(eq(project.getId()), any(BigInteger.class));
    }

    @Test
    public void testCreateResourceStorageLimit() {
        project.setStorageSize(project.getMaxStorageSize());

        when(projectRepository.getProjectById(any())).thenReturn(project);
        assertThrows(StorageLimitExceededException.class, () ->
                resourceService.createResource(file, 1L, 1L)
        );
    }

    @Test
    public void testCreateResourceAlienProject() {
        Project project = new Project();
        project.setId(2L);
        project.setMaxStorageSize(BigInteger.valueOf(500L));
        project.setStorageSize(BigInteger.valueOf(0));

        when(projectRepository.getProjectById(any())).thenReturn(project);
        when(teamMemberRepository.findById(any())).thenReturn(teamMember);

        assertThrows(UnauthorizedFileUploadException.class, () ->
                resourceService.createResource(file, 1L, 1L)
        );
    }

    @Test
    public void testCreateResourceUploadError() {
        when(projectRepository.getProjectById(any())).thenReturn(project);
        when(teamMemberRepository.findById(any())).thenReturn(teamMember);
        doThrow(new RuntimeException()).when(s3Service).upload(eq(file), any());

        assertThrows(ResourceUploadException.class, () ->
                resourceService.createResource(file, 1L, 1L)
        );
    }

    @Test
    public void testUpdateResource() {
        when(projectRepository.getProjectById(any())).thenReturn(project);
        when(teamMemberRepository.findById(any())).thenReturn(teamMember);
        when(resourceRepository.findById(anyLong())).thenReturn(Optional.ofNullable(resource));

        Resource result = resourceService.updateResource(file, 1L, 1L);

        assertEquals(result.getName(), file.getOriginalFilename());
        assertEquals(result.getSize(), BigInteger.valueOf(file.getSize()));
        assertEquals(result.getUpdatedBy(), teamMember);
        assertNotNull(result.getUpdatedAt());

        verify(projectRepository).getProjectById(1L);
        verify(teamMemberRepository).findById(1L);
        verify(resourceRepository).save(any(Resource.class));
        verify(projectService).updateStorageSize(eq(project.getId()), any(BigInteger.class));
    }

    @Test
    public void testUpdateDeleteStatus() {
        resource.setStatus(ResourceStatus.DELETED);
        when(resourceRepository.findById(anyLong())).thenReturn(Optional.ofNullable(resource));

        assertThrows(ResourceDeletedException.class, () ->
                resourceService.updateResource(file, 1L, 1L)
        );
    }

    @Test
    public void testUpdateResourceStorageLimit() {
        project.setStorageSize(project.getMaxStorageSize());
        resource.setSize(BigInteger.valueOf(1L));

        when(resourceRepository.findById(anyLong())).thenReturn(Optional.ofNullable(resource));

        when(projectRepository.getProjectById(any())).thenReturn(project);
        assertThrows(StorageLimitExceededException.class, () ->
                resourceService.updateResource(file, 1L, 1L)
        );
    }

    @Test
    public void testUpdateResourceUploadError() {
        when(projectRepository.getProjectById(any())).thenReturn(project);
        when(teamMemberRepository.findById(any())).thenReturn(teamMember);
        when(resourceRepository.findById(anyLong())).thenReturn(Optional.ofNullable(resource));
        doThrow(new RuntimeException()).when(s3Service).upload(eq(file), any());

        assertThrows(ResourceUploadException.class, () ->
                resourceService.updateResource(file, 1L, 1L)
        );
    }

    @Test
    public void testDeleteResource() {
        when(projectRepository.getProjectById(any())).thenReturn(project);
        when(teamMemberRepository.findById(any())).thenReturn(teamMember);
        when(resourceRepository.findById(anyLong())).thenReturn(Optional.ofNullable(resource));

        resourceService.deleteResource(1L, 1L);

        verify(s3Service).delete(anyString());
        verify(projectRepository).getProjectById(1L);
        verify(teamMemberRepository).findById(1L);
        verify(resourceRepository).save(any(Resource.class));
        verify(projectService).updateStorageSize(eq(project.getId()), any(BigInteger.class));
    }

    @Test
    public void testDeleteResourceAlienUser() {
        teamMember = new TeamMember();
        teamMember.setId(2L);
        teamMember.setRoles(new ArrayList<>());
        teamMember.setTeam(Team.builder()
                .project(Project.builder().id(2L).build())
                .build());

        when(teamMemberRepository.findById(any())).thenReturn(teamMember);
        when(resourceRepository.findById(anyLong())).thenReturn(Optional.ofNullable(resource));

        assertThrows(ResourceDeleteNotAllowedException.class, () ->
                resourceService.deleteResource(1L, 1L)
        );
    }

    @Test
    public void testDeleteResourceDeleteStatus() {
        resource.setStatus(ResourceStatus.DELETED);
        when(resourceRepository.findById(anyLong())).thenReturn(Optional.ofNullable(resource));

        assertThrows(ResourceDeletedException.class, () ->
                resourceService.deleteResource(1L, 1L)
        );
    }

    @Test
    public void testDownloadResource() throws IOException {
        when(resourceRepository.findById(anyLong())).thenReturn(Optional.ofNullable(resource));
        S3Object s3Object = mock(S3Object.class);
        ObjectMetadata objectMetadata = mock(ObjectMetadata.class);
        S3ObjectInputStream objectContent = mock(S3ObjectInputStream.class);

        when(objectMetadata.getContentType()).thenReturn("application/pdf");
        when(objectContent.readAllBytes()).thenReturn(null);

        when(s3Object.getObjectMetadata()).thenReturn(objectMetadata);
        when(s3Object.getObjectContent()).thenReturn(objectContent);
        when(s3Service.download(anyString())).thenReturn(s3Object);

        ResourceDownloadDto result = resourceService.downloadResource(1L);

        assertEquals(result.getOriginName(), resource.getName());
        assertEquals(result.getType(), MediaType.parseMediaType(objectMetadata.getContentType()));
        assertNull(result.getBytes());
    }

    @Test
    public void testDownloadResourceDeleteStatus() {
        resource.setStatus(ResourceStatus.DELETED);
        when(resourceRepository.findById(anyLong())).thenReturn(Optional.ofNullable(resource));

        assertThrows(ResourceDeletedException.class, () ->
                resourceService.downloadResource(1L)
        );
    }

    @Test
    public void testDownloadResourceDownloadError() {
        when(resourceRepository.findById(anyLong())).thenReturn(Optional.ofNullable(resource));
        doThrow(new RuntimeException()).when(s3Service).download(anyString());

        assertThrows(ResourceDownloadException.class, () ->
                resourceService.downloadResource(1L)
        );
    }
}
