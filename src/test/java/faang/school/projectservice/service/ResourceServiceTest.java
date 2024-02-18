package faang.school.projectservice.service;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {

    @Mock
    private ProjectService projectService;
    @Mock
    private S3Service s3Service;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private ResourceMapper resourceMapper;
    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private ResourceService resourceService;

    private Project project = new Project();
    private Resource resource = new Resource();

    @Test
    void testDownloadResourceFile() {
        long resourceId = 1L;
        String mockKey = "mockKey";
        InputStream mockStream = mock(InputStream.class);

        when(resourceRepository.getReferenceById(resourceId)).thenReturn(new Resource() {{
            setId(resourceId);
            setKey(mockKey);
        }});
        when(s3Service.downloadFile(mockKey)).thenReturn(mockStream);

        InputStream resultStream = resourceService.downloadResourceFile(resourceId);

        assertNotNull(resultStream, "The download stream should not be null");
        verify(s3Service, times(1)).downloadFile(mockKey);
    }

    @Test
    void testAddResourceFile() {
        Long projectId = 1L;
        long userId = 1L;
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());

        project.setId(projectId);
        project.setName("Test Project");
        project.setStorageSize(BigInteger.ZERO);
        project.setMaxStorageSize(BigInteger.valueOf(1024));

        when(projectService.getProjectModelById(projectId)).thenReturn(project);
        when(s3Service.uploadFile(any(MultipartFile.class), any(String.class))).thenReturn(new Resource());
        when(resourceMapper.toDto(any(Resource.class))).thenReturn(new ResourceDto());
        when(teamMemberRepository.findById(userId)).thenReturn(new TeamMember());

        ResourceDto dto = resourceService.addResourceFile(projectId, file, userId);

        assertNotNull(dto, "ResourceDto should not be null");
        verify(s3Service, times(1)).uploadFile(any(MultipartFile.class), any(String.class));
    }

    @Test
    void testDeleteResourceFile() {
        Long resourceId = 1L;
        long userId = 1L;
        String mockKey = "resourceKey";

        resource.setId(resourceId);
        resource.setKey(mockKey);
        resource.setSize(BigInteger.valueOf(100));
        resource.setProject(project);
        project.setStorageSize(BigInteger.valueOf(5));
        project.setMaxStorageSize(BigInteger.valueOf(10));

        when(resourceRepository.getReferenceById(resourceId)).thenReturn(resource);
        when(teamMemberRepository.findById(userId)).thenReturn(new TeamMember());

        resourceService.deleteResourceFile(resourceId, userId);

        verify(s3Service, times(1)).deleteFile(mockKey);
        assertEquals(ResourceStatus.DELETED, resource.getStatus());
    }

    @Test
    void addResourceFile_ThrowsException_WhenStorageLimitExceeded() {
        Long projectId = 1L;
        long userId = 1L;
        MultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", new byte[10]);

        project.setId(projectId);
        project.setStorageSize(BigInteger.valueOf(5));
        project.setMaxStorageSize(BigInteger.valueOf(10));

        when(projectService.getProjectModelById(projectId)).thenReturn(project);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> resourceService.addResourceFile(projectId, file, userId));

        assertTrue(thrown.getMessage().contains("Storage size exceeded"));
    }

}
