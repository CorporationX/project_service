package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.FilterInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.stream.Stream;

public class AuthorIdFilter implements Filter<StageInvitation, FilterInvitationDto> {

    @Override
    public boolean isApplicable(FilterInvitationDto filters) {
        return filters.getAuthorIdPattern() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> invitations, FilterInvitationDto filters) {
        return invitations.filter(invitation -> invitation.getAuthor().getId().equals(filters.getAuthorIdPattern()));
    }
}
