package faang.school.projectservice.filter.invitation;

import faang.school.projectservice.dto.filterDto.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StageInvitationStatusFilter implements StageInvitationFilter {
    @Override
    public boolean isApplicable(StageInvitationFilterDto filter) {
        return filter.getStatus() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitations, StageInvitationFilterDto filter) {
        return stageInvitations
                .filter(invitation -> invitation.getStatus().equals(filter.getStatus()));
    }
}
