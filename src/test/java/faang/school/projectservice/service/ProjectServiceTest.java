package faang.school.projectservice.service;


import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.resource.ResourceManager;
import faang.school.projectservice.service.team.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.InputStreamSource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    private Project project;
    private Long projectId;
    private Resource resourceOfTM_1;
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
    private final TeamService teamService = Mockito.mock(TeamService.class);


    private final String DIRECTORY_NAME = "projects";
    private final long MAX_STORAGE_SIZE = 2147483648L;

    private final ProjectService projectService = new ProjectService(projectRepository,
            resourceManager,
            teamService,
            DIRECTORY_NAME,
            MAX_STORAGE_SIZE);

    @BeforeEach
    public void setUp() {
        projectId = 100L;
        userId_1 = 1L;
        userId_2 = 2L;
        resourceId = 10L;

        teamMember_1 = new TeamMember();
        teamMember_1.setId(userId_1);
        teamMember_1.setRoles(List.of(TeamRole.ANALYST));

        teamMember_2 = new TeamMember();
        teamMember_2.setId(userId_2);
        teamMember_2.setRoles(List.of(TeamRole.MANAGER));

        team = new Team();
        team.setTeamMembers(List.of(teamMember_1, teamMember_2));

        project = new Project();
        project.setId(projectId);
        project.setTeams(List.of(team));
        project.setStorageSize(BigInteger.ZERO);

        resourceOfTM_1 = new Resource();
        resourceOfTM_1.setId(resourceId);
        resourceOfTM_1.setCreatedBy(teamMember_1);
        resourceOfTM_1.setProject(project);
        resourceOfTM_1.setSize(BigInteger.ONE);

        project.setResources(List.of(resourceOfTM_1));

    }

    @Test
    public void testGetFileFromProject_Success() {
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(teamService.checkParticipationUserInTeams(userId_1, project.getTeams()))
                .thenReturn(Optional.ofNullable(teamMember_1));
        when(resourceManager.getFileFromProject(any())).thenReturn(mock(InputStream.class));

        ResourceWithFileStream result = projectService.getFileFromProject(projectId, userId_1, resourceId);

        assertEquals(resourceOfTM_1, result.resource());
        assertNotNull(result.resource());
    }

    @Test
    public void testGetFileFromProject_NotSuccess_UserNotParticipatingInProject() {
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(teamService.checkParticipationUserInTeams(10000L, project.getTeams()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> projectService.getFileFromProject(projectId, userId_1, resourceId));
        assertEquals("User is not a part of the project", exception.getMessage());
    }

    @Test
    public void testDeleteFileFromProject_Success_UserIsCreator() {
        project.setStorageSize(BigInteger.ONE);

        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(resourceManager.getResourceById(resourceId)).thenReturn(resourceOfTM_1);
        when(teamService.checkParticipationUserInTeams(userId_1, project.getTeams()))
                .thenReturn(Optional.ofNullable(teamMember_1));

        projectService.deleteFileFromProject(projectId, userId_1, resourceId);

        assertEquals(BigInteger.ZERO, project.getStorageSize());

        verify(resourceManager).deleteFileFromProject(resourceOfTM_1);
        verify(projectRepository).save(project);
    }


    @Test
    public void testDeleteFileFromProject_Success_UserIsNotCreator_UserIsManager() {
        project.setStorageSize(BigInteger.ONE);

        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(resourceManager.getResourceById(resourceId)).thenReturn(resourceOfTM_1);
        when(teamService.checkParticipationUserInTeams(userId_2, project.getTeams()))
                .thenReturn(Optional.ofNullable(teamMember_2));

        projectService.deleteFileFromProject(projectId, userId_2, resourceId);

        assertEquals(BigInteger.ZERO, project.getStorageSize());

        verify(resourceManager).deleteFileFromProject(resourceOfTM_1);
        verify(projectRepository).save(project);
    }

    @Test
    public void testDeleteFileFromProject_NotSuccess_UserNotAllowedToDeleteFile() {
        TeamMember teamMember = new TeamMember();
        teamMember.setId(10000L);
        teamMember.setRoles(List.of(TeamRole.ANALYST));

        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(resourceManager.getResourceById(resourceId)).thenReturn(resourceOfTM_1);
        when(teamService.checkParticipationUserInTeams(10000L, project.getTeams()))
                .thenReturn(Optional.of(teamMember));


        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            projectService.deleteFileFromProject(projectId, 10000L, resourceId);
        });

        assertEquals("User is not allowed to delete this file", exception.getMessage());
    }


    @Test
    public void uploadFileToProject_Success() {
        long fileSize = resourceOfTM_1.getSize().longValue();
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(file.getSize()).thenReturn(fileSize);
        when(teamService.checkParticipationUserInTeams(userId_1, project.getTeams()))
                .thenReturn(Optional.ofNullable(teamMember_1));

        when(resourceManager.uploadFileToProject(eq(file), any(String.class), eq(project), any(TeamMember.class)))
                .thenReturn(resourceOfTM_1);

        Resource uploadedResource = projectService.uploadFileToProject(projectId, userId_1, file);

        assertNotNull(uploadedResource);
        assertEquals(resourceOfTM_1, uploadedResource);
        assertEquals(fileSize,project.getStorageSize().longValue());
        verify(projectRepository).save(project);
        verify(resourceManager).uploadFileToProject(eq(file), any(String.class), eq(project), any(TeamMember.class));
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
        long fileSize = resourceOfTM_1.getSize().longValue();
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(file.getSize()).thenReturn(fileSize);
        when(teamService.checkParticipationUserInTeams(10000L, project.getTeams()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            projectService.uploadFileToProject(projectId, 10000L, file);
        });
        assertEquals("User is not a part of the project", exception.getMessage());
    }
}
