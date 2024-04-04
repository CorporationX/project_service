package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.filter.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public interface StageInvitationFilter {
    public boolean isApplicable(StageInvitationFilterDto filters);

    public List<StageInvitation> apply(Stream<StageInvitation> stageInvitations, StageInvitationFilterDto filters);
}
