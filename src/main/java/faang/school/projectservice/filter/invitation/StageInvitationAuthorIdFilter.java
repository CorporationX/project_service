package faang.school.projectservice.filter.invitation;

import faang.school.projectservice.dto.FilterDto.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.stream.Stream;

public class StageInvitationAuthorIdFilter implements StageInvitationFilter{
    @Override
    public boolean isApplicable(StageInvitationFilterDto filter) {
        return filter.getAuthorId() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitations, StageInvitationFilterDto filter) {
        return stageInvitations
                .filter(stageInvitation -> stageInvitation.getAuthor().getId().equals(filter.getAuthorId()));
    }
}
