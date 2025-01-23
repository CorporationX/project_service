package faang.school.projectservice.filter.invitation;

import faang.school.projectservice.dto.FilterDto.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public interface StageInvitationFilter {
    boolean isApplicable (StageInvitationFilterDto filter);
    Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitations, StageInvitationFilterDto filter);
}
