package school.faang.project_service.service.projectResource;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.amazonS3Service.AmazonS3Service;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.projectResource.ProjectResourceService;
import faang.school.projectservice.service.teamMember.TeamMemberService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectResourceServiceTest {

    @Mock
    private AmazonS3Service amazonS3Service;

    @Mock
    private ProjectService projectService;

    @Mock
    private TeamMemberService teamMemberService;

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private ProjectResourceService projectResourceService;

    private Project project;
    private TeamMember teamMember;
    private Resource resource;
    private MultipartFile file;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setStorageSize(BigInteger.valueOf(1000));
        project.setMaxStorageSize(BigInteger.valueOf(2000));

        teamMember = new TeamMember();
        teamMember.setUserId(1L);
        teamMember.setRoles(Collections.singletonList(TeamRole.MANAGER));

        resource = new Resource();
        resource.setId(1L);
        resource.setProject(project);
        resource.setCreatedBy(teamMember);
        resource.setSize(BigInteger.valueOf(500));
        resource.setKey("test-key");
        resource.setStatus(ResourceStatus.ACTIVE);

        file = mock(MultipartFile.class);
    }

    @Test
    void testAddFile() {
        when(file.getSize()).thenReturn(500L);
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
        assertEquals(BigInteger.valueOf(1500), project.getStorageSize());
        verify(projectService, times(1)).saveProject(project);
        verify(resourceRepository, times(1)).save(resource);
    }

    @Test
    void testAddFileExceedsStorage() {
        when(projectService.getProject(anyLong())).thenReturn(project);
        when(file.getSize()).thenReturn(1500L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            projectResourceService.addFile(1L, 1L, file);
        });

        assertEquals("Exceeded the 2GB volume", exception.getMessage());
    }

    @Test
    void testUpdateFile() {
        when(file.getSize()).thenReturn(500L);

        when(resourceRepository.findById(anyLong())).thenReturn(Optional.of(resource));
        when(teamMemberService.getTeamMemberByIdAndProjectId(anyLong(), anyLong())).thenReturn(
            teamMember);
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);

        Resource result = projectResourceService.updateFile(1L, 1L, file);

        assertNotNull(result);
        assertEquals(resource, result);
        assertEquals(BigInteger.valueOf(1000), project.getStorageSize());
        verify(projectService, times(1)).saveProject(project);
        verify(resourceRepository, times(1)).save(resource);
        verify(amazonS3Service, times(1)).updateResource(any(MultipartFile.class), eq("test-key"));
    }

    @Test
    void testUpdateFileExceedsStorage() {
        when(resourceRepository.findById(anyLong())).thenReturn(Optional.of(resource));
        when(file.getSize()).thenReturn(
            2500L); // Устанавливаем размер файла, который превышает лимит

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
        assertEquals(BigInteger.valueOf(500), project.getStorageSize());
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

}
