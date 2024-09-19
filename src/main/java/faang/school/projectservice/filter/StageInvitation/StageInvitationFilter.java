package faang.school.projectservice.filter.StageInvitation;

import faang.school.projectservice.dto.client.stage.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.stream.Stream;

public interface StageInvitationFilter {

    boolean isApplicable(StageInvitationFilterDto stageInvitationFilterDto);

    Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitationStream,
                                  StageInvitationFilterDto stageInvitationFilterDto);
}
