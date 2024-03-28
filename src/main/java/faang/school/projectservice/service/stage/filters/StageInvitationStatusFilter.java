package faang.school.projectservice.service.stage.filters;

import faang.school.projectservice.dto.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StageInvitationStatusFilter implements StageInvitationFilter {
    @Override
    public boolean isApplicable(StageInvitationFilterDto filters) {
        return filters.getStatus() != null;
    }

    @Override
    public void apply(List<StageInvitation> stageInvitations, StageInvitationFilterDto filters) {
        stageInvitations.removeIf(stageInvitation -> filters.getStatus().equals(stageInvitation.getStatus()));

    }
}
