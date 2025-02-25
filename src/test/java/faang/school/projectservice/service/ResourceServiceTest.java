package faang.school.projectservice.service;

import faang.school.projectservice.dto.resource.ResourceResultDto;
import faang.school.projectservice.mapper.ResourceResultMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {

    private final long defaultMaxProjectStorageSize = 1000L;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private MinioService minioService;
    @Spy
    private ResourceResultMapper resourceResultMapper = Mappers.getMapper(ResourceResultMapper.class);
    @InjectMocks
    private ResourceService resourceService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(resourceService, "defaultMaxProjectStorageSize", defaultMaxProjectStorageSize);
    }

    @Test
    void uploadResource_Success() throws Exception {
        byte[] content = "test content".getBytes();
        MultipartFile file = createMockMultipartFile("test.txt", "text/plain", content);
        Long projectId = 1L;
        Project project = createProject(projectId, BigInteger.ZERO, BigInteger.valueOf(defaultMaxProjectStorageSize));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        Resource savedResource = Resource.builder().id(1L).name("test.txt").build();
        when(resourceRepository.save(any(Resource.class))).thenReturn(savedResource);
        ResourceResultDto result = resourceService.uploadResource(file, projectId, 2L);
        verify(minioService, times(1)) .uploadFile(any(ByteArrayInputStream.class),
                contains("project-" + projectId), eq("text/plain"), eq((long) content.length));

        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository, times(1)).save(projectCaptor.capture());

        Project savedProject = projectCaptor.getValue();
        assertEquals(BigInteger.valueOf(content.length), savedProject.getStorageSize());
        assertNotNull(result);
        verify(resourceResultMapper, times(1)).toResultDto(any(Resource.class));
    }

    @Test
    void uploadResource_EmptyFile() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> resourceService.uploadResource(file, 1L, 1L));
        assertEquals("The file is empty", exception.getMessage());
    }

    @Test
    void uploadResource_ExceedStorage() throws Exception {
        byte[] content = "large file".getBytes();
        MultipartFile file = createMockMultipartFile("large.txt", "text/plain", content);
        Long projectId = 1L;

        Project project = createProject(projectId, BigInteger.valueOf(995), BigInteger.valueOf(1000));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                resourceService.uploadResource(file, projectId, 1L));
        assertEquals("The project storage limit has been exceeded", exception.getMessage());
    }

    @Test
    void uploadResource_ProjectNotFound() throws Exception {
        byte[] content = "test".getBytes();
        MultipartFile file = createMockMultipartFile("test.txt", "text/plain", content);
        Long projectId = 1L;
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                resourceService.uploadResource(file, projectId, 1L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getMessage().contains("The project was not found"));
    }

    @Test
    void deleteResource_Success() {
        Long resourceId = 1L;
        Long teamMemberId = 2L;
        Project project = createProject(1L, BigInteger.valueOf(200), BigInteger.valueOf(defaultMaxProjectStorageSize));
        Resource resource = createResource(resourceId, "test.txt", "file-key",
                BigInteger.valueOf(100), ResourceStatus.ACTIVE, project, teamMemberId);

        TeamMember currentUser = createTeamMember(teamMemberId, TeamRole.OWNER);
        when(teamMemberRepository.findById(teamMemberId)).thenReturn(Optional.of(currentUser));
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));

        resourceService.deleteResource(resourceId, teamMemberId);

        verify(minioService, times(1)).removeFile("file-key");
        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository, times(1)).save(projectCaptor.capture());
        Project updatedProject = projectCaptor.getValue();
        assertEquals(BigInteger.valueOf(100), updatedProject.getStorageSize());

        ArgumentCaptor<Resource> resourceCaptor = ArgumentCaptor.forClass(Resource.class);
        verify(resourceRepository, times(1)).save(resourceCaptor.capture());
        Resource updatedResource = resourceCaptor.getValue();
        assertEquals(ResourceStatus.DELETED, updatedResource.getStatus());
        assertEquals(BigInteger.ZERO, updatedResource.getSize());
    }

    @Test
    void deleteResource_NotFound() {
        Long resourceId = 1L;
        Long teamMemberId = 2L;
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                resourceService.deleteResource(resourceId, teamMemberId));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getMessage().contains("The resource was not found"));
    }

    @Test
    void deleteResource_UserNotFound() {
        Long resourceId = 1L;
        Long teamMemberId = 2L;
        Resource resource = Resource.builder().id(resourceId).build();
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        when(teamMemberRepository.findById(teamMemberId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                resourceService.deleteResource(resourceId, teamMemberId));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getMessage().contains("The user was not found"));
    }

    @Test
    void deleteResource_NoPermission() {
        Long resourceId = 1L;
        Long teamMemberId = 2L;
        Project project = createProject(1L, BigInteger.valueOf(200), BigInteger.valueOf(defaultMaxProjectStorageSize));
        // Ресурс создан другим пользователем (id = 3)
        Resource resource = createResource(resourceId, "test.txt", "file-key",
                BigInteger.valueOf(100), ResourceStatus.ACTIVE, project, 3L);
        TeamMember currentUser = createTeamMember(teamMemberId, TeamRole.OWNER);
        when(teamMemberRepository.findById(teamMemberId)).thenReturn(Optional.of(currentUser));
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                resourceService.deleteResource(resourceId, teamMemberId));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertTrue(exception.getMessage().contains("The user does not have the rights to delete this file"));
    }

    private MultipartFile createMockMultipartFile(String fileName, String contentType, byte[] content) throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        lenient().when(file.isEmpty()).thenReturn(false);
        lenient().when(file.getBytes()).thenReturn(content);
        lenient().when(file.getSize()).thenReturn((long) content.length);
        lenient().when(file.getOriginalFilename()).thenReturn(fileName);
        lenient().when(file.getContentType()).thenReturn(contentType);
        return file;
    }

    private Project createProject(Long projectId, BigInteger currentStorage, BigInteger maxStorage) {
        Project project = new Project();
        project.setId(projectId);
        project.setName("Project " + projectId);
        project.setStorageSize(currentStorage);
        project.setMaxStorageSize(maxStorage);
        return project;
    }

    private TeamMember createTeamMember(Long id, TeamRole... roles) {
        return TeamMember.builder().id(id).roles(List.of(roles)).build();
    }

    private Resource createResource(Long resourceId, String fileName, String key, BigInteger size,
                                    ResourceStatus status, Project project, Long creatorId) {
        Resource resource = Resource.builder()
                .id(resourceId)
                .name(fileName)
                .key(key)
                .size(size)
                .status(status)
                .project(project)
                .build();
        resource.setCreatedBy(TeamMember.builder().id(creatorId).build());
        return resource;
    }

}
