package faang.school.projectservice.service;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.dto.stage.CreateStageRequest;
import faang.school.projectservice.dto.stage.DeleteStageRequest;
import faang.school.projectservice.dto.stage.StageResponse;
import faang.school.projectservice.dto.stage.UpdateStageRequest;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.mapper.StageMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.invitation.StageInvitationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StageServiceTest {
    @Mock
    private StageRepository stageRepository;

    @Spy
    private StageMapper stageMapper = new StageMapperImpl();

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectService projectService;
    @Mock
    private StageInvitationService stageInvitationService;

    @InjectMocks
    private StageService stageService;

    private long projectId;
    private CreateStageRequest createStageRequest;
    private Project project;
    private Stage stage1;
    private Stage stage2;
    private Stage stage;

    @BeforeEach
    void setUp() {
        stage = new Stage();
        stage.setStageId(1L);
        stage.setStageName("Stage 1");
        stage.setTasks(new ArrayList<>());
        stage.setStageRoles(new ArrayList<>());
        projectId = 1L;
        createStageRequest = new CreateStageRequest("Stage 1", projectId, List.of());

        project = new Project();
        project.setId(projectId);
        project.setName("Test Project");

        stage1 = new Stage();
        stage1.setStageName("Stage 1");
        stage1.setTasks(new ArrayList<>());
        stage1.setStageRoles(new ArrayList<>());

        stage2 = new Stage();
        stage2.setStageName("Stage 2");
        stage2.setTasks(new ArrayList<>());
        stage2.setStageRoles(new ArrayList<>());

        project.setStages(List.of(stage1, stage2));
    }

    @Test
    void testCreateStage_Success() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(stageRepository.save(any(Stage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StageResponse response = stageService.create(createStageRequest);

        assertNotNull(response, "Response should not be null");
        assertEquals("Stage 1", response.stageName(), "Stage name should match the request");
        assertEquals(projectId, response.projectId(), "Project ID should match the request");

        verify(stageRepository, times(1)).save(any(Stage.class));
        verify(projectRepository, times(1)).findById(projectId);
    }

    @Test
    void testCreateStage_ProjectNotFound() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> stageService.create(createStageRequest),
                "Expected exception for missing project");

        assertTrue(exception.getMessage().contains("Project with id"),
                "Exception message should indicate missing project");
        verify(stageRepository, never()).save(any(Stage.class));
    }

    @Test
    void testGetStagesByProjectWithFilters_NoFilters() {
        when(projectService.getProject(projectId)).thenReturn(project);

        List<StageResponse> responses = stageService.getStagesByProjectWithFilters(projectId, null, null);

        assertNotNull(responses, "Response should not be null");
        assertEquals(2, responses.size(),
                "Response should contain all stages");
        verify(projectService, times(1)).getProject(projectId);
    }

    @Test
    void testGetStagesByProjectWithFilters_RoleFilter() {
        StageRoles role = new StageRoles();
        role.setTeamRole(TeamRole.MANAGER);
        stage1.setStageRoles(List.of(role));

        when(projectService.getProject(projectId)).thenReturn(project);

        List<StageResponse> responses = stageService.getStagesByProjectWithFilters(projectId, List.of("MANAGER"), null);

        assertNotNull(responses, "Response should not be null");
        assertEquals(1, responses.size(), "Response should contain only one stage");
        assertEquals("Stage 1", responses.get(0).stageName(), "Filtered stage name should match");

        verify(projectService, times(1)).getProject(projectId);
    }

    @Test
    void testGetStagesByProjectWithFilters_TaskStatusFilter() {
        Task task = new Task();
        task.setStatus(TaskStatus.TESTING);
        stage1.setTasks(List.of(task));
        project.setStages(List.of(stage1));

        when(projectService.getProject(projectId)).thenReturn(project);

        List<StageResponse> responses = stageService.getStagesByProjectWithFilters(projectId,
                null, "TESTING");

        assertNotNull(responses, "Response should not be null");
        assertEquals(1, responses.size(), "Response should contain only one stage");
        assertEquals("Stage 1", responses.get(0).stageName(), "Filtered stage name should match");

        verify(projectService, times(1)).getProject(projectId);
    }

    @Test
    void testGetStagesByProjectWithFilters_InvalidTaskStatus() {
        when(projectService.getProject(projectId)).thenReturn(project);

        List<StageResponse> responses = stageService.getStagesByProjectWithFilters(projectId,
                null, "INVALID");

        assertNotNull(responses, "Response should not be null");
        assertTrue(responses.isEmpty(), "Response should be empty for invalid status");

        verify(projectService, times(1)).getProject(projectId);
    }

    @Test
    void testDeleteStageWithStrategy_NullRequest() {
        assertThrows(IllegalArgumentException.class, () ->
                stageService.deleteStageWithStrategy(null));
        verifyNoInteractions(stageRepository, taskRepository);
    }

    @Test
    void testDeleteStageWithStrategy_EmptyDeletionStrategy() {
        DeleteStageRequest request = DeleteStageRequest.builder()
                .stageId(1L)
                .deletionStrategy("")
                .build();
        when(stageRepository.findById(request.stageId())).thenReturn(Optional.of(stage));

        assertThrows(IllegalArgumentException.class, () -> stageService.deleteStageWithStrategy(request));

        verify(stageRepository, never()).save(any());
        verify(taskRepository, never()).deleteAll(any());
    }

    @Test
    void testUpdate_ValidUpdate() {
        UpdateStageRequest updateRequest = UpdateStageRequest.builder()
                .stageId(1L)
                .stageName("Updated Stage Name")
                .projectId(1L)
                .authorId(1L)
                .executorsIds(List.of(1L))
                .requiredRoles(List.of("DEVELOPER"))
                .build();

        when(stageRepository.findById(updateRequest.stageId())).thenReturn(Optional.of(stage));
        when(stageRepository.save(any(Stage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TeamMember teamMember = TeamMember.builder()
                .id(1L)
                .nickname("JohnDoe")
                .roles(List.of(TeamRole.DEVELOPER))
                .build();
        when(teamMemberRepository.findAllById(List.of(1L))).thenReturn(List.of(teamMember));
        when(teamMemberRepository.findByProjectId(updateRequest.projectId())).thenReturn(List.of(
                TeamMember.builder().id(1L).roles(List.of(TeamRole.DEVELOPER)).build(),
                TeamMember.builder().id(2L).roles(List.of(TeamRole.DEVELOPER)).build()
        ));

        StageResponse response = stageService.update(updateRequest);

        assertNotNull(response);
        assertEquals("Updated Stage Name", response.stageName());
        verify(stageRepository, times(1)).findById(updateRequest.stageId());
        verify(stageRepository, times(1)).save(any(Stage.class));
    }

    @Test
    void testUpdate_InvalidStage() {
        UpdateStageRequest updateRequest = UpdateStageRequest.builder()
                .stageId(999L)
                .stageName("Updated Stage Name")
                .projectId(1L)
                .authorId(1L)
                .executorsIds(List.of(1L))
                .requiredRoles(List.of("DEVELOPER"))
                .build();

        when(stageRepository.findById(updateRequest.stageId())).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class, () -> stageService.update(updateRequest));
        verify(stageRepository, never()).save(any(Stage.class));
    }

    @Test
    void testDeleteStage_StageNotFound() {
        DeleteStageRequest request = DeleteStageRequest.builder().stageId(999L).deletionStrategy("cascade").build();

        when(stageRepository.findById(request.stageId())).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class, () -> stageService.deleteStageWithStrategy(request));

        verify(stageRepository, never()).save(any());
        verify(taskRepository, never()).deleteAll(any());
    }

    @Test
    void testDeleteStage_Success() {
        DeleteStageRequest request = DeleteStageRequest.builder().stageId(1L).deletionStrategy("cascade").build();

        when(stageRepository.findById(request.stageId())).thenReturn(Optional.of(stage));
        stageService.deleteStageWithStrategy(request);

        verify(stageRepository, times(1)).delete(stage);
        verify(taskRepository, times(1)).deleteAllByStageId(any());
    }

    @Test
    void testGetStagesByProjectWithFilters_MultipleRoles() {
        StageRoles role1 = new StageRoles();
        role1.setTeamRole(TeamRole.MANAGER);

        StageRoles role2 = new StageRoles();
        role2.setTeamRole(TeamRole.DEVELOPER);

        stage1.setStageRoles(List.of(role1, role2));

        when(projectService.getProject(projectId)).thenReturn(project);

        List<StageResponse> responses = stageService.getStagesByProjectWithFilters(projectId,
                List.of("MANAGER", "DEVELOPER"), null);

        assertNotNull(responses, "Response should not be null");
        assertEquals(1, responses.size(), "Response should contain only one stage");
        assertEquals("Stage 1", responses.get(0).stageName(), "Filtered stage name should match");
        verify(projectService, times(1)).getProject(projectId);
    }

    @Test
    void testGetStagesByProjectWithFilters_MultipleStatuses() {
        Task task1 = new Task();
        task1.setStatus(TaskStatus.TODO);

        Task task2 = new Task();
        task2.setStatus(TaskStatus.TESTING);

        stage1.setTasks(List.of(task1, task2));
        project.setStages(List.of(stage1));

        when(projectService.getProject(projectId)).thenReturn(project);
        List<StageResponse> responses = stageService.getStagesByProjectWithFilters(projectId, null,
                "TODO,TESTING");

        assertNotNull(responses, "Response should not be null");
        assertEquals(1, responses.size(), "Response should contain only one stage");
        assertEquals("Stage 1", responses.get(0).stageName(), "Filtered stage name should match");

        verify(projectService, times(1)).getProject(projectId);
    }


    @Test
    void testGetAllStagesByProject_ValidProject() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        List<StageResponse> response = stageService.getAllStagesByProject(projectId);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Stage 1", response.get(0).stageName());
        assertEquals("Stage 2", response.get(1).stageName());
        verify(projectRepository, times(1)).findById(projectId);
    }

    @Test
    void testGetAllStagesByProject_ProjectNotFound() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> stageService.getAllStagesByProject(projectId));
        verify(projectRepository, times(1)).findById(projectId);
    }

    @Test
    void testGetStageById_ValidStage() {
        when(stageRepository.findById(1L)).thenReturn(Optional.of(stage));

        StageResponse response = stageService.getStageById(1L);
        assertNotNull(response);
        assertEquals("Stage 1", response.stageName());
        verify(stageRepository, times(1)).findById(1L);
    }

    @Test
    void testGetStageById_StageNotFound() {
        when(stageRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> stageService.getStageById(1L));
        verify(stageRepository, times(1)).findById(1L);
    }


}



