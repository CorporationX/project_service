package faang.school.projectservice.filter.StageInvitation.filterImpl;

import faang.school.projectservice.dto.client.stage.StageInvitationFilterDto;
import faang.school.projectservice.filter.StageInvitation.StageInvitationFilter;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
class StageInvitationNameFilter implements StageInvitationFilter {
    @Override
    public boolean isApplicable(StageInvitationFilterDto stageInvitationFilterDto) {
        return stageInvitationFilterDto.getInvitedStageName() != null &&
                !stageInvitationFilterDto.getInvitedStageName().isBlank();
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitationStream,
                                         StageInvitationFilterDto stageInvitationFilterDto) {
        return stageInvitationStream.filter(stageInvitation -> stageInvitation.getStage().getStageName() != null &&
                stageInvitation.getStage().getStageName()
                        .contains(stageInvitationFilterDto.getInvitedStageName()));
    }
}
