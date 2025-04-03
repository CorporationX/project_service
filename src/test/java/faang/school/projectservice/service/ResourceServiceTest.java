package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.config.project.ProjectStorageProperties;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.exception.AccessDeniedProjectException;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.exception.StorageLimitExceededException;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.project.interfaces.ProjectService;
import faang.school.projectservice.service.resources.implementations.ResourceServiceImpl;
import faang.school.projectservice.service.s3.interfaces.S3Service;
import faang.school.projectservice.validation.project.ProjectValidator;
import faang.school.projectservice.validation.resource.ResourceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ResourceServiceTest {

    @Mock
    private ProjectService projectService;
    @Mock
    private S3Service s3Service;
    @Mock
    private UserContext userContext;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private ResourceMapper resourceMapper;
    @Mock
    private ProjectStorageProperties projectStorageProperties;
    @Mock
    private ProjectValidator projectValidator;
    @Mock
    private ResourceValidator resourceValidator;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addResource_shouldAddFileSuccessfully() {
        Long projectId = 1L;
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", "test content".getBytes());

        Project project = new Project();
        project.setId(projectId);
        project.setStorageSize(BigInteger.ZERO);

        TeamMember member = new TeamMember();

        Resource uploaded = Resource.builder()
                .key("project_1/test.pdf")
                .name("test.pdf")
                .type(ResourceType.PDF)
                .size(BigInteger.valueOf(file.getSize()))
                .status(ResourceStatus.ACTIVE)
                .build();

        Resource saved = new Resource();
        ResourceDto dto = new ResourceDto(
                1L,
                "test.pdf",
                "project_1/test.pdf",
                BigInteger.valueOf(file.getSize()),
                ResourceType.PDF,
                ResourceStatus.ACTIVE,
                LocalDateTime.now(),
                "user42"
        );

        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(projectValidator.validateProjectMembership(projectId)).thenReturn(member);
        when(projectStorageProperties.getDefaultMaxSize()).thenReturn(BigInteger.valueOf(2L * 1024 * 1024 * 1024));
        when(s3Service.uploadFile(file, "project_1")).thenReturn(uploaded);
        when(resourceRepository.save(any(Resource.class))).thenReturn(saved);
        when(resourceMapper.toDto(saved)).thenReturn(dto);
        when(projectStorageProperties.getFolderPrefix()).thenReturn("project_");

        ResourceDto result = resourceService.addResource(projectId, file);

        assertThat(result).isNotNull();
        verify(resourceRepository).save(any(Resource.class));
        verify(projectService).save(project);
    }

    @Test
    void addResource_shouldThrowIfStorageLimitExceeded() {
        Long projectId = 1L;
        MockMultipartFile file = new MockMultipartFile(
                "file", "bigfile.pdf", "application/pdf", new byte[100]);

        Project project = new Project();
        project.setId(projectId);
        project.setStorageSize(BigInteger.valueOf(Long.MAX_VALUE));

        TeamMember member = new TeamMember();

        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(projectValidator.validateProjectMembership(projectId)).thenReturn(member);
        when(projectStorageProperties.getDefaultMaxSize()).thenReturn(BigInteger.valueOf(100));

        assertThrows(StorageLimitExceededException.class,
                () -> resourceService.addResource(projectId, file));
    }

    @Test
    void getResources_shouldReturnListOfResourceDtos() {
        Long projectId = 1L;
        TeamMember member = new TeamMember();
        Resource resource = new Resource();
        ResourceDto dto = new ResourceDto(
                1L,
                "file.pdf",
                "project_1/file.pdf",
                BigInteger.valueOf(12345),
                ResourceType.PDF,
                ResourceStatus.ACTIVE,
                LocalDateTime.now(),
                "user42"
        );

        when(projectValidator.validateProjectMembership(projectId)).thenReturn(member);
        when(resourceRepository.findAllByProjectIdAndStatus(projectId, ResourceStatus.ACTIVE))
                .thenReturn(List.of(resource));
        when(resourceMapper.toDto(resource)).thenReturn(dto);

        List<ResourceDto> result = resourceService.getResources(projectId);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0)).isEqualTo(dto);
        verify(resourceRepository).findAllByProjectIdAndStatus(projectId, ResourceStatus.ACTIVE);
    }

    @Test
    void getResources_shouldThrowIfUserNotInProject() {
        Long projectId = 1L;

        when(projectValidator.validateProjectMembership(projectId))
                .thenThrow(new AccessDeniedProjectException("User is not in the project"));

        assertThrows(AccessDeniedProjectException.class,
                () -> resourceService.getResources(projectId));
    }

    @Test
    void updateResource_shouldUpdateFileSuccessfully() {
        when(projectStorageProperties.getFolderPrefix()).thenReturn("project_");
        Long resourceId = 1L;
        Long projectId = 1L;
        String folder = "project_1";
        MockMultipartFile file = new MockMultipartFile("file", "updated.pdf", "application/pdf", "updated content".getBytes());

        Project project = new Project();
        project.setId(projectId);
        project.setStorageSize(BigInteger.valueOf(100));

        Resource existingResource = new Resource();
        existingResource.setId(resourceId);
        existingResource.setKey("project_1/old.pdf");
        existingResource.setSize(BigInteger.valueOf(10));
        existingResource.setProject(project);

        Resource uploaded = new Resource();
        uploaded.setKey("project_1/updated.pdf");
        uploaded.setSize(BigInteger.valueOf(file.getSize()));
        uploaded.setType(ResourceType.PDF);
        uploaded.setName("updated.pdf");

        Resource updated = new Resource();
        ResourceDto dto = new ResourceDto(
                1L,
                "updated.pdf",
                "project_1/updated.pdf",
                BigInteger.valueOf(file.getSize()),
                ResourceType.PDF,
                ResourceStatus.ACTIVE,
                LocalDateTime.now(),
                "user42"
        );

        when(resourceValidator.validateResourceAccess(projectId, resourceId)).thenReturn(existingResource);
        when(projectStorageProperties.getDefaultMaxSize()).thenReturn(BigInteger.valueOf(2L * 1024 * 1024 * 1024));
        when(s3Service.uploadFile(eq(file), eq(folder))).thenReturn(uploaded);
        when(resourceRepository.save(existingResource)).thenReturn(existingResource);
        when(resourceMapper.toDto(existingResource)).thenReturn(dto);
        when(projectValidator.validateProjectMembership(projectId)).thenReturn(new TeamMember());

        ResourceDto result = resourceService.updateResource(resourceId, projectId, file);

        assertThat(result).isNotNull();
        verify(resourceRepository).save(existingResource);
        verify(projectService).save(project);
    }

    @Test
    void updateResource_shouldThrowIfStorageLimitExceeded() {
        Long resourceId = 1L;
        Long projectId = 1L;
        MockMultipartFile file = new MockMultipartFile("file", "file.pdf", "application/pdf", new byte[100]);

        Project project = new Project();
        project.setId(projectId);
        project.setStorageSize(BigInteger.valueOf(100));

        Resource existingResource = new Resource();
        existingResource.setId(resourceId);
        existingResource.setSize(BigInteger.ZERO);
        existingResource.setProject(project);

        when(resourceValidator.validateResourceAccess(projectId, resourceId)).thenReturn(existingResource);
        when(projectStorageProperties.getDefaultMaxSize()).thenReturn(BigInteger.valueOf(50));

        assertThrows(StorageLimitExceededException.class,
                () -> resourceService.updateResource(resourceId, projectId, file));
    }

    @Test
    void updateResource_shouldThrowIfAccessDenied() {
        Long resourceId = 1L;
        Long projectId = 1L;
        MockMultipartFile file = new MockMultipartFile("file", "file.pdf", "application/pdf", new byte[100]);

        when(resourceValidator.validateResourceAccess(projectId, resourceId))
                .thenThrow(new AccessDeniedProjectException("No access to resource"));

        assertThrows(AccessDeniedProjectException.class,
                () -> resourceService.updateResource(resourceId, projectId, file));
    }

    @Test
    void deleteResource_shouldDeleteSuccessfully() {
        Long resourceId = 1L;
        Long projectId = 1L;

        Project project = new Project();
        project.setId(projectId);
        project.setStorageSize(BigInteger.valueOf(500));

        Resource resource = new Resource();
        resource.setId(resourceId);
        resource.setProject(project);
        resource.setKey("project_1/test.pdf");
        resource.setSize(BigInteger.valueOf(100));
        resource.setStatus(ResourceStatus.ACTIVE);

        TeamMember member = new TeamMember();

        when(resourceValidator.validateResourceAccess(projectId, resourceId)).thenReturn(resource);
        when(projectValidator.validateProjectMembership(projectId)).thenReturn(member);

        resourceService.deleteResource(resourceId, projectId);

        ArgumentCaptor<Resource> captor = ArgumentCaptor.forClass(Resource.class);
        verify(resourceRepository).save(captor.capture());

        Resource saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(ResourceStatus.DELETED);
        verify(s3Service).deleteFile("project_1/test.pdf");
        verify(projectService).save(project);
    }

    @Test
    void deleteResource_shouldThrowIfNotFound() {
        Long resourceId = 1L;
        Long projectId = 1L;

        when(resourceValidator.validateResourceAccess(projectId, resourceId))
                .thenThrow(new AccessDeniedProjectException("Resource not found or no access"));

        assertThrows(AccessDeniedProjectException.class,
                () -> resourceService.deleteResource(resourceId, projectId));
    }

    @Test
    void deleteResource_shouldThrowIfNotInProject() {
        Long resourceId = 1L;
        Long projectId = 1L;
        Resource resource = new Resource();
        resource.setId(resourceId);
        resource.setProject(new Project());

        when(resourceValidator.validateResourceAccess(projectId, resourceId)).thenReturn(resource);
        when(projectValidator.validateProjectMembership(projectId))
                .thenThrow(new AccessDeniedProjectException("User not in project"));

        assertThrows(AccessDeniedProjectException.class,
                () -> resourceService.deleteResource(resourceId, projectId));
    }

    @Test
    void deleteResource_shouldThrowIfS3Fails() {
        Long resourceId = 1L;
        Long projectId = 1L;

        Project project = new Project();
        project.setId(projectId);

        Resource resource = new Resource();
        resource.setId(resourceId);
        resource.setKey("project_1/test.pdf");
        resource.setSize(BigInteger.valueOf(100));
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setProject(project);

        TeamMember member = new TeamMember();

        when(resourceValidator.validateResourceAccess(projectId, resourceId)).thenReturn(resource);
        when(projectValidator.validateProjectMembership(projectId)).thenReturn(member);
        doThrow(new FileException("S3 error", null))
                .when(s3Service).deleteFile("project_1/test.pdf");


        assertThrows(FileException.class,
                () -> resourceService.deleteResource(resourceId, projectId));
    }
}

