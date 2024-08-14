package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public interface StageInvitationFilter {
    boolean isApplicable(StageInvitationFilterDto filter);

    void apply(Stream<StageInvitation> stageInvitations, StageInvitationFilterDto filter);
}
