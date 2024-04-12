package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.filter.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

public interface StageInvitationFilter {
    boolean isApplicable(StageInvitationFilterDto filters);

    boolean apply(StageInvitation stageInvitation, StageInvitationFilterDto filters);
}
