package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.resource.ResourceMediaType;
import faang.school.projectservice.service.resource.ResourceService;
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
    private ProjectService projectService;
    @Mock
    private ProjectMapper projectMapper;
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

        Resource resource = new Resource();
        TeamMember teamMember = new TeamMember();
        ProjectUpdateDto projectUpdateDto = new ProjectUpdateDto();
        ResourceDto resourceDto = new ResourceDto();

        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(s3Service.uploadFile(file, project.getId() + project.getName())).thenReturn(resource);
        when(userContext.getUserId()).thenReturn(userId);
        when(teamMemberRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(teamMember);
        when(projectMapper.toUpdateDto(project, BigInteger.valueOf(file.getSize()))).thenReturn(projectUpdateDto);
        when(resourceMapper.toDto(resource)).thenReturn(resourceDto);

        ResourceDto result = resourceService.addResource(projectId, file);

        verify(sizeValidator, times(1)).checkNullSizeOfProject(project);
        verify(s3Service, times(1)).uploadFile(file, project.getId() + project.getName());
        verify(resourceRepository, times(1)).save(resource);
        verify(projectService, times(1)).updateProject(projectId, projectUpdateDto);
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
        Resource resource = new Resource();
        resource.setKey("testKey");

        when(resourcePermissionValidator.getResourceWithPermission(resourceId, userId)).thenReturn(resource);

        resourceService.deleteResource(resourceId, userId);

        verify(s3Service, times(1)).deleteFromBucket(resource.getKey());
        verify(resourceRepository, times(1)).deleteById(resourceId);
    }

    @Test
    void updateResourceTest() {
        MultipartFile file = mock(MultipartFile.class);

        Resource resourceFromDb = new Resource();
        resourceFromDb.setKey("oldKey");
        resourceFromDb.setName("test.txt");
        Project project = new Project();
        project.setId(projectId);
        project.setStorageSize(BigInteger.ZERO);
        project.setMaxStorageSize(BigInteger.valueOf(1000000L));

        resourceFromDb.setProject(project);

        Resource resource = new Resource();
        resource.setKey("newKey");
        ProjectUpdateDto projectUpdateDto = new ProjectUpdateDto();
        ResourceDto resourceDto = new ResourceDto();

        when(resourcePermissionValidator.getResourceWithPermission(resourceId, userId)).thenReturn(resourceFromDb);
        when(s3Service.uploadFile(file, project.getId() + project.getName())).thenReturn(resource);
        when(projectMapper.toUpdateDto(project, BigInteger.valueOf(file.getSize()))).thenReturn(projectUpdateDto);
        when(resourceMapper.toDto(resource)).thenReturn(resourceDto);

        ResourceDto result = resourceService.updateResource(resourceId, userId, file);

        verify(s3Service, times(1)).deleteFromBucket("oldKey");
        verify(s3Service, times(1)).uploadFile(file, project.getId() + project.getName());
        verify(resourceRepository, times(1)).save(resourceFromDb);
        verify(projectService, times(1)).updateProject(project.getId(), projectUpdateDto);

        assertEquals(resourceDto, result);
    }
}
