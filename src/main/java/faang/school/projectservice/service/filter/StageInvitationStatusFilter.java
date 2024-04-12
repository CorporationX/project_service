package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.filter.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

@Component
public class StageInvitationStatusFilter implements StageInvitationFilter {
    @Override
    public boolean isApplicable(StageInvitationFilterDto filters) {
        return filters.getStatusPattern() != null;
    }

    @Override
    public boolean apply(StageInvitation stageInvitation, StageInvitationFilterDto filters) {
        return stageInvitation.getStatus().equals(filters.getStatusPattern());
    }
}
