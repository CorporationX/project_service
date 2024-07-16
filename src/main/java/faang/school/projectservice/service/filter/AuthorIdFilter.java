package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.FilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.stream.Stream;

public class AuthorIdFilter implements Filter<StageInvitation, FilterDto> {

    @Override
    public boolean isApplicable(FilterDto filters) {
        return filters.getAuthorId() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> invitations, FilterDto filters) {
        return invitations.filter(invitation -> invitation.getAuthor().getId().equals(filters.getAuthorId()));
    }
}
