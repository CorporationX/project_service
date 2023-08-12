package faang.school.projectservice.filter.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StageInvitationAuthorIdFilter implements StageInvitationFilter {
    @Override
    public boolean isApplicable(StageInvitationFilterDto filterDto) {
        return filterDto.getAuthorId() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitationStream, StageInvitationFilterDto filterDto) {
        return stageInvitationStream.filter(stageInvitation -> {
            var author = stageInvitation.getAuthor();
            if (author == null) {
                return false;
            }
            return author.getId() == filterDto.getAuthorId();
        });
    }
}
