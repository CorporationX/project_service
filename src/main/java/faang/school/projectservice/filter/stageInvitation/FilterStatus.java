package faang.school.projectservice.filter.stageInvitation;

import faang.school.projectservice.dto.invitation.DtoStageInvitationFilter;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class FilterStatus implements StageInvitationFilter {
    @Override
    public boolean isApplication(DtoStageInvitationFilter filters) {
        return filters.getStatus() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> invitations, DtoStageInvitationFilter filters) {
        return invitations.filter(invitation -> invitation.getStatus().toString().equals(filters.getStatus().toString()));
    }
}
