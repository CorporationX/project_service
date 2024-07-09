package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.client.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.stream.Stream;

public class StageInvitationStatusFilter implements StageInvitationFilter {
    @Override
    public boolean isApplicable(StageInvitationFilterDto filter) {
        return filter.getStatus() != null;
    }

    @Override
    public void apply(Stream<StageInvitation> stageInvitations, StageInvitationFilterDto filter) {
        stageInvitations
                .filter(stageInvitation -> stageInvitation.getStatus().equals(filter.getStatus()));
    }
}
