package faang.school.projectservice.service.stage_invitation.filter;

import faang.school.projectservice.dto.invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.stream.Stream;

public interface StageInvitationFilter {

    boolean isApplicable(StageInvitationFilterDto filter);

    Stream<StageInvitation> apply(Stream<StageInvitation> invitations, StageInvitationFilterDto filter);

}
