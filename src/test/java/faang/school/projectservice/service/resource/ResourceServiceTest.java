package faang.school.projectservice.service.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.S3FileDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.StorageException;
import faang.school.projectservice.exception.common.RecordNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private S3Service s3Service;
    @Mock
    private UserContext userContext;

    @InjectMocks
    private ResourceService resourceService;

    private Project project;
    private TeamMember teamMember;
    private Resource resource;
    private MultipartFile file;
    private static final Long PROJECT_ID = 1L;
    private static final Long RESOURCE_ID = 1L;
    private static final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        project = Project.builder()
                .id(PROJECT_ID)
                .name("Test Project")
                .storageSize(BigInteger.ZERO)
                .maxStorageSize(BigInteger.valueOf(1000000))
                .build();

        Team team = Team.builder()
                .project(project)
                .build();

        teamMember = TeamMember.builder()
                .id(1L)
                .userId(USER_ID)
                .team(team)
                .roles(List.of(TeamRole.MANAGER))
                .build();

        resource = Resource.builder()
                .id(RESOURCE_ID)
                .name("test.txt")
                .key(project.getId() + project.getName())
                .size(BigInteger.valueOf(100))
                .type(ResourceType.TEXT)
                .status(ResourceStatus.ACTIVE)
                .project(project)
                .createdBy(teamMember)
                .updatedBy(teamMember)
                .allowedRoles(List.of(TeamRole.MANAGER))
                .build();

        file = new MockMultipartFile(
                "test.txt",
                "test.txt",
                "text/plain",
                "Hello, World!".getBytes()
        );

    }

    @Test
    void testAddResourceSuccessful() {
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        when(teamMemberRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(teamMember);
        when(s3Service.uploadFile(any(), any())).thenReturn(project.getId() + project.getName());
        when(resourceRepository.save(any())).thenReturn(resource);
        when(projectRepository.save(any())).thenReturn(project);
        when(userContext.getUserId()).thenReturn(USER_ID);

        Resource result = resourceService.addResource(PROJECT_ID, file);

        assertNotNull(result);
        assertEquals(ResourceStatus.ACTIVE, result.getStatus());
        assertEquals(ResourceType.TEXT, result.getType());
        verify(s3Service).uploadFile(any(), any());
        verify(resourceRepository).save(any());
        verify(projectRepository).save(any());
    }

    @Test
    void testDeleteResourceSuccessful() {
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.of(resource));
        when(teamMemberRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(teamMember);
        when(resourceRepository.save(any())).thenReturn(resource);
        when(projectRepository.save(any())).thenReturn(project);
        when(userContext.getUserId()).thenReturn(USER_ID);

        Boolean result = resourceService.deleteResource(PROJECT_ID, RESOURCE_ID);

        assertTrue(result);
        verify(s3Service).deleteFile(resource.getKey());
        verify(resourceRepository).save(any());
        verify(projectRepository).save(any());
    }

    @Test
    void testDownloadFileSuccessful() {
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.of(resource));
        when(teamMemberRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(teamMember);
        when(userContext.getUserId()).thenReturn(USER_ID);

        S3FileDto s3FileDto = S3FileDto.builder()
                .fileName("test.txt")
                .contentType("text/plain")
                .contentLength(100)
                .resource(new ByteArrayResource("test".getBytes()))
                .build();
        
        when(s3Service.downloadFile(resource.getKey())).thenReturn(s3FileDto);

        S3FileDto result = resourceService.downloadFile(PROJECT_ID, RESOURCE_ID);

        assertNotNull(result);
        assertEquals("test.txt", result.getFileName());
        assertEquals("text/plain", result.getContentType());
        verify(s3Service).downloadFile(resource.getKey());
    }

    @Test
    void testUpdateResourceSuccessful() {
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.of(resource));
        when(teamMemberRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(teamMember);
        when(s3Service.uploadFile(any(), any())).thenReturn(project.getId() + project.getName());
        when(resourceRepository.save(any())).thenReturn(resource);
        when(projectRepository.save(any())).thenReturn(project);
        when(userContext.getUserId()).thenReturn(USER_ID);

        Resource result = resourceService.updateResource(PROJECT_ID, RESOURCE_ID, file);

        assertNotNull(result);
        verify(s3Service).deleteFile(resource.getKey());
        verify(s3Service).uploadFile(any(), any());
        verify(resourceRepository).save(any());
        verify(projectRepository).save(any());
    }

    @Test
    void testAddResourceProjectNotFound() {
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () ->
                resourceService.addResource(PROJECT_ID, file)
        );
    }

    @Test
    void testUpdateResourceResourceNotFound() {
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () ->
            resourceService.updateResource(PROJECT_ID, RESOURCE_ID, file)
        );
    }

    @Test
    void testAddResourceStorageSizeExceeded() {
        project.setStorageSize(BigInteger.valueOf(999999));
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(teamMemberRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(teamMember);

        assertThrows(StorageException.class, () -> 
            resourceService.addResource(PROJECT_ID, file)
        );
    }

    @Test
    void testDeleteResourceResourceAlreadyDeleted() {
        resource.setStatus(ResourceStatus.DELETED);
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.of(resource));

        assertThrows(DataValidationException.class, () -> 
            resourceService.deleteResource(PROJECT_ID, RESOURCE_ID)
        );
    }
}
