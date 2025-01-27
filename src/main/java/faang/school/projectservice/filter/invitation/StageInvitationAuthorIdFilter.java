package faang.school.projectservice.filter.invitation;

import faang.school.projectservice.dto.FilterDto.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StageInvitationAuthorIdFilter implements StageInvitationFilter {
    @Override
    public boolean isApplicable(StageInvitationFilterDto filter) {
        return filter.getAuthorId() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitations,
                                         StageInvitationFilterDto filter) {
        return stageInvitations
                .filter(stageInvitation -> filter.getAuthorId()
                        .equals(stageInvitation.getAuthor().getId()));
    }
}
