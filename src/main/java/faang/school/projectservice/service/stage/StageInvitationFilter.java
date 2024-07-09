package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.client.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public interface StageInvitationFilter {
    public boolean isApplicable(StageInvitationFilterDto filter);

    public void apply(Stream<StageInvitation> stageInvitations, StageInvitationFilterDto filter);
}
