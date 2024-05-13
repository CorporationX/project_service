package faang.school.projectservice.service.filter.stage_invitation_filter;

import faang.school.projectservice.dto.filter.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

@Component
public class StageInvitationStatusFilter implements StageInvitationFilter {

    @Override
    public boolean isApplicable(StageInvitationFilterDto filterDto) {
        return filterDto.getStatus() != null;
    }

    @Override
    public boolean apply(StageInvitation stageInvitation, StageInvitationFilterDto filterDto) {
        return stageInvitation.getStatus().equals(filterDto.getStatus());
    }
}
