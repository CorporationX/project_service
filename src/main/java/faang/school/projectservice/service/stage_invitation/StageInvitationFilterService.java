package faang.school.projectservice.service.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.stream.Stream;

public interface StageInvitationFilterService {
    Stream<StageInvitation> applyAll(Stream<StageInvitation> stageInvitations, StageInvitationFilterDto filterDto);
}
