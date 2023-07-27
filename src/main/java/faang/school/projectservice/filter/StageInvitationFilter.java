package faang.school.projectservice.filter;

import faang.school.projectservice.dto.invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface StageInvitationFilter {
    boolean isApplicable(StageInvitationFilterDto filter);
    void apply(List<StageInvitation> invitations, StageInvitationFilterDto filter);
}