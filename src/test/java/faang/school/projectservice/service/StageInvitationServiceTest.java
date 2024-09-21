package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.stage.StageService;
import faang.school.projectservice.service.stage.executor.DeleteStageProcessor;
import faang.school.projectservice.service.stage_invitation.StageInvitationService;
import faang.school.projectservice.service.stage_roles.StageRolesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class StageInvitationServiceTest {
    private Stage stage;
    private Project project;
    private StageDto stageDto;
    private long projectId = 100L;
    private long stageId = 1L;
    private List<Long> taskIds = List.of(1L, 2L, 3L);
    private List<Long> executorIds = List.of(1L, 2L, 3L);
    @Mock
    private StageRolesService stageRolesService;
    @Mock
    private StageInvitationRepository stageInvitationRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;;

    @InjectMocks
    private StageInvitationService stageInvitationService;


    @BeforeEach
    public void setup() {
        TeamRole teamRole = TeamRole.DEVELOPER;

        StageRolesDto stageRolesDto = new StageRolesDto(teamRole, 5);
        Set<StageRolesDto> stageRolesDtos = Set.of(stageRolesDto);

        stageDto = new StageDto(
                stageId,
                "Development Stage",
                projectId,
                stageRolesDtos,
                taskIds,
                executorIds
        );
        project = new Project();
        project.setId(projectId);
        stage = new Stage();
        stage.setStageId(stageId);
        stage.setProject(project);

    }


    @Test
    void testSucccess_sendRoleInvitationsForNewExecutors() {
        StageDto stageDto1 = new StageDto(1L,
                "stage1",
                1L,
                Set.of(new StageRolesDto(TeamRole.DEVELOPER, 5)),
                null,
                null);

        when(stageRolesService.getRoleCountMap(stage)).thenReturn(Map.of(TeamRole.DEVELOPER, 1L));
        when(teamMemberRepository.findByRoleAndProject(TeamRole.DEVELOPER, projectId)).thenReturn(List.of(new TeamMember()));
        List<StageInvitation> stageInvitations = new ArrayList<>();
        stageInvitations.add(stageInvitationService.buildStageInvitation(stage, new TeamMember()));
        stageInvitationService.sendRoleInvitationsForNewExecutors(stageDto1, stage);
        verify(stageInvitationRepository, times(1)).saveAll(stageInvitations);
    }

    @Test
    void testUnsuccessful_sendRoleInvitationsForNewExecutors() {
        StageDto stageDto1 = new StageDto(1L,
                "stage1",
                1L,
                Set.of(new StageRolesDto(TeamRole.DEVELOPER, 1)),
                null,
                null);
        when(stageRolesService.getRoleCountMap(stage)).thenReturn(Map.of(TeamRole.DEVELOPER, 1L));
        stageInvitationService.sendRoleInvitationsForNewExecutors(stageDto1, stage);
        verify(stageInvitationRepository, times(0)).saveAll(any());
    }
}
