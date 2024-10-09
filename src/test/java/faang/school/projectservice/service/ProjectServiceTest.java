package faang.school.projectservice.service;


import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.resource.ResourceManager;
import faang.school.projectservice.service.s3manager.S3Manager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    private Project project;
    private Long projectId;
    private ResourceDB resourceDBOfTM_1;
    private Team team;
    private Long resourceId;
    private Long userId_1;
    private Long userId_2;
    private TeamMember teamMember_1;
    private TeamMember teamMember_2;
    @Mock
    private MultipartFile file;
    private final ProjectRepository projectRepository = Mockito.mock(ProjectRepository.class);
    private final ResourceManager resourceManager = Mockito.mock(ResourceManager.class);
    private final S3Manager s3Manager = Mockito.mock(S3Manager.class);


    private final String DIRECTORY_NAME = "projects";
    private final long MAX_STORAGE_SIZE = 2147483648L;

    private final ProjectService projectService = new ProjectService(projectRepository,
            resourceManager,
            DIRECTORY_NAME,
            MAX_STORAGE_SIZE);

    @BeforeEach
    public void setUp() {
        projectId = 100L;
        userId_1 = 1L;
        userId_2 = 2L;
        resourceId = 10L;

        teamMember_1 = new TeamMember();
        teamMember_1.setUserId(userId_1);
        teamMember_1.setRoles(List.of(TeamRole.ANALYST));

        teamMember_2 = new TeamMember();
        teamMember_2.setUserId(userId_2);
        teamMember_2.setRoles(List.of(TeamRole.MANAGER));

        team = new Team();
        team.setTeamMembers(List.of(teamMember_1, teamMember_2));

        project = new Project();
        project.setId(projectId);
        project.setTeams(List.of(team));
        project.setStorageSize(BigInteger.ZERO);

        resourceDBOfTM_1 = new ResourceDB();
        resourceDBOfTM_1.setId(resourceId);
        resourceDBOfTM_1.setCreatedBy(teamMember_1);
        resourceDBOfTM_1.setProject(project);
        resourceDBOfTM_1.setSize(BigInteger.ONE);

        project.setResourceDBS(List.of(resourceDBOfTM_1));

    }

    @Test
    public void testGetFileFromProject_Success() {
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(resourceManager.getResource(any(String.class))).thenReturn(mock(Resource.class));

        ResourceInfo result = projectService.getFileFromProject(projectId, userId_1, resourceId);

        assertEquals(resourceDBOfTM_1, result.resourceDB());
        assertNotNull(result.resourceDB());
    }

    @Test
    public void testGetFileFromProject_NotSuccess_UserNotParticipatingInProject() {
        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> projectService.getFileFromProject(projectId, 10000L, resourceId));
        assertEquals("User is not a part of the project", exception.getMessage());
    }

    @Test
    public void testDeleteFileFromProject_Success_UserIsCreator() {
        project.setStorageSize(BigInteger.ONE);

        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(resourceManager.getResourceById(resourceId)).thenReturn(resourceDBOfTM_1);


        projectService.deleteFileFromProject(projectId, userId_1, resourceId);

        assertEquals(BigInteger.ZERO, project.getStorageSize());

        verify(resourceManager).deleteFileFromProject(resourceDBOfTM_1);
        verify(projectRepository).save(project);
    }


    @Test
    public void testDeleteFileFromProject_Success_UserIsNotCreator_UserIsManager() {
        project.setStorageSize(BigInteger.ONE);

        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(resourceManager.getResourceById(resourceId)).thenReturn(resourceDBOfTM_1);


        projectService.deleteFileFromProject(projectId, userId_2, resourceId);

        assertEquals(BigInteger.ZERO, project.getStorageSize());

        verify(resourceManager).deleteFileFromProject(resourceDBOfTM_1);
        verify(projectRepository).save(project);
    }

    @Test
    public void testDeleteFileFromProject_NotSuccess_UserNotAllowedToDeleteFile() {
        teamMember_2.setRoles(List.of(TeamRole.ANALYST));
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(resourceManager.getResourceById(resourceId)).thenReturn(resourceDBOfTM_1);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            projectService.deleteFileFromProject(projectId, userId_2, resourceId);
        });

        assertEquals("User is not allowed to delete this file", exception.getMessage());
    }


    @Test
    public void uploadFileToProject_Success() {
        long fileSize = resourceDBOfTM_1.getSize().longValue();
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(file.getSize()).thenReturn(fileSize);
        when(file.isEmpty()).thenReturn(false);

        when(resourceManager.uploadResource(eq(file), any(String.class), eq(project), any(TeamMember.class)))
                .thenReturn(new ResourceInfo(mock(Resource.class), resourceDBOfTM_1));

        ResourceInfo uploadedResourceInfo = projectService.uploadFileToProject(projectId, userId_1, file);

        assertNotNull(uploadedResourceInfo);
        assertEquals(resourceDBOfTM_1, uploadedResourceInfo.resourceDB());
        assertEquals(fileSize,project.getStorageSize().longValue());
        verify(projectRepository).save(project);
        verify(resourceManager).uploadResource(eq(file), any(String.class), eq(project), any(TeamMember.class));
    }


    @Test
    public void uploadFileToProject_FileSizeExceedsLimit_ShouldThrowException() {
        long fileSize = 9999999999L;
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(file.getSize()).thenReturn(fileSize);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            projectService.uploadFileToProject(projectId, userId_1, file);
        });
        assertEquals("File size exceeds project limit", exception.getMessage());
    }

    @Test
    public void uploadFileToProject_UserNotInTeam_ShouldThrowException() {
        long fileSize = resourceDBOfTM_1.getSize().longValue();
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(file.getSize()).thenReturn(fileSize);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            projectService.uploadFileToProject(projectId, 10000L, file);
        });
        assertEquals("User is not a part of the project", exception.getMessage());
    }
}
