package faang.school.projectservice.service.stage_invitation;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.stage_roles.StageRolesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StageInvitationService {
    private final StageRolesService stageRolesService;
    private final TeamMemberRepository teamMemberRepository;
    private final StageInvitationRepository stageInvitationRepository;

    public StageInvitation buildStageInvitation(Stage stage, TeamMember member) {
        return StageInvitation.builder()
                .stage(stage)
                .author(member)
                .invited(member)
                .status(StageInvitationStatus.PENDING)
                .description("Invitation for stage: " + stage.getStageName())
                .build();

    }

    public void createInvitationsForTeamRole(Stage stage, TeamRole role, long invitationsToSend) {
        List<TeamMember> availableMembers = teamMemberRepository.findByRoleAndProject(role, stage.getProject().getId());
        List<StageInvitation> stageInvitations = new ArrayList<>();
        for (int i = 0; i < invitationsToSend && i < availableMembers.size(); i++) {
            TeamMember member = availableMembers.get(i);
            stageInvitations.add(buildStageInvitation(stage, member));
        }
        stageInvitationRepository.saveAll(stageInvitations);
        log.info("Sent {} invitations for role {}", stageInvitations.size(), role);
    }


    public void sendRoleInvitationsForNewExecutors(StageDto stageDto, Stage stage) {
        log.info("Send role invitations for stage with id {}", stage.getStageId());
        Map<TeamRole, Long> currentRoleCountMap = stageRolesService.getRoleCountMap(stage);

        stageDto.stageRolesDtos().forEach(stageRoleDto -> {
            TeamRole role = stageRoleDto.teamRole();
            int requiredCount = stageRoleDto.count();
            long currentRoleCount = currentRoleCountMap.getOrDefault(role, 0L);

            log.debug("Role: " + role + ", required: " + requiredCount + ", current: " + currentRoleCount);

            if (currentRoleCount < requiredCount) {
                long invitationsToSend = requiredCount - currentRoleCount;
                log.info("Send " + invitationsToSend + " invitations for role: " + role);
                createInvitationsForTeamRole(stage, role, invitationsToSend);
            }
        });
    }

}
