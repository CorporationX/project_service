package faang.school.projectservice.filter.stageInvitation;

import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.stream.Stream;

public interface StageInvitationFilter {
    boolean isApplicable(StageInvitationFilterDto stageInvitationFilterDto);

    Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitationStream,
                                  StageInvitationFilterDto stageInvitationFilterDto);
}
