package faang.school.projectservice.filters.stageinvitation;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.stream.Stream;

public class StageInvitationFilterStatus implements StageInvitationFilter {
    @Override
    public boolean isApplicable(StageInvitationDto stageInvitationDto) {
        return stageInvitationDto.getStatus() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitationStream, StageInvitationDto stageInvitationDto) {
        return stageInvitationStream.filter(stageInvitation -> stageInvitation.getStatus().equals(stageInvitationDto.getStatus()));
    }
}
