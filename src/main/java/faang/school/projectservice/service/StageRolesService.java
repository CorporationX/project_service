package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class StageRolesService {
    private final StageRolesRepository stageRolesRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final StageInvitationRepository stageInvitationRepository;

    private final StageMapper stageMapper;

    public List<StageRoles> createStageRolesForStageById(
            @Min(1) Long stageId,
            Set<StageRolesDto> stageRolesDtos) {
        List<StageRoles> stageRoles = stageRolesDtos.stream()
                .map(stageRolesDto -> StageRoles.builder()
                        .teamRole(stageRolesDto.teamRole())
                        .count(stageRolesDto.count())
                        .stage(Stage.builder().stageId((stageId)).build())
                        .build())
                .toList();
        return stageRolesRepository.saveAll(stageRoles);
    }

    public void sendInvitationsForRole(Stage stage, TeamRole role, long invitationsToSend) {
        List<TeamMember> availableMembers = teamMemberRepository.findByRoleAndProject(role, stage.getProject().getId());
        List<StageInvitation> stageInvitations = new ArrayList<>();
        for (int i = 0; i < invitationsToSend && i < availableMembers.size(); i++) {
            TeamMember member = availableMembers.get(i);
            stageInvitations.add(buildStageInvitation(stage, member));
        }
        stageInvitationRepository.saveAll(stageInvitations);
    }

    private StageInvitation buildStageInvitation(Stage stage, TeamMember member) {
        return StageInvitation.builder()
                .stage(stage)
                .author(member)
                .invited(member)
                .status(StageInvitationStatus.PENDING)
                .description("Приглашение на этап: " + stage.getStageName())
                .build();

    }
}
