package faang.school.projectservice.service.filter.stage_invitation_filter;

import faang.school.projectservice.dto.filter.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

public interface StageInvitationFilter {
    boolean isApplicable(StageInvitationFilterDto filterDto);

    boolean apply(StageInvitation stageInvitation, StageInvitationFilterDto filterDto);
}
