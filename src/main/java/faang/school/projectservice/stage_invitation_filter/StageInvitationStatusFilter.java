package faang.school.projectservice.stage_invitation_filter;

import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StageInvitationStatusFilter implements StageInvitationFilter{
    @Override
    public boolean isApplicable(StageInvitationFilterDto filters){
        return filters.getStatus() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitations, StageInvitationFilterDto filters){
        return stageInvitations.filter(stageInvitation -> stageInvitation.getStatus().toString().contains(filters.getStatus()));
    }
}
