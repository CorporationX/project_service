package faang.school.projectservice.service;

import faang.school.projectservice.model.dto.StageInvitationDto;
import faang.school.projectservice.model.entity.Stage;
import faang.school.projectservice.model.entity.StageRoles;
import faang.school.projectservice.model.entity.TeamMember;

import java.util.List;

public interface StageInvitationService {
    StageInvitationDto sendInvitation(StageInvitationDto invitationDto);

    StageInvitationDto acceptInvitation(Long invitationId);

    StageInvitationDto declineInvitation(Long invitationId, String reason);

    List<StageInvitationDto> getInvitationsByUser(Long userId);

    void createStageInvitation(TeamMember invited, Stage stage, StageRoles stageRoles);
}
