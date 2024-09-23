package faang.school.projectservice.filter.stageInvitation;

import faang.school.projectservice.dto.client.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
class StageInvitationInvitedIdFilter implements Filter<StageInvitationFilterDto, StageInvitation> {

    @Override
    public boolean isApplicable(StageInvitationFilterDto stageInvitationFilterDto) {
        return stageInvitationFilterDto.getInvitedId() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitationStream,
                                         StageInvitationFilterDto stageInvitationFilterDto) {
        return stageInvitationStream.filter(stageInvitation -> stageInvitation.getInvited() != null &&
                Objects.equals(stageInvitation.getInvited()
                        .getId(), stageInvitationFilterDto.getInvitedId()));
    }
}
