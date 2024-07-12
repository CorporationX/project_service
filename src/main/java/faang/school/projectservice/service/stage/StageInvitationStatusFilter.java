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
    public Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitations, StageInvitationFilterDto filter) {
        return stageInvitations
                .filter(stageInvitation -> stageInvitation.getStatus().equals(filter.getStatus()));
    }
}
