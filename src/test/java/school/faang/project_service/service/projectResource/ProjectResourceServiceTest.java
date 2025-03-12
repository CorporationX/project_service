package school.faang.project_service.service.projectResource;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.amazonS3Service.AmazonS3Service;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.projectresource.ImageChecker;
import faang.school.projectservice.service.projectresource.ProjectResourceService;
import faang.school.projectservice.service.teamMember.TeamMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectResourceServiceTest {

    @Mock
    private AmazonS3Service amazonS3Service;

    @Mock
    private ProjectService projectService;

    @Mock
    private TeamMemberService teamMemberService;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private ImageChecker imageChecker;

    private Project project;

    @InjectMocks
    private ProjectResourceService projectResourceService;

    private TeamMember teamMember;
    private Resource resource;
    private MultipartFile file;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setStorageSize(BigInteger.valueOf(1024L * 1024L * 1024L));
        project.setMaxStorageSize(BigInteger.valueOf(2 * 1024L * 1024L * 1024L));

        teamMember = new TeamMember();
        teamMember.setUserId(1L);
        teamMember.setRoles(Collections.singletonList(TeamRole.MANAGER));

        resource = new Resource();
        resource.setId(1L);
        resource.setProject(project);
        resource.setCreatedBy(teamMember);
        resource.setSize(BigInteger.valueOf(1024L * 1024L * 1024L / 2));
        resource.setKey("test-key");
        resource.setStatus(ResourceStatus.ACTIVE);

        file = mock(MultipartFile.class);
    }

    @Test
    void testAddFileResourceTypeIsNotImage() {
        when(file.getSize()).thenReturn(1024L * 1024L * 1024L / 2);
        when(file.getOriginalFilename()).thenReturn("test-file.txt");
        when(projectService.getProject(anyLong())).thenReturn(project);
        when(teamMemberService.getTeamMemberByIdAndProjectId(anyLong(), anyLong())).thenReturn(
                teamMember);
        when(amazonS3Service.addResource(any(MultipartFile.class), anyString())).thenReturn(
                resource);
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);

        Resource result = projectResourceService.addFile(1L, 1L, file);

        assertNotNull(result);
        assertEquals(resource, result);
        assertEquals(BigInteger.valueOf(3 * 1024L * 1024L * 1024L / 2), project.getStorageSize());
        verify(projectService, times(1)).saveProject(project);
        verify(resourceRepository, times(1)).save(resource);
    }

    @Test
    void testAddFileExceedsStorage() {
        when(projectService.getProject(anyLong())).thenReturn(project);
        when(file.getSize()).thenReturn(3 * 1024L * 1024L * 1024L / 2);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            projectResourceService.addFile(1L, 1L, file);
        });

        assertEquals("Exceeded the 2GB volume", exception.getMessage());
    }

    @Test
    void testAddFileCheckImageSizeMoreThanFiveMb() {
        prepare();
        when(file.getSize()).thenReturn(6 * 1024L * 1024L);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            projectResourceService.addFile(1L, 3L, file);
        });

        assertEquals("The image size should not exceed 5 MB", exception.getMessage());
    }

    @Test
    void testAddFileCheckMoreThanFiftyImages() {
        List<String> galleryFileKeys = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            galleryFileKeys.add("image" + i + ".jpg");
        }
        project.setGalleryFileKeys(galleryFileKeys);
        prepare();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            projectResourceService.addFile(3L, 1L, file);
        });

        assertEquals("The number of images in the gallery can not exceed 50 pieces.", exception.getMessage());
    }

    @Test
    void testUpdateFile() {
        when(file.getSize()).thenReturn(1024L * 1024L * 1024L / 2);
        when(resourceRepository.findById(anyLong())).thenReturn(Optional.of(resource));
        when(teamMemberService.getTeamMemberByIdAndProjectId(anyLong(), anyLong())).thenReturn(
                teamMember);
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);

        Resource result = projectResourceService.updateFile(1L, 1L, file);

        assertNotNull(result);
        assertEquals(resource, result);
        assertEquals(BigInteger.valueOf(1024L * 1024L * 1024L), project.getStorageSize());
        verify(projectService, times(1)).saveProject(project);
        verify(resourceRepository, times(1)).save(resource);
        verify(amazonS3Service, times(1)).updateResource(any(MultipartFile.class), eq("test-key"));
    }

    @Test
    void testUpdateFileExceedsStorage() {
        when(resourceRepository.findById(anyLong())).thenReturn(Optional.of(resource));
        when(file.getSize()).thenReturn(
                5 * 1024L * 1024L * 1024L / 2); // Устанавливаем размер файла, который превышает лимит

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            projectResourceService.updateFile(1L, 1L, file);
        });

        assertEquals("Exceeded the 2GB volume", exception.getMessage());
    }

    @Test
    void testRemoveFile() {
        when(resourceRepository.findById(anyLong())).thenReturn(Optional.of(resource));
        when(teamMemberService.getTeamMemberByIdAndProjectId(anyLong(), anyLong())).thenReturn(
                teamMember);
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);

        Resource result = projectResourceService.removeFile(1L, 1L);

        assertNotNull(result);
        assertEquals(resource, result);
        assertEquals(ResourceStatus.DELETED, resource.getStatus());
        assertEquals(BigInteger.valueOf(1024L * 1024L * 1024L / 2), project.getStorageSize());
        assertEquals(BigInteger.valueOf(0), resource.getSize());
        assertNull(resource.getKey());
        verify(projectService, times(1)).saveProject(project);
        verify(resourceRepository, times(1)).save(resource);
        verify(amazonS3Service, times(1)).completeRemoval("test-key");
    }

    @Test
    void testRemoveFileAccessDenied() {
        TeamMember nonManager = new TeamMember();
        nonManager.setUserId(2L);
        nonManager.setRoles(Collections.singletonList(TeamRole.DEVELOPER));

        when(resourceRepository.findById(anyLong())).thenReturn(Optional.of(resource));
        when(teamMemberService.getTeamMemberByIdAndProjectId(anyLong(), anyLong())).thenReturn(
                nonManager);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            projectResourceService.removeFile(1L, 2L);
        });

        assertEquals("Access error, only the author or manager can delete the file",
                exception.getMessage());
    }

    private void prepare() {
        when(file.getContentType()).thenReturn("test-file.jpg");
        when(imageChecker.checkResourceTypeIsImage("test-file.jpg")).thenReturn(true);
        when(projectService.getProject(anyLong())).thenReturn(project);
        when(teamMemberService.getTeamMemberByIdAndProjectId(anyLong(), anyLong())).thenReturn(
                teamMember);
        when(amazonS3Service.addResource(any(MultipartFile.class), anyString())).thenReturn(
                resource);
    }
}
