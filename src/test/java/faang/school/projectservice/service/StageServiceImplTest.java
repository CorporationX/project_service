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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
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
    private StageMapper stageMapper;

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
        Stage stage = new Stage();
        stage.setStageId(stageId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(stageMapper.toStage(stageDto)).thenReturn(stage);
        when(stageRepository.save(stage)).thenReturn(stage);
        when(stageMapper.toStageDto(stage)).thenReturn(stageDto);

        StageDto result = service.createStage(stageDto);

        assertEquals(stageDto, result);
        verify(projectRepository).findById(projectId);
        verify(stageMapper).toStage(stageDto);
        verify(stageRepository).save(stage);
        verify(stageMapper).toStageDto(stage);
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
        Stage stage = new Stage();
        StageDto stageDto = new StageDto(
                stageId, "stageName", projectId,
                List.of(new StageRoleDto(TeamRole.ANALYST, 1)),
                List.of(new TeamMemberDto(memberId, "John", TeamRole.ANALYST)), 3
        );

        when(stageRepository.findByProjectId(projectId)).thenReturn(List.of(stage));
        when(stageMapper.toListStageDto(List.of(stage))).thenReturn(List.of(stageDto));

        List<StageDto> result = service.getAllStagesOfProject(projectId);

        assertEquals(1, result.size());
        assertEquals(stageDto, result.get(0));
        verify(stageRepository).findByProjectId(projectId);
        verify(stageMapper).toListStageDto(List.of(stage));
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
        stage.setExecutors(List.of());
        StageDto stageDto = new StageDto(
                stageId, "Updated Stage", projectId,
                List.of(new StageRoleDto(TeamRole.ANALYST, 1)),
                List.of(new TeamMemberDto(memberId, "John", TeamRole.ANALYST)), 3
        );

        Project project = new Project();
        Team team = new Team();
        TeamMember teamMember = new TeamMember();
        teamMember.setId(memberId);
        teamMember.setRoles(List.of(TeamRole.ANALYST));
        team.setTeamMembers(List.of(teamMember));
        project.setTeams(List.of(team));

        when(stageRepository.findById(stageId)).thenReturn(Optional.of(stage));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(stageRepository.save(stage)).thenReturn(stage);
        when(stageMapper.toStageDto(stage)).thenReturn(stageDto);

        StageDto result = service.updateStage(stageId, updateStageDto);

        assertEquals(stageDto, result);
        verify(stageRepository).findById(stageId);
        verify(stageMapper).updateStage(updateStageDto, stage);
        verify(stageRepository).save(stage);
        verify(stageMapper).toStageDto(stage);
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
        when(stageRepository.save(stage)).thenReturn(stage);

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
        TeamMember teamMember = new TeamMember();
        teamMember.setRoles(List.of(TeamRole.DEVELOPER));
        stage.setExecutors(List.of(teamMember));

        when(stageRepository.findById(stageId)).thenReturn(Optional.of(stage));
        when(stageRepository.save(stage)).thenReturn(stage);

        service.updateStage(stageId, updateStageDto);

        verify(notificationService, never()).sendStageInvitation(anyLong());
    }

    @Test
    public void checkGetByIdSuccess() {
        Stage stage = new Stage();
        StageDto stageDto = new StageDto(
                stageId, "stageName", projectId,
                List.of(new StageRoleDto(TeamRole.ANALYST, 1)),
                List.of(new TeamMemberDto(memberId, "John", TeamRole.ANALYST)), 3
        );

        when(stageRepository.findById(stageId)).thenReturn(Optional.of(stage));
        when(stageMapper.toStageDto(stage)).thenReturn(stageDto);

        StageDto result = service.getById(stageId);

        assertEquals(stageDto, result);
        verify(stageRepository).findById(stageId);
        verify(stageMapper).toStageDto(stage);
    }

    @Test
    public void checkGetByIdFailedWhenStageNotFound() {
        when(stageRepository.findById(stageId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getById(stageId));
        verify(stageRepository).findById(stageId);
    }
}