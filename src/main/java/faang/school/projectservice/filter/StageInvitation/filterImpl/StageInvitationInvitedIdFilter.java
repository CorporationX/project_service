package faang.school.projectservice.filter.StageInvitation.filterImpl;

import faang.school.projectservice.dto.client.stage.StageInvitationFilterDto;
import faang.school.projectservice.filter.StageInvitation.StageInvitationFilter;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.Objects;
import java.util.stream.Stream;

public class StageInvitationInvitedIdFilter implements StageInvitationFilter {

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
