package faang.school.projectservice.filter.stageInvitation;

import faang.school.projectservice.dto.client.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
class StageInvitationNameFilter implements Filter<StageInvitationFilterDto, StageInvitation> {

    @Override
    public boolean isApplicable(StageInvitationFilterDto stageInvitationFilterDto) {
        return stageInvitationFilterDto.getStageName() != null &&
                !stageInvitationFilterDto.getStageName().isBlank();
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitationStream,
                                         StageInvitationFilterDto stageInvitationFilterDto) {
        return stageInvitationStream.filter(stageInvitation -> stageInvitation.getStage().getStageName() != null &&
                stageInvitation.getStage().getStageName()
                        .contains(stageInvitationFilterDto.getStageName()));
    }
}
