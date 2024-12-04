package faang.school.projectservice.filter;

import faang.school.projectservice.dto.stage.invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StageInvitationAuthorFilter implements StageInvitationFilter {

    @Override
    public boolean isApplicable(StageInvitationFilterDto stageInvitationFilterDto) {
        return stageInvitationFilterDto.authorId() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> allStageInvitations,
                                         StageInvitationFilterDto stageInvitationFilterDto) {
        long authorId = stageInvitationFilterDto.authorId();
        return allStageInvitations
                .filter(stageInvitation -> stageInvitation.isAuthor(authorId));
    }
}
