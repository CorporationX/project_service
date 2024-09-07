package faang.school.projectservice.service.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.validator.resource.ResourcePermissionValidator;
import faang.school.projectservice.validator.resource.ResourceSizeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.mockito.Mock;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ResourceSizeValidator sizeValidator;
    @Mock
    private ResourceMapper resourceMapper;
    @Mock
    private S3Service s3Service;
    @Mock
    private UserContext userContext;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private ResourceMediaType resourceMediaType;
    @Mock
    private ResourcePermissionValidator resourcePermissionValidator;

    @InjectMocks
    private ResourceService resourceService;
    private Long resourceId;
    private Long userId;
    private Long projectId;

    @BeforeEach
    void setUp() {
        resourceId = 1L;
        userId = 2L;
        projectId = 1L;
    }

    @Test
    void addResourceTest() {
        MultipartFile file = mock(MultipartFile.class);
        Project project = new Project();
        project.setId(projectId);
        project.setStorageSize(BigInteger.ZERO);
        project.setMaxStorageSize(BigInteger.valueOf(1000000L));

        TeamMember teamMember = new TeamMember();
        ResourceDto resourceDto = new ResourceDto();

        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(s3Service.uploadFile(any(), anyString())).thenReturn("testKey");
        when(userContext.getUserId()).thenReturn(userId);
        when(teamMemberRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(teamMember);
        when(resourceMapper.toDto(any(Resource.class))).thenReturn(resourceDto);

        ResourceDto result = resourceService.addResource(projectId, file);

        verify(sizeValidator, times(1)).checkNullSizeOfProject(project);
        verify(s3Service, times(1)).uploadFile(any(), anyString());
        verify(resourceRepository, times(1)).save(any(Resource.class));
        verify(projectRepository, times(1)).save(project);
        assertEquals(resourceDto, result);
    }

    @Test
    void downloadResourceTest() {
        Resource resource = new Resource();
        resource.setName("test.txt");
        resource.setKey("testKey");

        InputStream inputStream = new ByteArrayInputStream("Test data".getBytes());
        MediaType mediaType = MediaType.TEXT_PLAIN;

        when(resourceRepository.getReferenceById(resourceId)).thenReturn(resource);
        when(s3Service.downloadResource(resource.getKey())).thenReturn(inputStream);
        when(resourceMediaType.getMediaType(resource.getName())).thenReturn(mediaType);

        Pair<MediaType, InputStream> result = resourceService.downloadResource(resourceId);

        assertEquals(mediaType, result.getFirst());
        assertEquals(inputStream, result.getSecond());
        verify(resourceRepository, times(1)).getReferenceById(resourceId);
    }

    @Test
    void deleteResourceTest() {
        userId = 2L;

        Project project = new Project();
        project.setId(projectId);
        project.setStorageSize(BigInteger.valueOf(1000L));

        Resource resource = new Resource();
        resource.setKey("testKey");
        resource.setSize(BigInteger.valueOf(100L));
        resource.setProject(project);

        when(userContext.getUserId()).thenReturn(userId);
        when(resourcePermissionValidator.getResourceWithPermission(resourceId, userId)).thenReturn(resource);

        resourceService.deleteResource(resourceId);

        // Verify interactions
        verify(s3Service, times(1)).deleteFromBucket(resource.getKey());
        verify(resourceRepository, times(1)).deleteById(resourceId);

        assertEquals(BigInteger.valueOf(900L), project.getStorageSize());
    }

    @Test
    void updateResourceTest() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getSize()).thenReturn(500L);
        when(file.getOriginalFilename()).thenReturn("updatedFile.txt");
        when(file.getContentType()).thenReturn(MediaType.TEXT_PLAIN_VALUE);

        Resource resourceFromDb = new Resource();
        resourceFromDb.setKey("oldKey");
        resourceFromDb.setName("oldFile.txt");
        resourceFromDb.setSize(BigInteger.valueOf(300L));

        Project project = new Project();
        project.setId(projectId);
        project.setName("TestProject");
        project.setStorageSize(BigInteger.valueOf(1000L));
        project.setMaxStorageSize(BigInteger.valueOf(5000L));
        resourceFromDb.setProject(project);

        Resource resource = new Resource();
        resource.setKey("newKey");

        ResourceDto resourceDto = new ResourceDto();

        when(userContext.getUserId()).thenReturn(userId);
        when(resourcePermissionValidator.getResourceWithPermission(resourceId, userId)).thenReturn(resourceFromDb);
        when(s3Service.uploadFile(file, project.getId() + project.getName())).thenReturn("newKey");
        when(resourceMapper.toDto(any(Resource.class))).thenReturn(resourceDto);

        ResourceDto result = resourceService.updateResource(resourceId, file);

        verify(s3Service, times(1)).deleteFromBucket("oldKey");
        verify(s3Service, times(1)).uploadFile(file, project.getId() + project.getName());
        verify(resourceRepository, times(1)).save(resourceFromDb);
        verify(projectRepository, times(1)).save(project);

        assertEquals(resourceDto, result);
        assertEquals("newKey", resourceFromDb.getKey());
        assertEquals("updatedFile.txt", resourceFromDb.getName());
        assertEquals(BigInteger.valueOf(500L), resourceFromDb.getSize());
    }
}



