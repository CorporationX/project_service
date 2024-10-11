package faang.school.projectservice.service;


import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.projectservice.jpa.ProjectResourceRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectResource;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.resource.ProjectResourceManager;
import faang.school.projectservice.service.resource.ProjectResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    private Project project;
    private Long projectId;
    private ProjectResource projectResourceOfTM_1;
    private Team team;
    private Long resourceId;
    private Long userId_1;
    private Long userId_2;
    private TeamMember teamMember_1;
    private TeamMember teamMember_2;
    @Mock
    private MultipartFile file;
    private final ProjectRepository projectRepository = mock(ProjectRepository.class);
    private final ProjectResourceService projectResourceService = mock(ProjectResourceService.class);
    private final ProjectResourceManager projectResourceManager = mock(ProjectResourceManager.class);


    private final String DIRECTORY_NAME = "projects";
    private final long MAX_STORAGE_SIZE = 2147483648L;

    private final ProjectService projectService = new ProjectService(projectRepository,
            projectResourceManager,
            projectResourceService);

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

        projectResourceOfTM_1 = new ProjectResource();
        projectResourceOfTM_1.setId(resourceId);
        projectResourceOfTM_1.setKey("TEST://");
        projectResourceOfTM_1.setCreatedBy(teamMember_1);
        projectResourceOfTM_1.setProject(project);
        projectResourceOfTM_1.setSize(BigInteger.ONE);

        project.setProjectResources(List.of(projectResourceOfTM_1));

    }

    @Test
    public void testGetFileFromProject_Success() {
        when(projectResourceService.findProjectResourceByIds(resourceId, projectId)).thenReturn(projectResourceOfTM_1);
        when(projectResourceManager.getFileS3ByKey(any(String.class))).thenReturn(mock(Resource.class));
        Pair<Resource, ProjectResource> result = projectService.getFileFromProject(projectId, userId_1, resourceId);

        assertEquals(projectResourceOfTM_1, result.getSecond());
        assertNotNull(result);
    }

    @Test
    public void testGetFileFromProject_NotSuccess_UserNotParticipatingInProject() {
        when(projectResourceService.findProjectResourceByIds(resourceId, projectId)).thenReturn(projectResourceOfTM_1);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> projectService.getFileFromProject(projectId, 10000L, resourceId));
        assertEquals("User is not a part of the project", exception.getMessage());
    }

    @Test
    public void testDeleteFileFromProject_Success_UserIsCreator() {
        project.setStorageSize(BigInteger.ONE);

        when(projectResourceService.findProjectResourceByIds(resourceId, projectId)).thenReturn(projectResourceOfTM_1);

        projectService.deleteFileFromProject(projectId, userId_1, resourceId);

        assertEquals(BigInteger.ZERO, project.getStorageSize());

        verify(projectResourceManager).deleteFileS3Async(projectResourceOfTM_1);
        verify(projectRepository).save(project);
    }


    @Test
    public void testDeleteFileFromProject_Success_UserIsNotCreator_UserIsManager() {
        project.setStorageSize(BigInteger.ONE);

        when(projectResourceService.findProjectResourceByIds(resourceId, projectId)).thenReturn(projectResourceOfTM_1);


        projectService.deleteFileFromProject(projectId, userId_2, resourceId);

        assertEquals(BigInteger.ZERO, project.getStorageSize());

        verify(projectResourceManager).deleteFileS3Async(projectResourceOfTM_1);
        verify(projectRepository).save(project);
    }

    @Test
    public void testDeleteFileFromProject_NotSuccess_UserNotAllowedToDeleteFile() {
        teamMember_2.setRoles(List.of(TeamRole.ANALYST));
        when(projectResourceService.findProjectResourceByIds(resourceId, projectId))
                .thenReturn(projectResourceOfTM_1);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            projectService.deleteFileFromProject(projectId, userId_2, resourceId);
        });
        assertEquals("User is not allowed to delete this file", exception.getMessage());
    }


    @Test
    public void uploadFileToProject_Success() {
        long fileSize = projectResourceOfTM_1.getSize().longValue();
        Pair<ProjectResource, ObjectMetadata> projectResourceWithMetadata = Pair.of(projectResourceOfTM_1, new ObjectMetadata());
        when(projectRepository.getByIdOrThrow(projectId)).thenReturn(project);
        when(projectResourceManager.getProjectResourceBeforeUploadFile(file, project, teamMember_1))
                .thenReturn(projectResourceWithMetadata);

        ProjectResource uploadedResource = projectService.uploadFileToProject(projectId, userId_1, file);

        assertNotNull(uploadedResource);
        assertEquals(projectResourceOfTM_1, uploadedResource);
        assertEquals(fileSize, project.getStorageSize().longValue());
        verify(projectRepository).save(project);
        verify(projectResourceService).save(projectResourceOfTM_1);
        verify(projectResourceManager).uploadFileS3Async(file, projectResourceOfTM_1, projectResourceWithMetadata.getSecond());
    }


    @Test
    public void uploadFileToProject_UserNotInTeam_ShouldThrowException() {
        when(projectRepository.getByIdOrThrow(projectId)).thenReturn(project);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            projectService.uploadFileToProject(projectId, 10000L, file);
        });
        assertEquals("User is not a part of the project", exception.getMessage());
    }
}
