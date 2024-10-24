package faang.school.projectservice.service.resource;

import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.model.dto.ResourceDto;
import faang.school.projectservice.exception.StorageLimitException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.resource.ResourceMapperImpl;
import faang.school.projectservice.model.entity.Project;
import faang.school.projectservice.model.entity.Resource;
import faang.school.projectservice.model.entity.Team;
import faang.school.projectservice.model.entity.TeamMember;
import faang.school.projectservice.model.enums.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.impl.ResourceServiceImpl;
import faang.school.projectservice.validator.ValidatorTeamMember;
import faang.school.projectservice.validator.ValidatorService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {
    @Mock
    private UserContext userContext;

    @Mock
    private ValidatorService validatorService;

    @Mock
    private ValidatorTeamMember validatorTeamMember;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private MultipartFile file;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private S3Service s3Service;

    @Spy
    private ResourceMapperImpl resourceMapper = new ResourceMapperImpl();

    @Captor
    private ArgumentCaptor<Resource> resourceCaptor;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    private Resource resource;
    private TeamMember teamMember;
    private Project project;
    private String key;
    private Long resourceId;
    private Long projectId;
    private Long teamMemberId;

    @BeforeEach
    public void setUp() {
        resourceId = 1L;
        projectId = 1L;
        teamMemberId = 1L;

        project = new Project();
        project.setId(projectId);
        project.setStorageSize(BigInteger.valueOf(200));
        project.setMaxStorageSize(BigInteger.valueOf(1000));

        teamMember = new TeamMember();
        teamMember.setId(1L);
        teamMember.setRoles(List.of(TeamRole.ANALYST));

        Team team = new Team();
        team.setProject(project);

        key = "someKey";
        resource = new Resource();
        resource.setProject(project);
        resource.setCreatedBy(teamMember);
        resource.setSize(BigInteger.valueOf(1));
        resource.setKey(key);


        lenient().when(teamMemberRepository.findById(teamMemberId)).thenReturn(teamMember);
        lenient().when(file.getSize()).thenReturn(200L);
        lenient().when(projectRepository.findById(projectId)).thenReturn(project);
        lenient().when(userContext.getUserId()).thenReturn(1L);
        lenient().when(s3Service.uploadFile(file, project.getName())).thenReturn(resource);
        lenient().when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));

    }

    @Test
    void testAddResourceSuccess() {
        when(resourceRepository.save(resource)).thenReturn(resource);

        ResourceDto resourceDto = resourceService.addResource(projectId, file);

        verify(s3Service, times(1)).uploadFile(file, resource.getName());
        verify(resourceRepository, times(1)).save(resourceCaptor.capture());
        verify(validatorService, times(1)).isProjectExists(projectId);
        verify(validatorTeamMember, times(1)).isMember(teamMember,project);
        verify(teamMemberRepository, times(1)).findById(teamMemberId);
        verify(projectRepository, times(1)).findById(projectId);

        Resource result = resourceCaptor.getValue();
        assertAll(
                () -> assertEquals(result.getName(), resourceDto.getName()),
                () -> assertEquals(result.getSize(), resourceDto.getSize()),
                () -> assertEquals(result.getUpdatedAt(), resourceDto.getUpdatedAt()),
                () -> assertEquals(result.getType(), resourceDto.getType()),
                () -> assertEquals(result.getProject().getId(), resourceDto.getProjectId())
        );
    }


    @Test
    void testAddResourceStorageSizeExceeded() {
        when(file.getSize()).thenReturn(900L);

        StorageLimitException exception = assertThrows(StorageLimitException.class,
                () -> resourceService.addResource(projectId, file));

        assertEquals("Storage limit exceeded", exception.getMessage());
        verify(validatorService, times(1)).isProjectExists(projectId);
        verify(validatorTeamMember, times(1)).isMember(teamMember,project);
        verify(projectRepository, times(1)).findById(projectId);
        verify(teamMemberRepository, times(1)).findById(teamMemberId);
        verify(resourceRepository, never()).save(any(Resource.class));
    }

    @Test
    void testGetResourceSuccess() {
        S3Object s3Object = new S3Object();
        s3Object.setBucketName("BucketName");
        when(s3Service.getFile(resource.getKey())).thenReturn(s3Object);

        S3Object result = resourceService.getResource(resourceId);

        verify(s3Service, times(1)).getFile(resource.getKey());
        verify(resourceRepository, times(1)).findById(resourceId);
        assertEquals(s3Object, result);
    }

    @Test
    void testGetResourceNotFound() {
        doThrow(EntityNotFoundException.class).when(resourceRepository).findById(resourceId);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> resourceService.getResource(resourceId));

        verify(resourceRepository, times(1)).findById(resourceId);
        assertNull(exception.getMessage());
    }

    @Test
    void testUpdateResourceSuccess() {
        String newKey = "someNewKey";
        Resource newResource = new Resource();
        newResource.setKey(newKey);
        newResource.setSize(BigInteger.valueOf(file.getSize()));
        when(s3Service.uploadFile(file, project.getName())).thenReturn(newResource);
        doNothing().when(s3Service).deleteFile(resource.getKey());

        ResourceDto result = resourceService.updateResource(resourceId, file);

        verify(s3Service, times(1)).deleteFile(key);
        verify(resourceRepository, times(1)).findById(resourceId);
        verify(teamMemberRepository, times(1)).findById(teamMemberId);
        verify(projectRepository, times(1)).findById(projectId);
        verify(s3Service, times(1)).uploadFile(file, resource.getName());
        assertAll(
                () -> assertEquals(file.getName(), result.getName()),
                () -> assertEquals(file.getSize(), result.getSize().longValue()),
                () -> assertEquals(newKey, result.getKey()),
                () -> assertEquals(resource.getId(), result.getId()),
                () -> assertEquals(resource.getProject().getId(), result.getProjectId())
        );
    }

    @Test
    void testUpdateResourceNotFound() {
        doThrow(EntityNotFoundException.class).when(resourceRepository).findById(resourceId);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> resourceService.updateResource(resourceId, file));

        verify(resourceRepository, times(1)).findById(resourceId);
        assertNull(exception.getMessage());
    }

    @Test
    void testDeleteResourceSuccessByOwner() {
        resource.setCreatedBy(teamMember);

        resourceService.deleteResource(resourceId);

        verify(s3Service, times(1)).deleteFile(key);
    }

    @Test
    void testDeleteResourceSuccessByManager() {
        teamMember.setRoles(List.of(TeamRole.MANAGER));
        resource.setCreatedBy(new TeamMember());

        resourceService.deleteResource(resourceId);

        verify(s3Service, times(1)).deleteFile(key);
    }

    @Test
    void testDeleteResourceNotPermited() {
        resource.setCreatedBy(new TeamMember());
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.ofNullable(resource));

        PermissionDeniedDataAccessException exception = assertThrows(PermissionDeniedDataAccessException.class,
                () -> resourceService.deleteResource(resourceId));

        assertEquals("You can't delete this file", exception.getMessage());
    }
}