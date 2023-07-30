package faang.school.projectservice.filter;

import faang.school.projectservice.dto.invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;
import java.util.stream.Stream;

@Component
public interface StageInvitationFilter {
    boolean isApplicable(StageInvitationFilterDto stageInvitationFilterDto);
    Stream<StageInvitation> apply(Stream<StageInvitation> invitationsStream, StageInvitationFilterDto stageInvitationFilterDto);
}