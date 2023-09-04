package faang.school.projectservice.filter.stageInvitation;

import faang.school.projectservice.dto.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StageInvitationAuthorFilter implements StageInvitationFilter{
    @Override
    public boolean isApplicable(StageInvitationFilterDto stageInvitationFilterDto) {
        return stageInvitationFilterDto.getAuthorIdPattern() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitationStream,
                                         StageInvitationFilterDto stageInvitationFilterDto) {
        return stageInvitationStream.filter(stageInvitation ->
                stageInvitation.getAuthor().getUserId().equals(stageInvitationFilterDto.getAuthorIdPattern()));
    }
}
