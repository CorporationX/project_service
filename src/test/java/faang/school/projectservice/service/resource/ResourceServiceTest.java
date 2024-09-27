package faang.school.projectservice.service.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.exception.StorageLimitException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.resource.ResourceMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.subproject.ValidatorService;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {
    @Mock
    UserContext userContext;

    @Mock
    ValidatorService validatorService;

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
    private ResourceService resourceService;

    private Resource resource;
    private TeamMember teamMember;
    private Project project;
    private String key;

    @BeforeEach
    public void setUp() {
        project = new Project();
        project.setId(1L);
        project.setStorageSize(BigInteger.valueOf(200));
        project.setMaxStorageSize(BigInteger.valueOf(1000));

        teamMember = new TeamMember();
        teamMember.setId(1L);
        teamMember.setRoles(List.of(TeamRole.ANALYST));

        key = "someKey";
        resource = new Resource();
        resource.setProject(project);
        resource.setCreatedBy(teamMember);
        resource.setSize(BigInteger.valueOf(1));
        resource.setKey(key);

        lenient().when(teamMemberRepository.findById(1L)).thenReturn(teamMember);
        lenient().when(file.getSize()).thenReturn(200L);
        lenient().when(projectRepository.findById(1L)).thenReturn(project);
        lenient().when(userContext.getUserId()).thenReturn(1L);
        lenient().when(s3Service.uploadFile(file, project.getName())).thenReturn(resource);

    }

    @Test
    void testAddResourceSuccess() {
        when(resourceRepository.save(resource)).thenReturn(resource);

        ResourceDto resourceDto = resourceService.addResource(1L, file);

        verify(s3Service, times(1)).uploadFile(file, resource.getName());
        verify(resourceRepository, times(1)).save(resourceCaptor.capture());
        verify(validatorService, times(1)).isProjectExists(1L);
        verify(teamMemberRepository, times(1)).findById(1L);
        verify(projectRepository, times(1)).findById(1L);

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
                () -> resourceService.addResource(1L, file));

        assertEquals("Storage limit exceeded", exception.getMessage());
        verify(validatorService, times(1)).isProjectExists(1L);
        verify(projectRepository, times(1)).findById(1L);
        verify(teamMemberRepository, times(1)).findById(1L);
        verify(resourceRepository, never()).save(any(Resource.class));
    }

//    @Test
//    void testGetResource() {
//    }

    @Test
    void testUpdateResourceSuccess() {
        String newKey = "someNewKey";
        Resource newResource = new Resource();
        newResource.setKey(newKey);
        newResource.setSize(BigInteger.valueOf(file.getSize()));
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));
        when(s3Service.uploadFile(file, project.getName())).thenReturn(newResource);
        doNothing().when(s3Service).deleteFile(resource.getKey());

        ResourceDto result = resourceService.updateResource(1L, file);

        verify(s3Service, times(1)).deleteFile(key);
        verify(resourceRepository, times(1)).findById(1L);
        verify(teamMemberRepository, times(1)).findById(1L);
        verify(projectRepository, times(1)).findById(1L);
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
    void testDeleteResourceSuccessByOwner() {
        resource.setCreatedBy(teamMember);
        when(resourceRepository.findById(1L)).thenReturn(Optional.ofNullable(resource));

        resourceService.deleteResource(1L);

        verify(s3Service, times(1)).deleteFile(key);
    }

    @Test
    void testDeleteResourceSuccessByManager() {
        teamMember.setRoles(List.of(TeamRole.MANAGER));
        resource.setCreatedBy(new TeamMember());
        when(resourceRepository.findById(1L)).thenReturn(Optional.ofNullable(resource));

        resourceService.deleteResource(1L);

        verify(s3Service, times(1)).deleteFile(key);
    }

    @Test
    void testDeleteResourceNotPermited() {
        resource.setCreatedBy(new TeamMember());
        when(resourceRepository.findById(1L)).thenReturn(Optional.ofNullable(resource));

        PermissionDeniedDataAccessException exception = assertThrows(PermissionDeniedDataAccessException.class,
                () -> resourceService.deleteResource(1L));

        assertEquals("You can't delete this file", exception.getMessage());
    }


}