package faang.school.projectservice.service.stage.filter;

import faang.school.projectservice.dto.filter.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.List;

public interface StageInvitationFilter {

    boolean isApplicable(StageInvitationFilterDto filters);

    boolean apply(StageInvitation stageInvitations, StageInvitationFilterDto filters);
}