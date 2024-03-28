package faang.school.projectservice.service.stage.filters;

import faang.school.projectservice.dto.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.List;

public interface StageInvitationFilter {

    boolean isApplicable(StageInvitationFilterDto filters);

    void apply(List<StageInvitation> stageInvitations, StageInvitationFilterDto filters);
}
