package faang.school.projectservice.filter.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StageInvitationStageIdFilter implements StageInvitationFilter {
    @Override
    public boolean isApplicable(StageInvitationFilterDto filterDto) {
        return filterDto.getStageId() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitationStream, StageInvitationFilterDto filterDto) {

        return stageInvitationStream.filter(stageInvitation -> {
            var stage = stageInvitation.getStage();
            if (stage == null) {
                return false;
            }
            return stage.getStageId() == filterDto.getStageId();
        });
    }
}
