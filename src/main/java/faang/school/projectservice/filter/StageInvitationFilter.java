package faang.school.projectservice.filter;

import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StageInvitationFilter {

    public List<StageInvitation> invitationByStatusAndStage(List<StageInvitation> stageInvitations,
                                           StageInvitationStatus statusFilter, long stageId) {
        return stageInvitations.stream()
                .filter(stageInvitation -> stageInvitation.getStatus().equals(statusFilter))
                .filter(stageInvitation -> stageInvitation.getStage().getStageId().equals(stageId))
                .toList();
    }
}