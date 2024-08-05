package faang.school.projectservice.service.resource;

import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.s3.S3Service;
import faang.school.projectservice.validator.resource.ResourceValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {
    @Mock
    private S3Service s3Service;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private ResourceValidator resourceValidator;
    @Mock
    private ResourceMapper resourceMapper;
    @Mock
    private ProjectRepository projectRepository;
    @InjectMocks
    private ResourceService resourceService;
    private String bucketName = null;
    @Captor
    private ArgumentCaptor<Resource> resourceCaptor;
//    @Test
//    public void testAdd(){
//        MockMultipartFile file = new MockMultipartFile("file",
//                "filename.txt", "text/plain", "some content".getBytes());
//        Project project = new Project();
//        project.setId(1L);
//        project.setStorageSize(BigInteger.ZERO);
//        Resource resource = new Resource();
//
//        //Mockito.when(projectRepository.getProjectById(project.getId())).thenReturn(project);
//        ResourceDto result = resourceService.add(file,null,1L);
//        Assertions.assertEquals(resourceMapper.toDto(resource),result);
//    }

    @Test
    void testAdd() {
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "some content".getBytes());
        List<TeamRole> allowedTeamRoles = List.of(TeamRole.DEVELOPER);
        Project project = new Project();
        project.setStorageSize(BigInteger.ZERO);
        TeamMember teamMember = new TeamMember();
        Resource resource = new Resource();
        resource.setId(1L);
        resource.setName(file.getOriginalFilename());
        resource.setKey("filename.txt" + LocalDateTime.now());
        ResourceDto resourceDto = new ResourceDto();

        when(userContext.getUserId()).thenReturn(1L);
        when(projectRepository.getProjectById(anyLong())).thenReturn(project);
        doNothing().when(resourceValidator).validateStorageOverflow(any(Project.class), any(MultipartFile.class));
        when(resourceValidator.getTeamMemberAndValidate(any(Project.class), anyLong())).thenReturn(teamMember);
        doNothing().when(resourceValidator).getAllowedRolesAndValidate(anyList(), any(TeamMember.class));
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);
        when(resourceMapper.toDto(any(Resource.class))).thenReturn(resourceDto);

        ResourceDto result = resourceService.add(file, allowedTeamRoles, 1L);
        assertEquals(resourceDto, result);
        verify(s3Service, times(1)).uploadFile(eq(file), eq(bucketName), anyString());
        verify(resourceRepository, times(1)).save(any(Resource.class));
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void testGet() {
        Resource resource = new Resource();
        resource.setId(1L);
        resource.setName("filename.txt");
        resource.setKey("filename.txt");
        Project project = new Project();
        project.setId(1L);
        TeamMember teamMember = new TeamMember();

        ByteArrayInputStream inputStream = new ByteArrayInputStream("dummy content".getBytes());
        S3Object s3Object = new S3Object();
        s3Object.setObjectContent(inputStream);
        s3Object.getObjectMetadata().setContentLength(12);
        s3Object.getObjectMetadata().setContentType("text/plain");

        when(userContext.getUserId()).thenReturn(1L);
        when(resourceValidator.getResourceAndValidate(anyLong())).thenReturn(resource);
        when(resourceValidator.getProjectAndValidate(anyLong(), any(Resource.class))).thenReturn(project);
        when(resourceValidator.getTeamMemberAndValidate(any(Project.class), anyLong())).thenReturn(teamMember);
        doNothing().when(resourceValidator).validateCanUserDownload(any(Resource.class), any(TeamMember.class));
        doNothing().when(resourceValidator).validateIsFileExist(eq(bucketName), anyString());
        when(s3Service.downloadFile(eq(bucketName), anyString())).thenReturn(s3Object);

        ResponseEntity<InputStreamResource> result = resourceService.get(1L, 1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(12, result.getHeaders().getContentLength());
        assertEquals(MediaType.parseMediaType("text/plain"), result.getHeaders().getContentType());
    }

    @Test
    void testRemove() {
        Resource resource = new Resource();
        resource.setId(1L);
        resource.setKey("filename.txt");
        resource.setSize(BigInteger.TEN);
        Project project = new Project();
        project.setStorageSize(BigInteger.TEN);
        TeamMember teamMember = new TeamMember();
        ResourceDto resourceDto = new ResourceDto();

        when(userContext.getUserId()).thenReturn(1L);
        when(resourceValidator.getResourceAndValidate(anyLong())).thenReturn(resource);
        doNothing().when(resourceValidator).validateIsFileExist(eq(bucketName), anyString());
        when(resourceValidator.getProjectAndValidate(anyLong(), any(Resource.class))).thenReturn(project);
        when(resourceValidator.getTeamMemberAndValidate(any(Project.class), anyLong())).thenReturn(teamMember);
        doNothing().when(resourceValidator).validateCanUserRemoveOrUpdate(any(TeamMember.class), any(Resource.class));
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);
        when(resourceMapper.toDto(any(Resource.class))).thenReturn(resourceDto);

        ResourceDto result = resourceService.remove(1L, 1L);
        assertEquals(resourceDto, result);
        verify(s3Service, times(1)).deleteFile(eq(bucketName), anyString());
        verify(resourceRepository, times(1)).save(resourceCaptor.capture());
        verify(projectRepository, times(1)).save(any(Project.class));
        Resource capturedResource = resourceCaptor.getValue();
        assertEquals(BigInteger.ZERO, capturedResource.getSize());
        assertEquals(ResourceStatus.DELETED, capturedResource.getStatus());
    }

    @Test
    void testUpdate() {
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "some content".getBytes());
        List<TeamRole> allowedTeamRoles = List.of(TeamRole.DEVELOPER);
        ResourceStatus resourceStatus = ResourceStatus.ACTIVE;
        Project project = new Project();
        project.setStorageSize(BigInteger.TEN);
        Resource resource = new Resource();
        resource.setId(1L);
        resource.setKey("filename.txt");
        resource.setSize(BigInteger.TEN);
        TeamMember teamMember = new TeamMember();
        ResourceDto resourceDto = new ResourceDto();

        doNothing().when(resourceValidator).validateIsUpdateRequired(any(MultipartFile.class), anyList());
        when(userContext.getUserId()).thenReturn(1L);
        when(resourceValidator.getResourceAndValidate(anyLong())).thenReturn(resource);
        when(resourceValidator.getProjectAndValidate(anyLong(), any(Resource.class))).thenReturn(project);
        doNothing().when(resourceValidator).validateIsFileExist(eq(bucketName), anyString());
        when(resourceValidator.getTeamMemberAndValidate(any(Project.class), anyLong())).thenReturn(teamMember);
        doNothing().when(resourceValidator).validateCanUserRemoveOrUpdate(any(TeamMember.class), any(Resource.class));
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);
        when(resourceMapper.toDto(any(Resource.class))).thenReturn(resourceDto);

        ResourceDto result = resourceService.update(file, allowedTeamRoles, 1L, 1L, resourceStatus);
        assertEquals(resourceDto, result);
        verify(s3Service, times(1)).deleteFile(eq(bucketName), anyString());
        verify(s3Service, times(1)).uploadFile(eq(file), eq(bucketName), anyString());
        verify(resourceRepository, times(1)).save(any(Resource.class));
        verify(projectRepository, times(1)).save(any(Project.class));
    }
}
