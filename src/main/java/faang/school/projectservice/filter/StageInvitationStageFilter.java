package faang.school.projectservice.filter;

import faang.school.projectservice.dto.stage.invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StageInvitationStageFilter implements StageInvitationFilter {

    @Override
    public boolean isApplicable(StageInvitationFilterDto stageInvitationFilterDto) {
        return stageInvitationFilterDto.stageId() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> allStageInvitations,
                                         StageInvitationFilterDto stageInvitationFilterDto) {
        long stageId = stageInvitationFilterDto.stageId();
        return allStageInvitations
                .filter(stageInvitation -> stageInvitation.isStage(stageId));
    }
}
