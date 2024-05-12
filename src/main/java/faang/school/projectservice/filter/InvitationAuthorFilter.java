package faang.school.projectservice.filter;

import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class InvitationAuthorFilter implements InvitationFilter {
    @Override
    public boolean isApplicable(InvitationFilterDto filterDto) {
        return Objects.nonNull(filterDto.getAuthor());
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitation, InvitationFilterDto filterDto) {
        return stageInvitation.filter(invitation -> invitation.getAuthor().equals(filterDto.getAuthor()));
    }
}
