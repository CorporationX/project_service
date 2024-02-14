package faang.school.projectservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {
    @InjectMocks
    private ResourceService resourceService;
    @Mock
    private ProjectService projectService;
    @Mock
    private S3Service s3Service;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private ResourceMapper resourceMapper;

    private final Project project = new Project();
    private final Resource resource = new Resource();
    Long projectId = 1L;
    long userId = 1L;
    Long resourceId = 1L;

    @Test
    void testUserIdExistsIsInvalid() {
        MockMultipartFile file = prepareMultipartFile();
        Project project = prepareProject();

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> resourceService.addResource(projectId, file, userId));

        assertEquals(illegalArgumentException.getMessage(), "There are no this user in team");
    }

    @Test
    void testStorageExceededIsInvalid() {
        MockMultipartFile file = prepareMultipartFile();
        Project project = prepareProject();

        project.setStorageSize(BigInteger.valueOf(2049));
        when(projectService.getProjectById(projectId)).thenReturn(project);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> resourceService.addResource(projectId, file, userId));
        assertEquals(illegalArgumentException.getMessage(), "Storage size exceeded. Buy a subscription to increase the size of storage");
    }

    @Test
    void testAddResourceFile() {
        MockMultipartFile file = prepareMultipartFile();

        Project project = prepareProject();

        when(s3Service.uploadFile(any(MultipartFile.class), any(String.class))).thenReturn(new Resource());
        when(teamMemberRepository.findById(userId)).thenReturn(new TeamMember());

        resourceService.addResource(projectId, file, userId);

        verify(s3Service, times(1)).uploadFile(any(MultipartFile.class), any(String.class));
        verify(resourceRepository, times(1)).save(any(Resource.class));
    }

    @Test
    void testResourceExistsIsInvalid() {
        prepareResource();

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> resourceService.deleteResource(resourceId, userId));
        assertEquals(illegalArgumentException.getMessage(), "There are no resource with this ID in DB");
    }

    @Test
    void testDeleteResourcePermissionsIsInvalid() {
        TeamMember teamMember = prepareResourceForDeleting();
        teamMember.setId(2L);
        teamMember.setRoles(List.of(TeamRole.ANALYST));

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> resourceService.deleteResource(resourceId, userId));
        assertEquals(illegalArgumentException.getMessage(), "Only author of resource and manager can update the file");
    }

    @Test
    void testDeleteFromMinio() {
        TeamMember teamMember = prepareResourceForDeleting();

        resourceService.deleteResource(resourceId, userId);
        verify(s3Service, times(1)).deleteFile(any());
    }

    @Test
    void testDeleteFromDB() {
        TeamMember teamMember = prepareResourceForDeleting();

        resourceService.deleteResource(resourceId, userId);
        verify(resourceRepository, times(1)).delete(resource);
    }

    private MockMultipartFile prepareMultipartFile() {
        return new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());
    }

    private Project prepareProject() {
        Project project = new Project();
        project.setId(projectId);
        project.setStorageSize(BigInteger.ZERO);
        project.setMaxStorageSize(BigInteger.valueOf(1024));

        when(projectService.getProjectById(projectId)).thenReturn(project);
        return project;
    }

    private void prepareResource() {
        String key = "resourceKey";
        resource.setId(resourceId);
        resource.setKey(key);
        resource.setSize(BigInteger.valueOf(100));
        resource.setProject(project);

        project.setStorageSize(BigInteger.valueOf(5));
        project.setMaxStorageSize(BigInteger.valueOf(10));
    }

    private TeamMember prepareResourceForDeleting() {
        TeamMember teamMember = new TeamMember();
        teamMember.setId(1L);

        prepareResource();
        resource.setCreatedBy(teamMember);
        resource.setUpdatedBy(teamMember);

        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        return teamMember;
    }
}
