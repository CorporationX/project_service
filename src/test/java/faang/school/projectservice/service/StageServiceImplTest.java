package faang.school.projectservice.service;

import faang.school.projectservice.client.NotificationServiceClient;
import faang.school.projectservice.dto.client.StageDto;
import faang.school.projectservice.dto.client.StageRoleDto;
import faang.school.projectservice.dto.client.TeamMemberDto;
import faang.school.projectservice.dto.client.UpdateStageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageServiceImplTest {

    @Mock
    private StageRepository stageRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Spy
    private StageMapper stageMapper = Mappers.getMapper(StageMapper.class);

    @Mock
    private NotificationServiceClient notificationService;

    @InjectMocks
    private StageServiceImpl service;

    private long stageId = 1L;
    private long projectId = 100L;
    private long memberId = 2L;

    @Test
    public void checkCreateStageSuccess() {
        StageDto stageDto = new StageDto(
                stageId, "stageName", projectId,
                List.of(new StageRoleDto(TeamRole.ANALYST, 1)),
                List.of(new TeamMemberDto(memberId, "John", TeamRole.ANALYST)), 3
        );

        Project project = new Project();
        project.setId(projectId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(stageRepository.save(any(Stage.class))).thenAnswer(invocation -> {
            Stage stage = invocation.getArgument(0);
            stage.setStageId(stageId);
            stage.setStageName("stageName");
            stage.setProject(project);
            return stage;
        });

        StageDto result = service.createStage(stageDto);

        assertNotNull(result);
        assertEquals(stageId, result.stageId());
        assertEquals("stageName", result.stageName());

        verify(projectRepository).findById(projectId);
        verify(stageRepository).save(any(Stage.class));
    }

    @Test
    public void checkCreateStageFailedWhenProjectNotFound() {
        StageDto stageDto = new StageDto(
                stageId, "stageName", projectId,
                List.of(new StageRoleDto(TeamRole.ANALYST, 1)),
                List.of(new TeamMemberDto(memberId, "John", TeamRole.ANALYST)), 3
        );

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.createStage(stageDto));
        verify(projectRepository).findById(projectId);
        verify(stageRepository, never()).save(any());
    }

    @Test
    public void checkCreateStageFailedWhenStageNameEmpty() {
        StageDto stageDto = new StageDto(
                stageId, "", projectId,
                List.of(new StageRoleDto(TeamRole.ANALYST, 1)),
                List.of(new TeamMemberDto(memberId, "John", TeamRole.ANALYST)), 3
        );

        assertThrows(DataValidationException.class, () -> service.createStage(stageDto));
        verify(projectRepository, never()).findById(anyLong());
        verify(stageRepository, never()).save(any());
    }

    @Test
    public void checkCreateStageFailedWhenRequiredRolesEmpty() {
        StageDto stageDto = new StageDto(
                stageId, "stageName", projectId,
                List.of(),
                List.of(new TeamMemberDto(memberId, "John", TeamRole.ANALYST)), 3
        );

        assertThrows(DataValidationException.class, () -> service.createStage(stageDto));
        verify(projectRepository, never()).findById(anyLong());
        verify(stageRepository, never()).save(any());
    }

    @Test
    public void checkGetAllStagesOfProjectSuccess() {

        Project project = new Project();
        project.setId(projectId);

        Stage stage = new Stage();
        stage.setStageId(stageId);
        stage.setStageName("stageName");
        stage.setProject(project);

        when(stageRepository.findByProjectId(projectId)).thenReturn(List.of(stage));

        List<StageDto> result = service.getAllStagesOfProject(projectId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(stageId, result.get(0).stageId());
        assertEquals("stageName", result.get(0).stageName());

        verify(stageRepository).findByProjectId(projectId);
    }

    @Test
    public void checkDeleteByIdSuccess() {
        service.deleteById(stageId);

        verify(stageRepository).deleteById(stageId);
    }

    @Test
    public void checkUpdateStageSuccess() {
        UpdateStageDto updateStageDto = new UpdateStageDto(
                stageId, "Updated Stage", projectId,
                new TeamMemberDto(memberId, "John", TeamRole.ANALYST),
                List.of(4L, 5L)
        );

        Stage stage = new Stage();
        stage.setStageId(stageId);
        stage.setExecutors(List.of());

        Project project = new Project();
        Team team = new Team();
        TeamMember teamMember = new TeamMember();
        teamMember.setId(memberId);
        teamMember.setRoles(List.of(TeamRole.ANALYST));
        team.setTeamMembers(List.of(teamMember));
        project.setTeams(List.of(team));

        when(stageRepository.findById(stageId)).thenReturn(Optional.of(stage));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(stageRepository.save(any(Stage.class))).thenAnswer(invocation -> {
            Stage savedStage = invocation.getArgument(0);
            return savedStage;
        });

        StageDto result = service.updateStage(stageId, updateStageDto);

        assertNotNull(result);
        assertEquals(stageId, result.stageId());

        verify(stageRepository).findById(stageId);
        verify(stageRepository).save(any(Stage.class));
        verify(projectRepository).findById(projectId);
    }

    @Test
    public void checkUpdateStageFailedWhenStageNotFound() {
        UpdateStageDto updateStageDto = new UpdateStageDto(
                stageId, "Updated Stage", projectId,
                new TeamMemberDto(memberId, "John", TeamRole.ANALYST),
                List.of(4L, 5L)
        );

        when(stageRepository.findById(stageId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateStage(stageId, updateStageDto));
        verify(stageRepository).findById(stageId);
        verify(stageRepository, never()).save(any());
    }

    @Test
    public void checkUpdateStageSendInvitationWhenRoleNotInStage() {
        UpdateStageDto updateStageDto = new UpdateStageDto(
                stageId, "Updated Stage", projectId,
                new TeamMemberDto(memberId, "John", TeamRole.DEVELOPER),
                List.of(4L, 5L)
        );

        Stage stage = new Stage();
        stage.setStageId(stageId);
        stage.setExecutors(List.of());

        Project project = new Project();
        Team team = new Team();
        TeamMember teamMember = new TeamMember();
        teamMember.setId(memberId);
        teamMember.setRoles(List.of(TeamRole.DEVELOPER));
        team.setTeamMembers(List.of(teamMember));
        project.setTeams(List.of(team));

        when(stageRepository.findById(stageId)).thenReturn(Optional.of(stage));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(stageRepository.save(any(Stage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.updateStage(stageId, updateStageDto);

        verify(notificationService).sendStageInvitation(memberId);
    }

    @Test
    public void checkUpdateStageNotSendInvitationWhenRoleInStage() {
        UpdateStageDto updateStageDto = new UpdateStageDto(
                stageId, "Updated Stage", projectId,
                new TeamMemberDto(memberId, "John", TeamRole.DEVELOPER),
                List.of(4L, 5L)
        );

        Stage stage = new Stage();
        stage.setStageId(stageId);
        TeamMember teamMember = new TeamMember();
        teamMember.setRoles(List.of(TeamRole.DEVELOPER));
        stage.setExecutors(List.of(teamMember));

        when(stageRepository.findById(stageId)).thenReturn(Optional.of(stage));
        when(stageRepository.save(any(Stage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.updateStage(stageId, updateStageDto);

        verify(notificationService, never()).sendStageInvitation(anyLong());
    }

    @Test
    public void checkGetByIdSuccess() {
        Project project = new Project();
        project.setId(projectId);

        Stage stage = new Stage();
        stage.setStageId(stageId);
        stage.setStageName("stageName");
        stage.setProject(project);

        when(stageRepository.findById(stageId)).thenReturn(Optional.of(stage));

        StageDto result = service.getById(stageId);

        assertNotNull(result);
        assertEquals(stageId, result.stageId());
        assertEquals("stageName", result.stageName());

        verify(stageRepository).findById(stageId);
    }

    @Test
    public void checkGetByIdFailedWhenStageNotFound() {
        when(stageRepository.findById(stageId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getById(stageId));
        verify(stageRepository).findById(stageId);
    }
}