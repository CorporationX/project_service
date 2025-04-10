package faang.school.projectservice.resourceService;

import faang.school.projectservice.exception.AccessToDeniedException;
import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.exception.ResourceNotFoundException;
import faang.school.projectservice.exception.StorageLimitExceededException;
import faang.school.projectservice.exception.UserNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.MinioService;
import faang.school.projectservice.service.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static faang.school.projectservice.constants.Constants.BASE_MAX_STORAGE_BYTES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {
    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MinioService minioService;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private ResourceService resourceService;

    private static final Long PROJECT_ID = 1L;
    private static final Long USER_ID = 2L;
    private static final BigInteger FILE_SIZE = BigInteger.TEN;
    public static final MultipartFile FILE = new MockMultipartFile("file", "test.pdf", "application/pdf", new byte[10]);

    private Project project;
    private TeamMember member;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(PROJECT_ID);
        project.setStorageSize(BigInteger.ZERO);
        project.setHasExtendedStorage(false);
        project.setMemberRoles(Map.of());

        member = new TeamMember();
        member.setId(USER_ID);
        member.setRoles(Set.of(TeamRole.DEVELOPER));
    }

    @Test
    public void testUploadFileSuccessful() throws IOException {
        project.setMemberRoles(Map.of(member, Set.of(TeamRole.DEVELOPER)));

        String expectedKey = String.format("project %d/random key", PROJECT_ID);

        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        when(teamMemberRepository.findById(USER_ID)).thenReturn(Optional.of(member));
        doNothing().when(minioService).uploadFile(anyString(), eq(FILE));
        when(resourceRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Resource result = resourceService.uploadFile(FILE, USER_ID, PROJECT_ID);

        assertNotNull(result);
        assertEquals(FILE_SIZE, result.getSize());
        assertEquals(ResourceStatus.ACTIVE, result.getStatus());
        assertEquals(member, result.getCreatedBy());
        assertEquals(project, result.getProject());
        assertNotNull(result.getKey());

        verify(minioService).uploadFile(anyString(), eq(FILE));
        verify(projectRepository).save(project);
        verify(resourceRepository).save(any());
    }

    @Test
    public void testUploadFile_userNotInProject() {
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        when(teamMemberRepository.findById(USER_ID)).thenReturn(Optional.of(member));

        assertThrows(AccessToDeniedException.class, () -> resourceService.uploadFile(FILE, USER_ID, PROJECT_ID));
    }

    @Test
    public void testUploadFile_projectNotFound() {
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class,
                () -> resourceService.uploadFile(FILE, USER_ID, PROJECT_ID));
    }

    @Test
    public void testUploadFile_userNotFound() {
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        when(teamMemberRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> resourceService.uploadFile(FILE, USER_ID, PROJECT_ID));
    }

    @Test
    public void testUploadFile_exceedsLimit() {
        project.setStorageSize(BASE_MAX_STORAGE_BYTES);
        project.setMemberRoles(Map.of(member, Set.of(TeamRole.DEVELOPER)));

        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        when(teamMemberRepository.findById(USER_ID)).thenReturn(Optional.of(member));

        assertThrows(StorageLimitExceededException.class, () -> resourceService.uploadFile(FILE, USER_ID, PROJECT_ID));
    }

    @Test
    public void testDeleteFile_success_byCreator() {
        member.setId(USER_ID);
        member.setRoles(Set.of(TeamRole.DEVELOPER));

        project.setStorageSize(FILE_SIZE);
        project.setMemberRoles(Map.of(member, Set.of(TeamRole.DEVELOPER)));

        Resource resource = new Resource();
        resource.setId(1L);
        resource.setProject(project);
        resource.setKey("valid-key");
        resource.setSize(FILE_SIZE);
        resource.setCreatedBy(member);
        resource.setStatus(ResourceStatus.ACTIVE);

        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));
        when(teamMemberRepository.findById(USER_ID)).thenReturn(Optional.of(member));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        resourceService.deleteFile(1L, USER_ID);

        verify(minioService).deleteFile("valid-key");
        verify(resourceRepository).save(any());
        verify(projectRepository).save(project);
        assertEquals(1, project.getStorageSize().compareTo(BigInteger.ZERO));
    }

    @Test
    public void testDeleteFile_success_byManager() {
        TeamMember manager = new TeamMember();
        manager.setId(USER_ID);
        manager.setRoles(Set.of(TeamRole.MANAGER));
        project.setMemberRoles(Map.of(manager, Set.of(TeamRole.MANAGER)));

        Resource resource = new Resource();
        resource.setId(1L);
        resource.setProject(project);
        resource.setKey("key");
        resource.setSize(FILE_SIZE);
        resource.setCreatedBy(member);
        resource.setStatus(ResourceStatus.ACTIVE);

        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));
        when(teamMemberRepository.findById(USER_ID)).thenReturn(Optional.of(manager));

        resourceService.deleteFile(1L, USER_ID);

        verify(minioService).deleteFile("key");
        verify(resourceRepository).save(any());
        verify(projectRepository).save(project);
    }

    @Test
    public void testDeleteFile_userNotFound() {
        Resource resource = new Resource();
        resource.setId(1L);
        resource.setProject(project);

        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));
        when(teamMemberRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> resourceService.deleteFile(1L, USER_ID));
    }

    @Test
    public void testDeleteFile_noPermission() {
        TeamMember other = new TeamMember();
        other.setId(USER_ID);
        other.setRoles(Set.of(TeamRole.DEVELOPER));

        project.setMemberRoles(Map.of(other, Set.of(TeamRole.DEVELOPER)));

        Resource resource = new Resource();
        resource.setId(1L);
        resource.setProject(project);
        resource.setCreatedBy(new TeamMember());

        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));
        when(teamMemberRepository.findById(USER_ID)).thenReturn(Optional.of(other));

        assertThrows(AccessToDeniedException.class, () -> resourceService.deleteFile(1L, USER_ID));
    }

    @Test
    public void testDeleteFile_notFound() {
        when(resourceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> resourceService.deleteFile(1L, USER_ID));
    }
}
