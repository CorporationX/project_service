package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.filter.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.List;
import java.util.stream.Stream;

public class StageInvitationTeamMemberFilter implements StageInvitationFilter {
    @Override
    public boolean isApplicable(StageInvitationFilterDto filters) {
        return filters.getTeamMemberPattern() != null;
    }

    @Override
    public List<StageInvitation> apply(Stream<StageInvitation> stageInvitations, StageInvitationFilterDto filters) {
        return stageInvitations
                .filter(stageInvitation -> stageInvitation.getInvited().getId() == filters.getTeamMemberPattern())
                .toList();
    }
}
