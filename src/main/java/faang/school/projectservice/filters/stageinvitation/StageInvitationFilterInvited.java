package faang.school.projectservice.filters.stageinvitation;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StageInvitationFilterInvited implements StageInvitationFilter {
    @Override
    public boolean isApplicable(StageInvitationDto stageInvitationDto) {
        return stageInvitationDto.getInvited() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitationStream, StageInvitationDto stageInvitationDto) {
        return stageInvitationStream.filter(stageInvitation -> stageInvitation.getInvited().equals(stageInvitationDto.getInvited()));
    }
}
