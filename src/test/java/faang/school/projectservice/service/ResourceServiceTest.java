package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {
    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private TeamMemberService teamMemberService;

    @Mock
    private UserServiceClient userServiceClient;

    @Spy
    private ResourceMapper resourceMapper = Mappers.getMapper(ResourceMapper.class);

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private ResourceService resourceService;

    private Project project;
    private TeamMember teamMember;
    private Resource resource;
    private String key;
    private MultipartFile file;

    @BeforeEach
    public void setUp() {
        long projectId = 1L;
        long userId = 1L;
        key = "test-key";

        project = new Project();
        project.setId(projectId);
        project.setName("Test Project");
        project.setMaxStorageSize(new BigInteger("1000000"));
        project.setStorageSize(new BigInteger("0"));

        teamMember = new TeamMember();
        teamMember.setId(userId);
        teamMember.setRoles(new ArrayList<>());
        teamMember.getRoles().add(TeamRole.MANAGER);

        resource = new Resource();
        resource.setId(1L);
        resource.setCreatedBy(teamMember);
        resource.setProject(project);
        resource.setSize(new BigInteger("100"));
        resource.setType(ResourceType.AUDIO);
        resource.setKey("test-key");
        resource.setAllowedRoles(new ArrayList<>());
        resource.getAllowedRoles().add(TeamRole.MANAGER);
        resource.setStatus(ResourceStatus.ACTIVE);

        file = mock(MultipartFile.class);
    }

    @Test
    public void testUploadResourceWithSuccessfulData() {
        when(projectService.findProjectById(1L)).thenReturn(project);
        when(teamMemberService.getProjectMember(1L, 1L)).thenReturn(teamMember);
        when(s3Service.uploadFile(file, "1Test Project")).thenReturn(key);

        Resource expectedResource = Resource.builder()
                .id(1L)
                .name(file.getOriginalFilename())
                .key(key)  // Ключ файла
                .size(BigInteger.valueOf(file.getSize()))
                .status(ResourceStatus.ACTIVE)
                .type(ResourceType.AUDIO)
                .project(project)
                .createdBy(teamMember)
                .updatedBy(teamMember)
                .allowedRoles(new ArrayList<>(teamMember.getRoles()))
                .build();

        when(resourceRepository.save(any(Resource.class))).thenReturn( expectedResource);

        ResourceDto result = resourceService.uploadResource(1L, 1L, file);

        assertNotNull(result);
        assertEquals(expectedResource.getId(), result.id());
        verify(resourceRepository).save(any(Resource.class));
        verify(s3Service).uploadFile(any(), any());
    }

    @Test
    public void testUploadResourceUserNotMember() {
        when(projectService.findProjectById(1L)).thenReturn(project);
        when(teamMemberService.teamMemberInProjectNotExists(1L, 1L)).thenReturn(true);

        assertThrows(DataValidationException.class, () ->
                resourceService.uploadResource(1L, 1L, file)
        );
    }

    @Test
    public void testDeleteResourceWithCorrectData() {
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));
        when(teamMemberService.getProjectMember(1L, 1L)).thenReturn(teamMember);
        when(resourceRepository.save(resource)).thenReturn(resource);

        ResourceDto result = resourceService.deleteResource(1L, 1L);

        assertNotNull(result);
        assertEquals(ResourceStatus.DELETED, result.status());
        assertEquals(BigInteger.ZERO, result.size());

        verify(resourceRepository).save(any(Resource.class));
        verify(s3Service).deleteResource("test-key");
    }

    @Test
    public void testDeleteResourceWIthUserNotMember() {
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));
        when(teamMemberService.teamMemberInProjectNotExists(1L, 1L)).thenReturn(true);

        assertThrows(DataValidationException.class, () ->
                resourceService.deleteResource(1L, 1L)
        );
    }

    @Test
    public void testDeleteResourceWhenPermissionDenied() {
        teamMember = new TeamMember();
        teamMember.setUserId(2L);
        teamMember.setRoles(new ArrayList<>());
        teamMember.getRoles().add(TeamRole.DESIGNER);

        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));
        when(teamMemberService.getProjectMember(2L, 1L)).thenReturn(teamMember);

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                resourceService.deleteResource(1L, 2L)
        );
        assertEquals("Manager or Creator can delete file", exception.getMessage());
    }

    @Test
    public void testUpdateResourceWithCorrectData() {
        Resource newResource = new Resource();
        newResource.setId(34L);
        newResource.setType(ResourceType.TEXT);
        newResource.setCreatedBy(teamMember);
        newResource.setProject(project);
        newResource.setSize(new BigInteger("20"));
        newResource.setKey("test-key");

        when(file.getContentType()).thenReturn("text/plain");
        when(file.getOriginalFilename()).thenReturn("test.txt");
        when(file.getSize()).thenReturn(20L);
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));
        when(teamMemberService.getProjectMember(1L, 1L)).thenReturn(teamMember);
        when(userServiceClient.getUser(1L)).thenReturn(new UserDto());
        doNothing().when(s3Service).deleteResource(resource.getKey());
        when(s3Service.uploadFile(file, "1Test Project")).thenReturn(key);
        when(resourceRepository.save(resource)).thenReturn(resource);

        ResourceDto result = resourceService.updateResource(1L, 1L, file);

        assertNotNull(result);
        assertEquals(newResource.getType(), result.type());
        assertEquals(newResource.getSize(), result.size());

        verify(resourceRepository).save(any(Resource.class));
        verify(s3Service).deleteResource(resource.getKey());
        verify(s3Service).uploadFile(any(), any());
    }
}